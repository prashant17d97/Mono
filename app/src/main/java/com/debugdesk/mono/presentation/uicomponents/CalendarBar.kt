package com.debugdesk.mono.presentation.uicomponents

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.debugdesk.mono.R
import com.debugdesk.mono.presentation.report.Filter
import com.debugdesk.mono.presentation.report.FilterRange
import com.debugdesk.mono.presentation.report.ReportIntent
import com.debugdesk.mono.presentation.report.ReportState
import com.debugdesk.mono.utils.Dp
import com.debugdesk.mono.utils.Dp.dp10
import com.debugdesk.mono.utils.Dp.dp2
import com.debugdesk.mono.utils.Dp.dp40
import com.debugdesk.mono.utils.Dp.dp48
import com.debugdesk.mono.utils.Dp.dp6
import com.debugdesk.mono.utils.Dp.dp8
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.toDate

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CalendarBar(
    modifier: Modifier = Modifier,
    reportState: ReportState,
    onIntentChange: (ReportIntent) -> Unit = {},
) {
    var showCustomRangeDialog by remember { mutableStateOf(false) }

    Column(
        modifier =
        modifier
            .fillMaxWidth()
            .then(
                if (reportState.isCalendarExpanded) {
                    Modifier
                } else {
                    Modifier
                        .height(40.dp)
                },
            )
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape =
                RoundedCornerShape(
                    bottomEnd = Dp.dp20,
                    bottomStart = Dp.dp20,
                    topEnd = Dp.dp20,
                    topStart = Dp.dp20,
                ),
            )
            .padding(vertical = dp6, horizontal = dp2),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AnimatedVisibility(visible = reportState.isCalendarExpanded) {
            FlowRow(
                modifier =
                Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(dp6, Alignment.CenterVertically),
                horizontalArrangement = Arrangement.spacedBy(dp6, Alignment.CenterHorizontally),
            ) {
                reportState.filters.forEach { filter ->
                    Text(
                        text = stringResource(id = filter.title),
                        style = MaterialTheme.typography.titleMedium,
                        modifier =
                        Modifier
                            .background(
                                color =
                                if (filter.isSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.secondaryContainer
                                },
                                shape = RoundedCornerShape(50),
                            )
                            .padding(dp8)
                            .clickable {
                                when (filter.filterRange) {
                                    FilterRange.CUSTOM_RANGE -> {
                                        showCustomRangeDialog = true
                                    }
                                    FilterRange.THIS_MONTH -> {
                                        onIntentChange(ReportIntent.ResetClick)
                                    }
                                    else -> {
                                        onIntentChange(
                                            ReportIntent.UpdateFilter(
                                                filter = filter,
                                                filterRange = filter.filterRange,
                                                dateRange = null,
                                            ),
                                        )
                                    }
                                }

                                onIntentChange(ReportIntent.ExpandCalendar(isCalendarExpanded = false))
                            },
                    )
                }
            }
        }

        AnimatedVisibility(visible = !reportState.isCalendarExpanded) {
            Row(
                modifier =
                modifier
                    .fillMaxWidth()
                    .height(dp48),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_left_arrow),
                    contentDescription = "Left",
                    modifier =
                    Modifier
                        .size(dp40)
                        .clickable { onIntentChange(ReportIntent.LeftClick) },
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier =
                    Modifier
                        .fillMaxHeight()
                        .clickable { onIntentChange(ReportIntent.ExpandCalendar(isCalendarExpanded = true)) },
                ) {
                    Text(
                        text = reportState.calendarTitleString,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Icon(
                        imageVector = Icons.Rounded.ArrowDropDown,
                        contentDescription = "",
                        modifier = Modifier.size(dp40),
                    )
                }
                Row {
                    AnimatedVisibility(
                        visible = reportState.showRest,
                        enter = fadeIn() + slideInHorizontally { it },
                        exit = fadeOut() + slideOutHorizontally { it },
                    ) {
                        IconButton(
                            onClick = { onIntentChange(ReportIntent.ResetClick) },
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Refresh,
                                contentDescription = "Reset",
                                modifier = Modifier.size(dp40),
                            )
                        }
                    }
                    Icon(
                        painter = painterResource(id = R.drawable.ic_right_arrow),
                        contentDescription = "Right",
                        modifier =
                        Modifier
                            .size(dp40)
                            .clickable { onIntentChange(ReportIntent.RightClick) },
                    )
                }
            }
        }
    }
    AnimatedVisibility(visible = showCustomRangeDialog) {
        PopUp(dismiss = { showCustomRangeDialog = !showCustomRangeDialog }) {
            CustomDateRange(
                onSubmit = {
                    onIntentChange(
                        ReportIntent.UpdateFilter(
                            filter =
                            Filter(
                                title = R.string.custom_range,
                                isSelected = true,
                                filterRange = FilterRange.CUSTOM_RANGE,
                            ),
                            filterRange = FilterRange.CUSTOM_RANGE,
                            dateRange = it,
                        ),
                    )
                    showCustomRangeDialog = !showCustomRangeDialog
                    Log.e("Calendar", "CalendarBar: $it")
                },
                onCancel = { showCustomRangeDialog = !showCustomRangeDialog },
            )
        }
    }
}

