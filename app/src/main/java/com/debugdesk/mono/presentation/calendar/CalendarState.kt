package com.debugdesk.mono.presentation.calendar

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import com.debugdesk.mono.R
import com.debugdesk.mono.domain.data.local.localdatabase.model.DailyTransaction
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.distributeTransactionsByDate
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.getCurrentDate
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.getCurrentMonth
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.getCurrentYear
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.getDateOfMonthFromTimestamp
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.toIntIfEmpty
import com.debugdesk.mono.utils.enums.ExpenseType

data class CalendarState(
    val selectedDate: Int = getCurrentDate,
    val selectedMonth: Int = getCurrentMonth,
    val selectedYear: Int = getCurrentYear,
    val yearRange: IntRange = (2016..getCurrentYear),
    val showCategoryList: Boolean = true,
    val transaction: List<DailyTransaction> = emptyList(),
    @StringRes
    val currencyStringIcon: Int = R.string.inrIcon
) {


    val dayTransaction = { day: String ->
        Pair(
            day.getTransaction(ExpenseType.Expense)?.type == ExpenseType.Expense.name,
            day.getTransaction(ExpenseType.Income)?.type == ExpenseType.Income.name
        )
    }

    val isSelected =
        { day: String ->
            day.toIntIfEmpty() != getCurrentDate
                    && day.toIntIfEmpty() == selectedDate
                    && selectedDate != 0
        }


    val sortedTransaction: Map<Int, List<DailyTransaction>> get() = transaction.distributeTransactionsByDate()
    val isTransactionEmpty:Boolean get() = transaction.isEmpty()
    val monthString: String
        @Composable
        get() {
            val months = stringArrayResource(id = R.array.months)
            return months[selectedMonth]
        }

    @Composable
    fun currentDateContainerColor(date: String): Color {
        return animateColorAsState(
            targetValue = if (date.toIntIfEmpty() == getCurrentDate) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.primaryContainer, label = "ContainerColor"
        ).value
    }

    val months: List<String>
        @Composable
        get() {
            return stringArrayResource(id = R.array.months).toList()
        }

    private fun String.getTransaction(type: ExpenseType): DailyTransaction? {
        val transaction = transaction.find {
            it.currentMonthId == selectedMonth
                    && it.year == selectedYear
                    && it.type == type.name
                    && getDateOfMonthFromTimestamp(it.date) == toIntIfEmpty()
        }
        Log.e(
            "CalendarState",
            ": ${transaction?.type},  $this, ${transaction?.currentMonthId}, $selectedMonth"
        )
        return transaction
    }
}
