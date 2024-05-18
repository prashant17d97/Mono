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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.debugdesk.mono.R
import com.debugdesk.mono.presentation.uicomponents.CustomButton
import com.debugdesk.mono.presentation.uicomponents.ScreenView
import com.debugdesk.mono.presentation.uicomponents.SpacerHeight
import com.debugdesk.mono.presentation.uicomponents.SpacerWidth
import com.debugdesk.mono.utils.Dp.dp10
import com.debugdesk.mono.utils.Dp.dp20
import com.debugdesk.mono.utils.enums.Buttons
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.LazyListSnapperLayoutInfo
import dev.chrisbanes.snapper.rememberLazyListSnapperLayoutInfo
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun Reminder(
    navHostController: NavHostController,
    reminder: ReminderVM = koinViewModel()
) {
    var currentTimeSelected by rememberSaveable {
        mutableStateOf("")
    }
    var scrolling by rememberSaveable {
        mutableStateOf(true)
    }
    val hour = reminder.time[0].toInt()
    val minute = reminder.time[1].toInt()

    val initialTimeSelected = "${
        String.format("%02d", hour - (12.takeIf { hour > 12 }
            ?: 0))
    }:${
        String.format(
            "%02d",
            minute
        )
    } ${"PM".takeIf { hour > 12 && minute > 0 } ?: "AM"}"


    Log.e("Reminder", "Reminder: $initialTimeSelected === $currentTimeSelected")
    ScreenView(modifier = Modifier.fillMaxSize(),
        trailing = "Back",
        showBack = true,
        isScrollEnabled = false,
        trailingColor = Color.Transparent,
        heading = stringResource(id = R.string.reminder),
        onBackClick = {
            navHostController.popBackStack()
        }) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                ReminderCard(reminder.time) { currentTime, isScrolling ->
                    currentTimeSelected = currentTime
                    scrolling = isScrolling
                }
                SpacerHeight(value = dp10)
                Text(
                    text = stringResource(id = R.string.reminderCaption),
                    style = MaterialTheme.typography.titleSmall
                )

            }

            CustomButton(
                text = stringResource(id = R.string.setReminder.takeIf { initialTimeSelected != currentTimeSelected }
                    ?: R.string.removeReminder),
                modifier = Modifier.fillMaxWidth(),
                status = Buttons.Disable.takeIf { scrolling }
                    ?: (Buttons.Inactive.takeIf { initialTimeSelected == currentTimeSelected }
                        ?: Buttons.Active)
            ) {
                when (it) {
                    Buttons.Active -> reminder.toast(null, message = currentTimeSelected)
                    Buttons.Inactive -> reminder.toast(null, message = currentTimeSelected)
                    Buttons.Disable -> {

                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalSnapperApi::class)
@Composable
fun ReminderCard(
    time: List<String>, ignoredCurrentTime: (currentTime: String, isScrolling: Boolean) -> Unit
) {
    val hours = stringArrayResource(id = R.array.hours_12)
    val minutes = stringArrayResource(id = R.array.minutes)
    val format = stringArrayResource(id = R.array.format)
    val density = LocalDensity.current
    val itemHeight = 80 // Adjust this value to change the height of each item
    val hourState = rememberLazyListState()
    val minuteState = rememberLazyListState()
    val formatState = rememberLazyListState()
    val hourInfo: LazyListSnapperLayoutInfo = rememberLazyListSnapperLayoutInfo(hourState)
    val minuteInfo: LazyListSnapperLayoutInfo = rememberLazyListSnapperLayoutInfo(minuteState)
    val formatInfo: LazyListSnapperLayoutInfo = rememberLazyListSnapperLayoutInfo(formatState)



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

        val hour = time[0].toInt()
        val minute = time[1].toInt()
        delay(600)
        hourState.animateScrollBy(value = with(density) { (itemHeight.dp).toPx() } * (hour - (13.takeIf { hour > 12 }
            ?: 1)), animationSpec = tween(1000)
        )
        minuteState.animateScrollBy(value = with(density) { (itemHeight.dp).toPx() } * (minute),
            animationSpec = tween(2000.takeIf { minute > 5 } ?: 100))
        if (hour > 12 && minute > 0) {
            formatState.animateScrollBy(value = with(density) { (itemHeight.dp).toPx() } * 1,
                animationSpec = tween(500))
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        LazyColumn(
            state = hourState,
            flingBehavior = rememberSnapperFlingBehavior(hourState),
            modifier = Modifier
                .height((itemHeight).dp)
                .weight(1f)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(20.dp)
                )
        ) {
            itemsIndexed(hours) { _, item ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight.dp)
                        .padding(horizontal = 30.dp), contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
        }
        Text(
            text = ":",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(horizontal = 10.dp)
        )
        LazyColumn(
            state = minuteState,
            flingBehavior = rememberSnapperFlingBehavior(minuteState),
            modifier = Modifier
                .height((itemHeight).dp)
                .weight(1f)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(20.dp)
                )
        ) {
            itemsIndexed(minutes) { _, item ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier
                    )
                }
            }
        }

        SpacerWidth(value = dp20)
        LazyColumn(
            state = formatState,
            flingBehavior = rememberSnapperFlingBehavior(formatState),
            modifier = Modifier
                .weight(1f)
                .height((itemHeight).dp)
        ) {
            itemsIndexed(format) { _, item ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight.dp)
                        .padding(horizontal = 30.dp), contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier

                    )
                }
            }
        }
    }
    ignoredCurrentTime(
        "${hours[hourInfo.currentItem?.index ?: 0]}:${minutes[minuteInfo.currentItem?.index ?: 0]} ${format[formatInfo.currentItem?.index ?: 0]}",
        when {
            hourState.isScrollInProgress -> true
            minuteState.isScrollInProgress -> true
            formatState.isScrollInProgress -> true
            else -> false
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun ReminderPreview() = Reminder(rememberNavController())