package com.debugdesk.mono.presentation.calendar

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.debugdesk.mono.R
import com.debugdesk.mono.domain.data.local.localdatabase.model.listOfPreviewTransaction
import com.debugdesk.mono.presentation.uicomponents.DropDown
import com.debugdesk.mono.presentation.uicomponents.ExpenseCard
import com.debugdesk.mono.presentation.uicomponents.MonoColumn
import com.debugdesk.mono.presentation.uicomponents.NoDataFoundLayout
import com.debugdesk.mono.presentation.uicomponents.PreviewTheme
import com.debugdesk.mono.utils.CommonColor.brandColor
import com.debugdesk.mono.utils.CommonColor.inActiveButton
import com.debugdesk.mono.utils.Dp.dp0
import com.debugdesk.mono.utils.Dp.dp1
import com.debugdesk.mono.utils.Dp.dp10
import com.debugdesk.mono.utils.Dp.dp12
import com.debugdesk.mono.utils.Dp.dp120
import com.debugdesk.mono.utils.Dp.dp16
import com.debugdesk.mono.utils.Dp.dp2
import com.debugdesk.mono.utils.Dp.dp4
import com.debugdesk.mono.utils.Dp.dp40
import com.debugdesk.mono.utils.Dp.dp48
import com.debugdesk.mono.utils.Dp.dp50
import com.debugdesk.mono.utils.Dp.dp55
import com.debugdesk.mono.utils.Dp.dp6
import com.debugdesk.mono.utils.Dp.dp80
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.getDayOfWeekName
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.toIntIfEmpty
import org.koin.androidx.compose.koinViewModel
import java.util.Calendar

@Composable
fun CalendarPage(
    calendarPageVM: CalendarPageVM = koinViewModel(),
    navHostController: NavHostController
) {

    LaunchedEffect(key1 = Unit) {
        calendarPageVM.fetchTransactions()
    }
    val calendarState by calendarPageVM.calendarState.collectAsState()
    LaunchedEffect(key1 = calendarState) {
        Log.e("TAG", "CalendarPage: ")
    }
    CalendarPageContainer(
        calendar = calendarState,
        onSelected = {
            calendarPageVM.onCalendarIntentHandle(
                calendarIntent = it,
                navHostController = navHostController
            )
        }
    )
}


@Composable
private fun CalendarPageContainer(
    calendar: CalendarState,
    onSelected: (CalendarIntent) -> Unit
) {
    MonoColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        top = dp0,
        start = dp10,
        end = dp10,
        bottom = dp10,
        isScrollEnabled = false,
        header = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(id = R.string.back),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Light),
                    modifier = Modifier
                        .clickable { onSelected(CalendarIntent.NavigateBack) }
                        .padding(horizontal = 2.dp))
                YearDropdown(
                    modifier = Modifier.weight(1f),
                    calendarState = calendar,
                    onYearSelected = { onSelected(CalendarIntent.OnCalendarUpdate(it)) }
                )

                MonthDropDown(
                    modifier = Modifier.padding(horizontal = dp4),
                    calendarState = calendar,
                    onSelected = { onSelected(CalendarIntent.OnCalendarUpdate(it)) }
                )

            }
        },
        modifier = Modifier.fillMaxSize()
    ) {
        CalendarGrid(
            calendarState = calendar,
            onSelected = { onSelected(CalendarIntent.OnDateSelected(it)) },
            onTransactionClick = {
                onSelected(CalendarIntent.OnTransactionClick(it))
            }
        )
    }
}

@Composable
private fun WeekBox(
    modifier: Modifier = Modifier, week: String, shape: RoundedCornerShape
) {
    val selectedWeekDay by rememberUpdatedState(newValue = week == getDayOfWeekName())
    val containerColor by animateColorAsState(
        targetValue = if (selectedWeekDay) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.primaryContainer, label = "ContainerColor"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(width = dp50, height = dp40)
            .background(color = containerColor, shape = shape)

    ) {
        Text(
            text = week, color = contentColorFor(containerColor)
        )
    }
}

@Composable
private fun DateBox(
    date: String,
    calendarState: CalendarState,
    modifier: Modifier = Modifier,
    height: Dp = dp55,
    padding: Dp = dp2,
    shape: Shape = RoundedCornerShape(dp4),
    onSelected: (String) -> Unit = {}
) {
    val (expense, income) = rememberUpdatedState(calendarState.dayTransaction(date)).value
    val outlineColor by animateColorAsState(
        targetValue = if (calendarState.isSelected(date)) brandColor
        else Color.Transparent, label = "ContainerColor"
    )
    val containerColor by rememberUpdatedState(
        newValue = calendarState.currentDateContainerColor(
            date
        )
    )

    Box(contentAlignment = Alignment.Center,
        modifier = modifier
            .padding(padding)
            .size(width = dp50, height = height)
            .border(width = dp1, color = outlineColor, shape = shape)
            .background(
                color = containerColor.copy(alpha = 0.75f),
                shape = shape
            )
            .clickable { onSelected(date) }) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(
                space = dp6,
                alignment = Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = date, color = contentColorFor(containerColor)
            )
            if (date.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        dp6,
                        alignment = Alignment.CenterHorizontally
                    )
                ) {

                    // Income
                    DotBox(
                        color = brandColor, isAvailable = income
                    )

                    // Expense
                    DotBox(
                        color = inActiveButton, isAvailable = expense
                    )
                }
            }
        }
    }
}

