package com.debugdesk.mono.presentation.calendar

sealed class CalendarIntent {
    data class OnCalendarUpdate(val calendarState: CalendarState) : CalendarIntent()
    data class OnDateSelected(val calendarState: CalendarState) : CalendarIntent() {
        val date = calendarState.selectedDate
    }

    data class OnTransactionClick(val transactionId: Int) : CalendarIntent()
    data object NavigateBack : CalendarIntent()
}