package com.debugdesk.mono.presentation.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.debugdesk.mono.domain.repo.Repository
import com.debugdesk.mono.navigation.Screens
import com.debugdesk.mono.ui.appconfig.AppConfigManager
import com.debugdesk.mono.utils.NavigationFunctions.navigateTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class CalendarPageVM(
    private val repository: Repository, private val appConfigManager: AppConfigManager
) : ViewModel(), KoinComponent {
    private val _calendarState: MutableStateFlow<CalendarState> = MutableStateFlow(CalendarState())
    val calendarState: StateFlow<CalendarState> = _calendarState

    companion object {
        private const val TAG = "CalendarPageVM"
    }

    init {
        observeTransactionChanges()
    }

    private fun observeTransactionChanges() {
        viewModelScope.launch {
            combine(
                repository.allDailyMonthTransaction,
                appConfigManager.appConfigProperties
            ) { transaction, appConfig ->
                val currentState = calendarState.value
                CalendarState(
                    selectedDate = currentState.selectedDate,
                    selectedMonth = currentState.selectedMonth,
                    selectedYear = currentState.selectedYear,
                    yearRange = currentState.yearRange,
                    showCategoryList = currentState.showCategoryList,
                    transaction = transaction,
                    currencyStringIcon = appConfig.selectedCurrencyIconString
                )
            }.collect { transactions ->
                _calendarState.tryEmit(transactions)
            }
        }
    }


    fun fetchTransactions() {
        viewModelScope.launch(Dispatchers.IO) {
            _calendarState.tryEmit(calendarState.value.copy(yearRange = repository.getYearRange()))
            repository.getAllTransactionByMonth(
                calendarState.value.selectedMonth, calendarState.value.selectedYear
            )
        }
    }

    fun onCalendarIntentHandle(
        calendarIntent: CalendarIntent, navHostController: NavHostController
    ) {
        when (calendarIntent) {
            is CalendarIntent.OnCalendarUpdate -> fetchTransaction(calendarIntent.calendarState)
            is CalendarIntent.OnDateSelected -> updateCalendarState(calendarIntent.calendarState)
            CalendarIntent.NavigateBack -> navHostController.popBackStack()
            is CalendarIntent.OnTransactionClick -> navHostController.navigateTo(
                Screens.EditTransaction.passTransactionId(
                    calendarIntent.transactionId
                )
            )
        }

    }

    private fun fetchTransaction(calendarState: CalendarState) {
        updateCalendarState(calendarState)
        fetchTransactions()
    }

    private fun updateCalendarState(calendarState: CalendarState) {
        _calendarState.tryEmit(
            calendarState
        )
    }

}