@Composable
fun DotBox(
    modifier: Modifier = Modifier,
    color: Color = inActiveButton, isAvailable: Boolean = false
) {
    val animateColor by animateColorAsState(
        targetValue = if (isAvailable) color else Color.Transparent,
        label = "Dot Box"
    )
    Box(
        modifier = modifier
            .size(dp12)
            .background(animateColor, shape = CircleShape)
    )
}

@Composable
fun MonthDropDown(
    modifier: Modifier = Modifier,
    calendarState: CalendarState,
    onSelected: (CalendarState) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    DropDown(
        selectedValue = calendarState.monthString,
        items = calendarState.months,
        expanded = expanded,
        onSelected = { _, index ->
            onSelected(calendarState.copy(selectedMonth = index, showCategoryList = false))
        },
        onExpend = { expanded = it },
        heading = {
            Icon(
                painter = painterResource(id = R.drawable.ic_caret_down),
                contentDescription = "",
                modifier = modifier.clickable { expanded = !expanded }
            )
        }
    )
}

@Composable
fun YearDropdown(
    modifier: Modifier = Modifier,
    calendarState: CalendarState,
    onYearSelected: (CalendarState) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val years = calendarState.yearRange.map { it.toString() }
    Box(
        modifier = modifier.height(dp48),
        contentAlignment = Alignment.Center,
    ) {
        DropDown(expanded = expanded,
            selectedValue = calendarState.selectedYear.toString(),
            items = years,
            onSelected = { item, _ ->
                onYearSelected(
                    calendarState.copy(
                        selectedYear = item.toIntIfEmpty(),
                        showCategoryList = false
                    )
                )
            },
            onExpend = { expanded = it },
            heading = {
                Text(
                    text = "${calendarState.monthString}, ${calendarState.selectedYear}",
                    modifier = Modifier
                        .clickable { expanded = !expanded },
                    style = MaterialTheme.typography.titleMedium
                )

            }
        )
    }
}


@Composable
private fun CalendarGrid(
    calendarState: CalendarState,
    onSelected: (CalendarState) -> Unit,
    onTransactionClick: (transactionId: Int) -> Unit = {}
) {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, calendarState.selectedYear)
        set(Calendar.MONTH, calendarState.selectedMonth + 1)
        set(Calendar.DAY_OF_MONTH, 1)
    }

    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val startDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .padding(dp2)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(dp4)
                )
        ) {
            stringArrayResource(id = R.array.weeks).forEach { day ->
                WeekBox(
                    week = day, modifier = Modifier.weight(1f), shape = RoundedCornerShape(dp4)
                )
            }
        }

        val totalCells = daysInMonth + startDayOfWeek - 1
        val rows = (totalCells / 7) + if (totalCells % 7 != 0) 1 else 0

        for (i in 0 until rows) {
            Row {
                for (j in 0 until 7) {
                    val day = i * 7 + j - (startDayOfWeek - 2)
                    val date = if (day in 1..daysInMonth) day.toString() else ""
                    DateBox(
                        date = date,
                        calendarState = calendarState,
                        modifier = Modifier.weight(1f),
                        onSelected = {
                            if (it.isNotEmpty()) {
                                onSelected(
                                    calendarState.copy(
                                        selectedDate = it.toIntIfEmpty(),
                                        showCategoryList = true
                                    )
                                )
                            }
                        }
                    )
                }
            }
        }

        NoDataFoundLayout(
            show = calendarState.isTransactionEmpty,
            text = R.string.noTransactionFound,
            imageSize = dp120,
            modifier = Modifier.padding(top = dp80),
            content = {
                Column(modifier = Modifier.padding(top = dp16)) {
                    calendarState.sortedTransaction.forEach { (_, dailyTransaction) ->
                        ExpenseCard(currency = stringResource(calendarState.currencyIcon),
                            dailyTransaction = dailyTransaction,
                            onTap = { onTransactionClick(it.transactionId) })
                    }
                }
            })
    }
}

@Preview
@Composable
private fun CalendarPagePrev() {
    PreviewTheme {
        CalendarPageContainer(
            CalendarState(
                transaction = listOfPreviewTransaction
            ),
            onSelected = {}
        )
    }
}
