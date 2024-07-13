package com.debugdesk.mono.presentation.report

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.debugdesk.mono.R
import com.debugdesk.mono.domain.repo.Repository
import com.debugdesk.mono.navigation.Screens
import com.debugdesk.mono.presentation.uicomponents.hasPermission
import com.debugdesk.mono.ui.appconfig.AppConfigManager
import com.debugdesk.mono.ui.appconfig.AppStateManager
import com.debugdesk.mono.utils.NavigationFunctions.navigateTo
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.getCurrentMonthYear
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.getExpenseAmount
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.getIncomeAmount
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.getMonthAndYearFromLong
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.getMonthName
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.getMonthRange
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.getNextMonth
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.getPreviousMonth
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.getTotalAmount
import com.debugdesk.mono.utils.enums.ExpenseType
import com.debugdesk.mono.utils.states.AlertState
import com.debugdesk.mono.utils.states.Drawable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ReportVM(
    private val appConfigManager: AppConfigManager,
    private val repository: Repository,
    private val appStateManager: AppStateManager
) : ViewModel() {

    companion object {
        private const val TAG = "ReportVM"
    }

    private val _requestNotificationPermission = MutableStateFlow(false)
    val requestNotificationPermission: StateFlow<Boolean>
        get() = _requestNotificationPermission

    val appState = appStateManager

    fun requestNotificationPermissionDialog(context: Context) {
        if (!requestNotificationPermission.value && Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU && !context.hasPermission(
                arrayOf(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            )
        ) {
            appStateManager.updateAlertState(
                AlertState(
                    show = true,
                    title = R.string.notfication_title,
                    message = R.string.notfication_message,
                    negativeButtonText = R.string.empty,
                    drawable = Drawable.Animated(),
                    onPositiveClick = {
                        _requestNotificationPermission.value = true
                    }
                )
            )

        }
    }

    init {
        listenStateChanges()
        viewModelScope.launch(Dispatchers.IO) {
            repository.getTransactionAll()
            repository.fetchCategories()
            getMonthTransaction(getCurrentMonthYear())
        }.invokeOnCompletion { updateTab(0) }
    }

    fun fetchTransaction() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getTransactionAll()
            getMonthTransaction(getCurrentMonthYear())
        }.invokeOnCompletion { updateTab(0) }
    }

    private val _reportState: MutableStateFlow<ReportState> = MutableStateFlow(ReportState())

    val reportState: StateFlow<ReportState> = _reportState

    private fun listenStateChanges() {
        viewModelScope.launch {
            combine(
                appConfigManager.appConfigProperties,
                repository.allDailyTransaction,
                repository.allDailyMonthTransaction,
                repository.allDailyYearTransaction,
                repository.categoryModelList
            ) { appConfigProperties, allDailyTransaction, allDailyMonthTransaction, _, categories ->
                ReportState(
                    allDailyMonthTransaction = allDailyMonthTransaction,
                    transaction = allDailyMonthTransaction,
                    totalAmount = allDailyTransaction.getTotalAmount(),
                    currentMonthExpense = allDailyMonthTransaction.getExpenseAmount(),
                    currentMonthIncome = allDailyMonthTransaction.getIncomeAmount(),
                    currentMonthAvailableBalance = allDailyMonthTransaction.getTotalAmount(),
                    currency = appConfigProperties.selectedCurrencyIconString,
                    categories = categories
                )

            }.collect {
                delay(100)
                _reportState.tryEmit(
                    reportState.value.copy(
                        allDailyMonthTransaction = it.allDailyMonthTransaction,
                        transaction = it.transaction,
                        totalAmount = it.totalAmount,
                        currentMonthExpense = it.currentMonthExpense,
                        currentMonthIncome = it.currentMonthIncome,
                        currentMonthAvailableBalance = it.currentMonthAvailableBalance,
                        currency = it.currency,
                        categories = it.categories
                    )
                )
                updateTab(0)
            }
        }
    }

    // Function to fetch transaction data for a specific month
    private fun getMonthTransaction(currentMonthYear: Pair<Int, Int> = getCurrentMonthYear()) {
        val (month, year) = currentMonthYear
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllTransactionByMonth(month, year)
            repository.getAllTransactionByYear(year)
        }
    }

    // Function to update tab content based on selected index
    private fun updateTab(currentIndex: Int) {
        val transaction = when (currentIndex) {
            0 -> reportState.value.allDailyMonthTransaction
            1 -> reportState.value.allDailyMonthTransaction.filter { it.type == ExpenseType.Expense.name }
            2 -> reportState.value.allDailyMonthTransaction.filter { it.type == ExpenseType.Income.name }
            else -> emptyList()
        }
        _reportState.tryEmit(
            reportState.value.copy(
                transaction = transaction,
                tabs = reportState.value.tabs.mapIndexed { index, tabs ->
                    tabs.copy(isSelected = index == currentIndex)
                }
            )
        )

    }

    // Function to update selected date
    private fun updateSelectedDate(timeStamp: Long) {
        val getMonthAndYearFromLong = getMonthAndYearFromLong(timeStamp)
        getMonthTransaction(getMonthAndYearFromLong)
        _reportState.tryEmit(
            reportState.value.copy(
                selectedDate = getMonthAndYearFromLong,
                monthString = getMonthName(getMonthAndYearFromLong.first),
                month = getMonthAndYearFromLong.first,
                year = getMonthAndYearFromLong.second,
                showRest = true,
                filterString = null,
            )
        )
    }


    fun updateReportState(
        context: Context,
        reportIntent: ReportIntent,
        navHostController: NavHostController
    ) {
        when (reportIntent) {
            is ReportIntent.ResetClick -> {
                updateFilter(
                    context,
                    ReportIntent.UpdateFilter(filterRange = FilterRange.THIS_MONTH)
                )

                val (month, year) = getCurrentMonthYear()
                _reportState.tryEmit(
                    reportState.value.copy(
                        selectedDate = getCurrentMonthYear(),
                        monthString = getMonthName(month),
                        month = month,
                        year = year,
                        showRest = false,
                        filterString = null,
                    )
                )
                getMonthTransaction(getCurrentMonthYear())
            }

            ReportIntent.RightClick -> updateSelectedDate(
                getNextMonth(
                    reportState.value.selectedDate.first,
                    reportState.value.selectedDate.second
                )
            )

            ReportIntent.LeftClick -> updateSelectedDate(
                getPreviousMonth(
                    reportState.value.selectedDate.first,
                    reportState.value.selectedDate.second
                )
            )

            is ReportIntent.UpdateTab -> updateTab(reportIntent.currentIndex)
            is ReportIntent.UpdateSelectedDate -> updateSelectedDate(reportIntent.timeStamp)
            is ReportIntent.UpdateFilter -> {
                updateFilter(context, reportIntent)
                viewModelScope.launch(Dispatchers.IO) {
                    when (reportIntent.filterRange) {
                        FilterRange.THIS_MONTH -> getMonthTransaction()
                        FilterRange.LAST_MONTH,
                        FilterRange.LAST_THREE_MONTH, FilterRange.LAST_SIX_MONTH -> {
                            val (startDate, endDate) = getMonthRange(reportIntent.filterRange.range)
                            repository.getTransactionByDateRange(
                                startDate = startDate, endDate = endDate

                            )
                        }

                        FilterRange.CUSTOM_RANGE -> {
                            repository.getTransactionByDateRange(
                                reportIntent.dateRange?.first ?: 0L,
                                reportIntent.dateRange?.second ?: 0L
                            )
                        }
                    }
                }

            }

            is ReportIntent.ExpandCalendar -> {
                _reportState.tryEmit(
                    reportState.value.copy(
                        isCalendarExpanded = reportIntent.isCalendarExpanded
                    )
                )
            }

            is ReportIntent.OnTransactionClick -> {
                _reportState.tryEmit(
                    reportState.value.copy(
                        showTransactionCard = true,
                        showClickedTransaction = reportIntent.transaction
                    )
                )
            }

            ReportIntent.DeleteTransaction -> {
                _reportState.tryEmit(
                    reportState.value.copy(
                        showTransactionCard = false
                    )
                )
                viewModelScope.launch(Dispatchers.IO) {
                    repository.deleteTransaction(reportState.value.showClickedTransaction)
                    getMonthTransaction()
                }
            }

            ReportIntent.CloseTransactionCard -> {
                _reportState.tryEmit(
                    reportState.value.copy(
                        showTransactionCard = false
                    )
                )
            }

            is ReportIntent.EditTransaction -> {
                _reportState.tryEmit(
                    reportState.value.copy(
                        showTransactionCard = false
                    )
                )
                navHostController.navigate(
                    Screens.EditTransaction.passTransactionId(
                        reportIntent.transactionId
                    )
                )
            }

            is ReportIntent.ChangeReportView -> updateView(
                navHostController,
                reportIntent.reportView
            )

            is ReportIntent.NavigateToGraph -> navHostController.navigateTo(
                Screens.Graph.passCategoryId(
                    reportIntent.categoryId
                )
            )
        }
    }

    private fun updateFilter(context: Context, reportIntent: ReportIntent.UpdateFilter) {
        _reportState.tryEmit(
            reportState.value.copy(
                filters = reportState.value.filters.map { filter ->
                    filter.copy(isSelected = filter == reportIntent.filter)
                },
                filterString = context.getString(reportIntent.filter.title),
                showRest = true
            )
        )
    }

    fun updateView(navHostController: NavHostController, reportView: ReportView) {
        when (reportView) {
            ReportView.MonthlyReport, ReportView.CategoryReport -> _reportState.tryEmit(
                reportState.value.copy(
                    selectedReportView = reportView.stringValue
                )
            )

            ReportView.CalendarReport -> navHostController.navigateTo(Screens.CalendarPage)
        }
    }
}
