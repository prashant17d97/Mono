package com.debugdesk.mono.presentation.input

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.debugdesk.mono.R
import com.debugdesk.mono.domain.data.local.localdatabase.model.DailyTransaction
import com.debugdesk.mono.domain.data.local.localdatabase.model.emptyTransaction
import com.debugdesk.mono.domain.repo.Repository
import com.debugdesk.mono.navigation.Screens
import com.debugdesk.mono.presentation.edittrans.TransactionIntent
import com.debugdesk.mono.presentation.input.state.InputState
import com.debugdesk.mono.presentation.uicomponents.amounttf.AmountTfState
import com.debugdesk.mono.presentation.uicomponents.amounttf.TextFieldCalculatorIntent
import com.debugdesk.mono.presentation.uicomponents.editcategory.EditCategoryIntent
import com.debugdesk.mono.ui.appconfig.AppConfigManager
import com.debugdesk.mono.ui.appconfig.AppStateManager
import com.debugdesk.mono.utils.CameraFunction
import com.debugdesk.mono.utils.NavigationFunctions.navigateTo
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.double
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.showAlertDialog
import com.debugdesk.mono.utils.enums.ExpenseType
import com.debugdesk.mono.utils.enums.ImageSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class InputVM(
    private val repository: Repository,
    private val appConfigManager: AppConfigManager,
    private val appStateManager: AppStateManager,
) : ViewModel() {
    private val _incomeState = MutableStateFlow(InputState())
    private val _expenseState = MutableStateFlow(InputState())
    val expenseState: StateFlow<InputState> get() = _expenseState
    val incomeState: StateFlow<InputState> get() = _incomeState

    private var transactionType: ExpenseType = ExpenseType.Expense
    private var initialTransaction: DailyTransaction? = null

    private val stateFlow: MutableStateFlow<InputState>
        get() = if (transactionType == ExpenseType.Expense) _expenseState else _incomeState

    private val state: InputState
        get() = if (transactionType == ExpenseType.Expense) expenseState.value else incomeState.value

    companion object {
        private const val TAG = "MainVM"
    }

    init {
        fetchInitialData()
        listenChanges()
    }

    fun fetchInitialData() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.fetchCategories()
        }

        viewModelScope.launch {
            combine(
                appConfigManager.appConfigProperties,
                repository.categoryModelList
            ) { appConfigProperties, categoryModels ->

                InputState(
                    amountTfState = AmountTfState(
                        currencyIcon = appConfigProperties.selectedCurrencyIconDrawable
                    ),
                    categoryList = categoryModels,
                    appStateManager = appStateManager
                )
            }.collect {
                updateState(it)
            }
        }
    }

    private fun updateState(state: InputState) {
        _incomeState.tryEmit(
            InputState(
                amountTfState = state.amountTfState,
                categoryList = state.categoryList.filter { it.categoryType == ExpenseType.Income.name },
                appStateManager = state.appStateManager
            )
        )
        _expenseState.tryEmit(
            InputState(
                amountTfState = state.amountTfState,
                categoryList = state.categoryList.filter { it.categoryType == ExpenseType.Expense.name },
                appStateManager = state.appStateManager
            )
        )
    }

    private fun listenChanges() {
        combine(expenseState, incomeState) { expenseState, incomeState ->
            emitChanges(expenseState)
            emitChanges(incomeState)
        }.launchIn(viewModelScope)
    }

    private fun emitChanges(state: InputState) {
        val updatedState = state.copy(
            changesFound = state.transaction.amount != 0.0
                    && state.transaction.categoryIcon != emptyTransaction.categoryIcon
        )
        if (state == _incomeState.value) {
            _incomeState.tryEmit(updatedState)
        } else {
            _expenseState.tryEmit(updatedState)
        }
    }

    fun handleTransactionIntent(
        inputIntent: TransactionIntent,
        transactionType: ExpenseType,
        navHostController: NavHostController,
        context: Context
    ) {
        this.transactionType = transactionType
        when (inputIntent) {
            TransactionIntent.OnBackClick -> handleBackClick(navHostController)
            is TransactionIntent.OnNewTransactionSaveClick -> saveNewTransaction(
                inputIntent.type,
                navHostController, context
            )

            is TransactionIntent.OpenCalendarDialog -> updateCalendarDialog(inputIntent.showDialog)
            is TransactionIntent.UpdateAmount -> updateAmount(inputIntent.amountTFIntent)
            is TransactionIntent.UpdateDate -> updateDate(inputIntent.date)
            TransactionIntent.DismissCameraAndGalleryWindow, is TransactionIntent.DismissCameraGallery -> closeCameraAndGalleryWindow()
            is TransactionIntent.SaveImage -> saveImages(
                inputIntent
            )

            is TransactionIntent.UpdateCategoryIntent -> updateCategoryIntent(
                inputIntent.editCategoryIntent,
                navHostController
            )


            TransactionIntent.DeleteImage -> deleteImage()
            TransactionIntent.OnTrailIconClick -> openCameraAndGalleryWindow()
            is TransactionIntent.OnValueChange -> stateFlow.tryEmit(
                state.copy(
                    transaction = state.transaction.copy(note = inputIntent.value),
                    note = inputIntent.value
                )
            )

            else -> {}
        }
    }

    private fun handleBackClick(navHostController: NavHostController) {
        if (initialTransaction == null
            || initialTransaction == expenseState.value.transaction
            || initialTransaction == incomeState.value.transaction
        ) {
            navHostController.popBackStack()
        } else {
            appStateManager.showAlertDialog(
                message = R.string.discard_changes,
                positiveButtonText = R.string.discard,
                onPositiveClick = { navHostController.popBackStack() }
            )
        }
    }

    private fun saveNewTransaction(
        type: ExpenseType,
        navHostController: NavHostController,
        context: Context
    ) {
        val (month, year) = CommonFunctions.getMonthAndYearFromLong(state.transaction.date)

        viewModelScope.launch {
            val transaction = state.transaction.copy(
                type = type.name,
                currentMonthId = month,
                year = year
            )
            repository.insert(transaction)
        }.invokeOnCompletion {
            resetState(navHostController, context)
        }
    }

    private fun resetState(navHostController: NavHostController, context: Context) {
        viewModelScope.launch {
            updateState(InputState())
            repository.getTransactionAll()
            withContext(Dispatchers.Main) {
                appStateManager.showToastState(toastMsg = R.string.transaction_added)
                navHostController.popBackStack()
                CameraFunction.clearPicturesFolder(context)
            }
        }
    }

    private fun saveImages(intent: TransactionIntent.SaveImage) {
        stateFlow.tryEmit(
            state.copy(
                transaction = state.transaction.copy(
                    imagePath = intent.imagePath,
                    imageSource = intent.imageSource,
                    createdOn = intent.createdOn
                ),
                image = intent.imagePath,
                createdOn = intent.createdOn,
                imageSource = intent.imageSource

            )
        )
        closeCameraAndGalleryWindow()
    }

    private fun updateCalendarDialog(showDialog: Boolean) {
        stateFlow.tryEmit(state.copy(showCalendarDialog = showDialog))
    }

    private fun updateAmount(amountIntent: TextFieldCalculatorIntent) {
        val amount = when (amountIntent) {
            is TextFieldCalculatorIntent.OnHeightChange -> {
                state.copy(
                    amountTfState = state.amountTfState.copy(
                        height = amountIntent.height,
                    )
                )
            }

            is TextFieldCalculatorIntent.OnValueChange -> {
                state.copy(
                    transaction = state.transaction.copy(
                        amount = amountIntent.value.double()
                    ), amountTfState = state.amountTfState.copy(
                        amountValue = amountIntent.value
                    )
                )
            }

            is TextFieldCalculatorIntent.OpenDialog -> {
                state.copy(
                    amountTfState = state.amountTfState.copy(
                        openDialog = amountIntent.openDialog
                    )
                )
            }
        }
        stateFlow.tryEmit(amount)
    }

    private fun updateCategoryIntent(
        categoryIntent: EditCategoryIntent,
        navHostController: NavHostController
    ) {
        when (categoryIntent) {
            is EditCategoryIntent.OnCategoryListChange -> {
                val selectedCategory = categoryIntent.list.first { it.isSelected }
                stateFlow.tryEmit(
                    state.copy(
                        categoryList = categoryIntent.list,
                        transaction = state.transaction.copy(
                            category = selectedCategory.category,
                            categoryId = selectedCategory.categoryId,
                            categoryIcon = selectedCategory.categoryIcon ?: R.drawable.mono
                        )
                    )
                )
            }

            EditCategoryIntent.OnEditCategoryClicked -> navHostController.navigateTo(Screens.EditCategory)
        }
    }

    private fun updateDate(date: Long) {
        val (month, year) = CommonFunctions.getMonthAndYearFromLong(date)
        Log.d(TAG, "updateDate: $month, $year, ${Date(date)}")
        stateFlow.tryEmit(
            state.copy(
                transaction = state.transaction.copy(
                    date = date,
                    currentMonthId = month,
                    year = year
                ),
                date = date,
            )
        )
    }


    private fun deleteImage() {
        stateFlow.tryEmit(
            state.copy(
                transaction = state.transaction.copy(
                    imagePath = "",
                    imageSource = ImageSource.NONE
                ),
                image = "",
                imageSource = ImageSource.NONE,
                createdOn = 0L
            )
        )
        appStateManager.showToastState(toastMsg = R.string.image_deleted)
    }

    private fun closeCameraAndGalleryWindow() {
        stateFlow.tryEmit(state.copy(showCameraAndGallery = false))
    }

    private fun openCameraAndGalleryWindow() {
        stateFlow.tryEmit(state.copy(showCameraAndGallery = true))
    }

    fun closeAllDialogs(transactionType: ExpenseType) {
        this.transactionType = transactionType
        _incomeState.tryEmit(
            incomeState.value.copy(
                showCalendarDialog = false,
                showCameraAndGallery = false,
                changesFound = false,
                transactionType = transactionType
            )
        )
        _expenseState.tryEmit(
            expenseState.value.copy(
                showCalendarDialog = false,
                showCameraAndGallery = false,
                changesFound = false,
                transactionType = transactionType
            )
        )
    }
}

