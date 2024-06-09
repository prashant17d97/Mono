package com.debugdesk.mono.presentation.report

import androidx.annotation.StringRes
import com.debugdesk.mono.R
import com.debugdesk.mono.domain.data.local.localdatabase.model.CategoryModel
import com.debugdesk.mono.domain.data.local.localdatabase.model.DailyTransaction
import com.debugdesk.mono.domain.data.local.localdatabase.model.emptyTransaction
import com.debugdesk.mono.model.Tabs
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.distributeTransactionsByDate
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.filterExpenseType
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.filterIncomeType
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.getCurrentMonthYear
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.getExpenseAmount
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.getIncomeAmount
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.getTotalAmount

data class ReportState(
    val selectedDate: Pair<Int, Int> = getCurrentMonthYear(),
    val monthString: String = CommonFunctions.getMonthName(selectedDate.first),
    val filterString: String? = null,
    val month: Int = selectedDate.second,
    val year: Int = selectedDate.second,
    val totalAmount: Double = 0.0,
    val allDailyMonthTransaction: List<DailyTransaction> = emptyList(),
    val transaction: List<DailyTransaction> = allDailyMonthTransaction,
    val currentMonthExpense: Double = allDailyMonthTransaction.getExpenseAmount(),
    val currentMonthIncome: Double = allDailyMonthTransaction.getIncomeAmount(),
    val currentMonthAvailableBalance: Double = allDailyMonthTransaction.getTotalAmount(),
    val filters: List<Filter> = Filter.values.toList(),
    val currency: Int = R.string.inrIcon,
    val showRest: Boolean = false,
    val isCalendarExpanded: Boolean = false,
    val tabs: List<Tabs> = Tabs.values,
    val showTransactionCard: Boolean = false,
    val showClickedTransaction: DailyTransaction = emptyTransaction,
    val categories: List<CategoryModel> = emptyList(),
    val reportView: List<ReportView> = ReportView.entries,
    val selectedReportView: Int = ReportView.MonthlyReport.stringValue
) {
    val distributedTransaction = transaction.distributeTransactionsByDate()
    val isTransactionEmpty = distributedTransaction.isEmpty()

    val calendarTitleString = filterString ?: "$monthString, $year"

    val expenseCategory = categories.filter { it.categoryType?.filterExpenseType ?: false }
    val incomeCategory = categories.filter { it.categoryType?.filterIncomeType ?: false }
}

enum class ReportView(@StringRes val stringValue: Int) {
    MonthlyReport(stringValue = R.string.month_report),
    CategoryReport(stringValue = R.string.category_report),
    CalendarReport(stringValue = R.string.calender_report)
}
