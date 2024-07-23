package com.debugdesk.mono.presentation.setting.reminder

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.debugdesk.mono.R
import com.debugdesk.mono.presentation.uicomponents.CustomButton
import com.debugdesk.mono.presentation.uicomponents.MonoColumn
import com.debugdesk.mono.presentation.uicomponents.PreviewTheme
import com.debugdesk.mono.presentation.uicomponents.SpacerHeight
import com.debugdesk.mono.presentation.uicomponents.SpacerWidth
import com.debugdesk.mono.utils.Dp.dp10
import com.debugdesk.mono.utils.Dp.dp20
import com.debugdesk.mono.utils.enums.Buttons
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun Reminder(
    navHostController: NavHostController,
    viewModel: ReminderVM = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    viewModel.fetchAppInitialData()

    val format = stringArrayResource(id = R.array.format)
    val hourState = rememberLazyListState()
    val minuteState = rememberLazyListState()
    val formatState = rememberLazyListState()
    val hr = remember { derivedStateOf { hourState.firstVisibleItemIndex } }.value
    val selectedMinute = remember { derivedStateOf { minuteState.firstVisibleItemIndex } }.value
    val formatA = remember { derivedStateOf { formatState.firstVisibleItemIndex } }.value

    LaunchedEffect(
        hourState.isScrollInProgress,
        minuteState.isScrollInProgress,
        formatState.isScrollInProgress,
    ) {
        Log.d(
            "Reminder",
            "Reminder: ${hourState.isScrollInProgress}, ${minuteState.isScrollInProgress}, ${formatState.isScrollInProgress}",
        )
        viewModel.handleEvent(
            ReminderEvent.Scrolling(hourState.isScrollInProgress || minuteState.isScrollInProgress || formatState.isScrollInProgress),
            context = context,
        )
        if (!hourState.isScrollInProgress || !minuteState.isScrollInProgress || !formatState.isScrollInProgress) {
            Log.d("Reminder", "Reminder: inIf ")
            viewModel.handleEvent(
                ReminderEvent.UpdateTime(
                    hour = hr,
                    minute = selectedMinute,
                    isPM = format[formatA] == context.getString(R.string.PM),
                ),
                context = context,
            )
        }
    }
    ReminderView(
        state = state,
        hourState = hourState,
        minuteState = minuteState,
        formatState = formatState,
        onEvent = { event -> viewModel.handleEvent(event, context = context) },
        navHostController = navHostController,
    )
}

@Composable
fun ReminderView(
    state: ReminderState,
    hourState: LazyListState = rememberLazyListState(),
    minuteState: LazyListState = rememberLazyListState(),
    formatState: LazyListState = rememberLazyListState(),
    onEvent: (ReminderEvent) -> Unit,
    navHostController: NavHostController,
) {
    MonoColumn(
        modifier = Modifier.fillMaxSize(),
        trailing = stringResource(id = R.string.back),
        showBack = true,
        isScrollEnabled = false,
        trailingColor = Color.Transparent,
        heading = stringResource(id = R.string.reminder),
        onBackClick = { navHostController.navigateUp() },
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f),
        ) {
            ReminderCard(
                hour = state.hour,
                minute = state.minute,
                hourState = hourState,
                minuteState = minuteState,
                formatState = formatState,
            )
            SpacerHeight(value = dp10)
            Text(
                text = stringResource(id = R.string.reminderCaption),
                style = MaterialTheme.typography.labelLarge,
            )
        }

        CustomButton(
            text = stringResource(id = state.buttonString),
            modifier = Modifier.fillMaxWidth(),
            status = state.buttonState,
        ) {
            when (it) {
                Buttons.Active -> onEvent(ReminderEvent.SetReminder)
                Buttons.Inactive -> onEvent(ReminderEvent.RemoveReminder)
                Buttons.Disable -> {}
            }
        }
    }
}

@OptIn(ExperimentalSnapperApi::class)
@Composable
fun ReminderCard(
    hour: Int,
    minute: Int,
    hourState: LazyListState = rememberLazyListState(),
    minuteState: LazyListState = rememberLazyListState(),
    formatState: LazyListState = rememberLazyListState(),
) {
    val hours = stringArrayResource(id = R.array.hours_12)
    val minutes = stringArrayResource(id = R.array.minutes)
    val format = stringArrayResource(id = R.array.format)
    val density = LocalDensity.current
    val itemHeight = 80 // Adjust this value to change the height of each item

    LaunchedEffect(Unit) {
        val centerIndex = hourState.layoutInfo.visibleItemsInfo.size / 2
        val centerItem = hourState.layoutInfo.visibleItemsInfo[centerIndex].index
        if (centerItem != hourState.firstVisibleItemIndex) {
            hourState.scrollToItem(centerItem, scrollOffset = itemHeight * centerIndex)
        }

        val centerIndexMinute = minuteState.layoutInfo.visibleItemsInfo.size / 2
        val centerItemMinute = minuteState.layoutInfo.visibleItemsInfo[centerIndexMinute].index
        if (centerItemMinute != minuteState.firstVisibleItemIndex) {
            minuteState.scrollToItem(centerItem, scrollOffset = itemHeight * centerIndexMinute)
        }

        val centerIndexFormat = formatState.layoutInfo.visibleItemsInfo.size / 2
        val centerItemFormat = formatState.layoutInfo.visibleItemsInfo[centerIndexFormat].index
        if (centerItemFormat != formatState.firstVisibleItemIndex) {
            formatState.scrollToItem(centerItem, scrollOffset = itemHeight * centerIndexFormat)
        }
        delay(600)
        hourState.animateScrollBy(
            value =
            with(density) { (itemHeight.dp).toPx() } * (
                hour - (
                    13.takeIf { hour > 12 }
                        ?: 1
                    )
                ),
            animationSpec = tween(1000),
        )
        minuteState.animateScrollBy(
            value = with(density) { (itemHeight.dp).toPx() } * (minute),
            animationSpec = tween(2000.takeIf { minute > 5 } ?: 100),
        )
        if (hour > 12 && minute >= 0) {
            formatState.animateScrollBy(
                value = with(density) { (itemHeight.dp).toPx() } * 1,
                animationSpec = tween(500),
            )
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        LazyColumn(
            state = hourState,
            flingBehavior = rememberSnapperFlingBehavior(hourState),
            modifier =
            Modifier
                .height((itemHeight).dp)
                .weight(1f)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(20.dp),
                ),
        ) {
            itemsIndexed(hours) { _, item ->
                Box(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(itemHeight.dp)
                        .padding(horizontal = 30.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = item,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineMedium,
                    )
                }
            }
        }
        Text(
            text = ":",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(horizontal = 10.dp),
        )
        LazyColumn(
            state = minuteState,
            flingBehavior = rememberSnapperFlingBehavior(minuteState),
            modifier =
            Modifier
                .height((itemHeight).dp)
                .weight(1f)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(20.dp),
                ),
        ) {
            itemsIndexed(minutes) { _, item ->
                Box(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(itemHeight.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = item,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier,
                    )
                }
            }
        }

        SpacerWidth(value = dp20)
        LazyColumn(
            state = formatState,
            flingBehavior = rememberSnapperFlingBehavior(formatState),
            modifier =
            Modifier
                .weight(1f)
                .height((itemHeight).dp),
        ) {
            itemsIndexed(format) { _, item ->
                Box(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(itemHeight.dp)
                        .padding(horizontal = 30.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = item,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier,
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun ReminderPreview() {
    PreviewTheme {
        ReminderCard(
            hour = 2,
            minute = 9,
        )
    }
}
