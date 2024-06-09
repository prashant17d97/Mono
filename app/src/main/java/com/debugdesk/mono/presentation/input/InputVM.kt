package com.debugdesk.mono.presentation.input

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.debugdesk.mono.R
import com.debugdesk.mono.domain.data.local.localdatabase.model.DailyTransaction
import com.debugdesk.mono.domain.data.local.localdatabase.model.TransactionImage
import com.debugdesk.mono.domain.data.local.localdatabase.model.emptyTransaction
import com.debugdesk.mono.domain.repo.Repository
import com.debugdesk.mono.navigation.Screens
import com.debugdesk.mono.presentation.edittrans.TransactionIntent
import com.debugdesk.mono.presentation.input.state.InputState
import com.debugdesk.mono.presentation.uicomponents.amounttf.AmountTfState
import com.debugdesk.mono.presentation.uicomponents.amounttf.TextFieldCalculatorIntent
import com.debugdesk.mono.presentation.uicomponents.editcategory.EditCategoryIntent
import com.debugdesk.mono.presentation.uicomponents.notetf.NoteIntent
import com.debugdesk.mono.ui.appconfig.AppConfigManager
import com.debugdesk.mono.ui.appconfig.AppStateManager
import com.debugdesk.mono.utils.CameraFunction.deleteImageFile
import com.debugdesk.mono.utils.NavigationFunctions.navigateTo
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.double
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.showAlertDialog
import com.debugdesk.mono.utils.enums.ExpenseType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
                    amountTfState = AmountTfState(currencyIcon = appConfigProperties.currencyIcon),
                    categoryList = categoryModels,
                    appStateManager = appStateManager
                )
            }.collect {
                updateState(it)
            }
        }
    }

    private fun updateState(state: InputState) {
        _incomeState.tryEmit(state)
        _expenseState.tryEmit(state)
    }

    private fun listenChanges() {
        combine(expenseState, incomeState) { expenseState, incomeState ->
            emitChanges(expenseState)
            emitChanges(incomeState)
        }.launchIn(viewModelScope)
    }

    private fun emitChanges(state: InputState) {
        val updatedState = state.copy(
            changesFound = state.transaction != emptyTransaction
                    && state.transaction.amount != 0.0
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
    ) {
        this.transactionType = transactionType
        when (inputIntent) {
            TransactionIntent.OnBackClick -> handleBackClick(navHostController)
            is TransactionIntent.OnNewTransactionSaveClick -> saveNewTransaction(
                inputIntent.type,
                navHostController
            )
            is TransactionIntent.OpenCalendarDialog -> updateCalendarDialog(inputIntent.showDialog)
            is TransactionIntent.UpdateAmount -> updateAmount(inputIntent.amountTFIntent)
            is TransactionIntent.UpdateCategoryIntent -> updateCategoryIntent(
                inputIntent.editCategoryIntent,
                navHostController
            )
            is TransactionIntent.UpdateDate -> updateDate(inputIntent.date)
            is TransactionIntent.UpdateNote -> updateNoteIntent(inputIntent.noteIntent)
            TransactionIntent.DismissCameraAndGalleryWindow, is TransactionIntent.DismissCameraGallery -> closeCameraAndGalleryWindow()
            is TransactionIntent.SaveImagesFilePath -> saveImages(inputIntent.transactionImages)
            is TransactionIntent.DeleteImage -> deleteImage(inputIntent.transactionImages)
            TransactionIntent.CloseImageGallery -> closeGallery()
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

    private fun closeGallery() {
        stateFlow.tryEmit(state.copy(showImageGallery = false))
    }

    private fun saveNewTransaction(type: ExpenseType, navHostController: NavHostController) {
        val (month, year) = CommonFunctions.getMonthAndYearFromLong(state.transaction.date)

        viewModelScope.launch {
            val transaction = state.transaction.copy(
                type = type.name,
                currentMonthId = month,
                year = year
            )
            repository.insert(transaction)
        }.invokeOnCompletion {
            resetState(navHostController)
        }
    }

    private fun resetState(navHostController: NavHostController) {
        viewModelScope.launch {
            updateState(InputState())
            repository.getTransactionAll()
            withContext(Dispatchers.Main) {
                appStateManager.showToastState(toastMsg = R.string.transaction_added)
                navHostController.popBackStack()
            }
        }
    }

    private fun saveImages(images: List<TransactionImage>) {
        val transactionId = state.transaction.transactionId
        val existingImages = state.transaction.transactionImage.associateBy { it.absolutePath }

        val updatedImages = images.map { transactionImage ->
            transactionImage.copy(
                transactionId = transactionId,
                imageId = existingImages[transactionImage.absolutePath]?.imageId ?: 0
            )
        }

        stateFlow.tryEmit(
            state.copy(
                transaction = state.transaction.copy(transactionImage = updatedImages),
                noteState = state.noteState.copy(transactionImages = images)
            )
        )
        closeCameraAndGalleryWindow()
    }

    private fun updateCalendarDialog(showDialog: Boolean) {
        stateFlow.tryEmit(state.copy(showCalendarDialog = showDialog))
    }

    private fun updateNoteIntent(
        noteIntent: NoteIntent,
    ) {
        when (noteIntent) {
            NoteIntent.OnTrailIconClick -> openCameraAndGalleryWindow()
            is NoteIntent.OnValueChange -> stateFlow.tryEmit(
                state.copy(
                    transaction = state.transaction.copy(note = noteIntent.value),
                    noteState = state.noteState.copy(noteValue = noteIntent.value)
                )
            )
            is NoteIntent.DeleteImages -> deleteImage(noteIntent.transactionImage)
            is NoteIntent.ShowGallery -> stateFlow.tryEmit(
                state.copy(showImageGallery = true, clickedIndex = noteIntent.selectedIndex)
            )
            else -> {}
        }
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
        stateFlow.tryEmit(
            state.copy(
                transaction = state.transaction.copy(
                    date = date,
                    currentMonthId = month,
                    year = year
                )
            )
        )
    }

    private fun deleteImage(images: List<TransactionImage>) {
        stateFlow.tryEmit(
            state.copy(
                transaction = state.transaction.copy(transactionImage = images),
                noteState = state.noteState.copy(transactionImages = images)
            )
        )
        images.forEach { it.deleteImageFile() }
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
                showImageGallery = false,
                changesFound = false,
                transactionType = transactionType
            )
        )
        _expenseState.tryEmit(
            expenseState.value.copy(
                showCalendarDialog = false,
                showCameraAndGallery = false,
                showImageGallery = false,
                changesFound = false,
                transactionType = transactionType
            )
        )
    }
}