@Composable
fun CustomDateRange(
    onSubmit: (Pair<Long, Long>) -> Unit = {},
    onCancel: (Boolean) -> Unit = {},
) {
    var startDate: Long? by remember { mutableStateOf(null) }
    var endDate: Long? by remember { mutableStateOf(null) }
    Column(
        modifier =
        Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape =
                RoundedCornerShape(
                    bottomEnd = Dp.dp20,
                    bottomStart = Dp.dp20,
                    topEnd = Dp.dp20,
                    topStart = Dp.dp20,
                ),
            )
            .padding(dp10),
        verticalArrangement =
        Arrangement.spacedBy(
            space = dp10,
            alignment = Alignment.CenterVertically,
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = stringResource(id = R.string.select_date_range))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
            Arrangement.spacedBy(
                dp10,
                alignment = Alignment.CenterHorizontally,
            ),
        ) {
            DateText(
                modifier = Modifier.weight(1f),
                text = startDate?.toDate() ?: stringResource(id = R.string.start),
            ) { startDate = it }
            DateText(
                modifier = Modifier.weight(1f),
                text = endDate?.toDate() ?: stringResource(id = R.string.end),
            ) { endDate = it }
        }

        SpacerHeight(value = dp10)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
            Arrangement.spacedBy(
                dp10,
                alignment = Alignment.End,
            ),
        ) {
            Button(onClick = { onCancel(false) }) {
                Text(text = stringResource(id = R.string.cancel))
            }
            Button(onClick = { onSubmit(Pair(startDate ?: 0, endDate ?: 0)) }) {
                Text(text = stringResource(id = R.string.okay))
            }
        }
    }
}

@Composable
fun DateText(
    modifier: Modifier = Modifier,
    text: String,
    initialDateInMillis: Long? = System.currentTimeMillis(),
    selectedDate: (Long) -> Unit = {},
) {
    var showDateDialog by remember { mutableStateOf(false) }
    Box(
        modifier =
        modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape =
                RoundedCornerShape(
                    bottomEnd = Dp.dp20,
                    bottomStart = Dp.dp20,
                    topEnd = Dp.dp20,
                    topStart = Dp.dp20,
                ),
            )
            .padding(dp10)
            .clickable {
                showDateDialog = !showDateDialog
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(text = text)
    }

    DatePickerDialog(
        openDialog = showDateDialog,
        initial = initialDateInMillis ?: 0,
        openDialogChange = { showDateDialog = it },
        value = selectedDate,
    )
}

@Preview
@Composable
fun CalendarPrev() {
    PreviewTheme {
        CalendarBar(
            reportState = ReportState(),
        )
    }
}

@Preview
@Composable
fun CalendarPrev2() {
    PreviewTheme {
        CalendarBar(
            reportState = ReportState(isCalendarExpanded = true),
        )
    }
}

@Preview
@Composable
fun CustomDateRangePrev() {
    PreviewTheme {
        CustomDateRange()
    }
}
