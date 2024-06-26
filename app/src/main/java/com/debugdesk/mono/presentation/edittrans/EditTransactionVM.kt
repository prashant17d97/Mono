package com.debugdesk.mono.presentation.edittrans

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.debugdesk.mono.R
import com.debugdesk.mono.domain.data.local.localdatabase.model.DailyTransaction
import com.debugdesk.mono.domain.repo.Repository
import com.debugdesk.mono.navigation.Screens
import com.debugdesk.mono.presentation.uicomponents.amounttf.AmountTfState
import com.debugdesk.mono.presentation.uicomponents.amounttf.TextFieldCalculatorIntent
import com.debugdesk.mono.presentation.uicomponents.editcategory.EditCategoryIntent
import com.debugdesk.mono.presentation.uicomponents.notetf.NoteIntent
import com.debugdesk.mono.presentation.uicomponents.notetf.NoteState
import com.debugdesk.mono.ui.appconfig.AppConfigManager
import com.debugdesk.mono.ui.appconfig.AppStateManager
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.double
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.getCurrentMonthYear
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.showAlertDialog
import com.debugdesk.mono.utils.enums.ExpenseType
import com.debugdesk.mono.utils.enums.ImageSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditTransactionVM(
    val appStateManager: AppStateManager,
    private val appConfigManager: AppConfigManager,
    private val repository: Repository,
) : ViewModel() {

    private val _editTransactionState = MutableStateFlow(TransactionState())
    val editTransactionState: StateFlow<TransactionState>
        get() = _editTransactionState

    private var initialTransaction: DailyTransaction? = null

    fun getDailyTransaction(transactionId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.fetchTransactionFromId(transactionId)
            repository.fetchCategories()
        }
    }

    init {
        viewModelScope.launch {
            combine(
                appConfigManager.appConfigProperties,
                repository.categoryModelList,
                repository.transaction
            ) { appConfigProperties, categoryModels, transaction: DailyTransaction ->
                initialTransaction = transaction
                TransactionState(
                    transaction = transaction, amountTfState = AmountTfState(
                        currencyIcon = appConfigProperties.selectedCurrencyIconDrawable,
                        amountValue = transaction.amount.toString(),
                    ), noteState = NoteState(
                        imagePath = transaction.imagePath,
                        imageSource = transaction.imageSource,
                        createdOn = transaction.createdOn,
                        noteValue = transaction.note
                    ), categoryList = categoryModels.map { categoryModel ->
                        categoryModel.copy(
                            isSelected = categoryModel.categoryId == transaction.categoryId
                        )
                    },
                    appStateManager = appStateManager
                )
            }.collect {
                _editTransactionState.tryEmit(it)
            }
        }
        listenChanges()
    }

    private fun listenChanges() {
        editTransactionState.onEach {
            _editTransactionState.tryEmit(
                it.copy(
                    changesFound = it.transaction != initialTransaction
                )
            )
        }.launchIn(viewModelScope)
    }

    fun handleTransactionIntent(
        transactionIntent: TransactionIntent,
        navHostController: NavHostController,
    ) {
        when (transactionIntent) {
            TransactionIntent.OnBackClick -> {
                if (initialTransaction == null || initialTransaction == editTransactionState.value.transaction) {
                    navHostController.popBackStack()
                } else {
                    appStateManager.showAlertDialog(
                        message = R.string.discard_changes,
                        positiveButtonText = R.string.discard,
                        onPositiveClick = {
                            navHostController.popBackStack()
                        },
                    )
                }
            }

            TransactionIntent.OnDeleteClick -> deleteTransactionWithAlert(navHostController)
            TransactionIntent.OnUpdateClick -> updateTransaction(navHostController)
            is TransactionIntent.OnNewTransactionSaveClick -> saveNewTransaction(transactionIntent.type)
            is TransactionIntent.OpenCalendarDialog -> updateCalendarDialog(transactionIntent.showDialog)
            is TransactionIntent.UpdateAmount -> updateAmount(transactionIntent.amountTFIntent)
            is TransactionIntent.UpdateCategoryIntent -> updateCategoryIntent(
                transactionIntent.editCategoryIntent, navHostController
            )

            is TransactionIntent.UpdateDate -> updateDate(transactionIntent.date)
            is TransactionIntent.UpdateNote -> updateNote(transactionIntent.noteIntent)
            TransactionIntent.DismissCameraAndGalleryWindow -> closeCameraAndGalleryWindow()
            is TransactionIntent.DismissCameraGallery -> closeCameraAndGalleryWindow()

            is TransactionIntent.SaveImage -> saveImages(transactionIntent)
        }
    }

    private fun saveNewTransaction(type: ExpenseType) {
        viewModelScope.launch {
            repository.insert(editTransactionState.value.transaction.copy(type = type.name))
        }
    }

    private fun saveImages(intent: TransactionIntent.SaveImage) {
        _editTransactionState.tryEmit(
            editTransactionState.value.copy(
                transaction = editTransactionState.value.transaction.copy(
                    imagePath = intent.imagePath,
                    imageSource = intent.imageSource,
                    createdOn = intent.createdOn
                ),
                noteState = editTransactionState.value.noteState.copy(
                    imagePath = intent.imagePath,
                    imageSource = intent.imageSource,
                    createdOn = intent.createdOn
                )

            )
        )
        closeCameraAndGalleryWindow()
    }


    private fun deleteTransactionWithAlert(navHostController: NavHostController) {
        appStateManager.showAlertDialog(message = R.string.transaction_deleted_alert,
            onPositiveClick = {
                deleteTransaction(
                    navHostController
                )
            })
    }

    private fun updateCalendarDialog(showDialog: Boolean) {
        _editTransactionState.tryEmit(
            editTransactionState.value.copy(
                showCalendarDialog = showDialog
            )
        )
    }

    private fun updateTransaction(
        navHostController: NavHostController
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTransaction(
                editTransactionState.value.transaction
            )
            repository.getAllTransactionByMonth(
                getCurrentMonthYear().first, getCurrentMonthYear().second
            )
        }.invokeOnCompletion {
            viewModelScope.launch {
                withContext(Dispatchers.Main) {
                    appStateManager.showToastState(toastMsg = R.string.transaction_updated)
                    navHostController.popBackStack()
                }
            }
        }
    }

    private fun deleteTransaction(
        navHostController: NavHostController
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTransaction(
                editTransactionState.value.transaction
            )
            repository.getAllTransactionByMonth(
                getCurrentMonthYear().first, getCurrentMonthYear().second
            )
        }.invokeOnCompletion {
            appStateManager.showToastState(toastMsg = R.string.transaction_deleted)
        }
        navHostController.popBackStack()
    }

    private fun updateNote(noteIntent: NoteIntent) {
        when (noteIntent) {
            NoteIntent.OnTrailIconClick -> openCameraAndGalleryWindow()

            is NoteIntent.OnValueChange -> {
                _editTransactionState.tryEmit(
                    editTransactionState.value.copy(
                        transaction = editTransactionState.value.transaction.copy(
                            note = noteIntent.value
                        ), noteState = editTransactionState.value.noteState.copy(
                            noteValue = noteIntent.value
                        )
                    )
                )
            }

            is NoteIntent.DeleteImage -> deleteImage()
        }

    }

    private fun updateAmount(amountIntent: TextFieldCalculatorIntent) {
        when (amountIntent) {
            is TextFieldCalculatorIntent.OnHeightChange -> {
                _editTransactionState.tryEmit(
                    editTransactionState.value.copy(
                        amountTfState = editTransactionState.value.amountTfState.copy(
                            height = amountIntent.height,
                        )
                    )
                )
            }

            is TextFieldCalculatorIntent.OnValueChange -> {
                _editTransactionState.tryEmit(
                    editTransactionState.value.copy(
                        transaction = editTransactionState.value.transaction.copy(
                            amount = amountIntent.value.double()
                        ), amountTfState = editTransactionState.value.amountTfState.copy(
                            amountValue = amountIntent.value
                        )
                    )
                )
            }

            is TextFieldCalculatorIntent.OpenDialog -> {
                _editTransactionState.tryEmit(
                    editTransactionState.value.copy(
                        amountTfState = editTransactionState.value.amountTfState.copy(
                            openDialog = amountIntent.openDialog
                        )
                    )
                )
            }
        }

    }

    private fun updateCategoryIntent(
        categoryIntent: EditCategoryIntent, navHostController: NavHostController
    ) {
        when (categoryIntent) {
            is EditCategoryIntent.OnCategoryListChange -> {
                val selectedCategory = categoryIntent.list.first { it.isSelected }
                _editTransactionState.tryEmit(
                    editTransactionState.value.copy(
                        categoryList = categoryIntent.list,
                        transaction = editTransactionState.value.transaction.copy(
                            category = selectedCategory.category,
                            categoryId = selectedCategory.categoryId,
                            categoryIcon = selectedCategory.categoryIcon ?: R.drawable.mono
                        )
                    )
                )
            }

            EditCategoryIntent.OnEditCategoryClicked -> {
                navHostController.navigate(Screens.EditCategory.route)
            }
        }
    }

    private fun updateDate(date: Long) {
        val (month, year) = CommonFunctions.getMonthAndYearFromLong(date)

        _editTransactionState.tryEmit(
            editTransactionState.value.copy(
                transaction = editTransactionState.value.transaction.copy(
                    date = date,
                    currentMonthId = month,
                    year = year
                )
            )
        )
    }

    private fun deleteImage() {
        _editTransactionState.tryEmit(
            editTransactionState.value.copy(
                transaction = editTransactionState.value.transaction.copy(
                    imagePath = byteArrayOf(),
                    imageSource = ImageSource.NONE,
                    createdOn = 0L
                ),
                noteState = editTransactionState.value.noteState.copy(
                    imagePath = byteArrayOf(),
                    imageSource = ImageSource.NONE,
                    createdOn = 0L
                )
            )
        )
        appStateManager.showToastState(toastMsg = R.string.image_deleted)
    }


    private fun closeCameraAndGalleryWindow() {
        if (editTransactionState.value.showCameraAndGallery) {
            _editTransactionState.tryEmit(
                editTransactionState.value.copy(showCameraAndGallery = false)
            )
        }
    }


    private fun openCameraAndGalleryWindow() {
        _editTransactionState.tryEmit(
            editTransactionState.value.copy(showCameraAndGallery = true)
        )
    }
}