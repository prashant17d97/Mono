package com.debugdesk.mono.presentation.report

import com.debugdesk.mono.domain.data.local.localdatabase.model.DailyTransaction

sealed class ReportIntent {
    data object ResetClick :
        ReportIntent()

    data object RightClick : ReportIntent()
    data object LeftClick : ReportIntent()
    data class ExpandCalendar(val isCalendarExpanded: Boolean = false) : ReportIntent()
    data class UpdateTab(val currentIndex: Int = 0) : ReportIntent()
    data class UpdateSelectedDate(val timeStamp: Long) : ReportIntent()
    data class UpdateFilter(
        val filter: Filter = Filter.values.first(),
        val filterRange: FilterRange,
        val dateRange: Pair<Long, Long>? = null
    ) : ReportIntent()

    data class OnTransactionClick(val transaction: DailyTransaction) : ReportIntent()
    data class EditTransaction(val transactionId: Int) : ReportIntent()
    data object DeleteTransaction : ReportIntent()
    data object CloseTransactionCard : ReportIntent()
    data class ChangeReportView(val reportView: ReportView) : ReportIntent()
}


enum class FilterRange(val range: Int) {
    THIS_MONTH(0),
    LAST_MONTH(1),
    LAST_THREE_MONTH(3),
    LAST_SIX_MONTH(6),
    CUSTOM_RANGE(0)
}