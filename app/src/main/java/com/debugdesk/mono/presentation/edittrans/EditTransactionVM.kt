package com.debugdesk.mono.presentation.edittrans

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.debugdesk.mono.R
import com.debugdesk.mono.domain.data.local.localdatabase.model.DailyTransaction
import com.debugdesk.mono.domain.data.local.localdatabase.model.TransactionImage
import com.debugdesk.mono.domain.repo.Repository
import com.debugdesk.mono.navigation.Screens
import com.debugdesk.mono.presentation.uicomponents.amounttf.AmountTfState
import com.debugdesk.mono.presentation.uicomponents.amounttf.TextFieldCalculatorIntent
import com.debugdesk.mono.presentation.uicomponents.editcategory.EditCategoryIntent
import com.debugdesk.mono.presentation.uicomponents.notetf.NoteIntent
import com.debugdesk.mono.presentation.uicomponents.notetf.NoteState
import com.debugdesk.mono.ui.appconfig.AppConfigManager
import com.debugdesk.mono.ui.appconfig.AppStateManager
import com.debugdesk.mono.utils.CameraFunction.deleteImageFile
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.double
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.getCurrentMonthYear
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.showAlertDialog
import com.debugdesk.mono.utils.enums.ExpenseType
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
                        currencyIcon = appConfigProperties.currencyIcon,
                        amountValue = transaction.amount.toString(),
                    ), noteState = NoteState(
                        transactionImages = transaction.transactionImage,
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

            is TransactionIntent.SaveImagesFilePath -> saveImages(
                transactionIntent.transactionImages
            )

            is TransactionIntent.DeleteImage -> deleteImage(transactionIntent.transactionImages)
            TransactionIntent.CloseImageGallery -> _editTransactionState.tryEmit(
                editTransactionState.value.copy(
                    showImageGallery = false
                )
            )

            is TransactionIntent.DeleteFromDB -> deleteDBImage(transactionIntent.transactionImage)
        }
    }

    private fun saveNewTransaction(type: ExpenseType) {
        viewModelScope.launch {
            repository.insert(editTransactionState.value.transaction.copy(type = type.name))
        }
    }

    private fun saveImages(images: List<TransactionImage>) {
        val editData = editTransactionState.value
        val transactionId = editData.transaction.transactionId
        val existingImages = editData.transaction.transactionImage.associateBy { it.absolutePath }

        val updatedImages = images.map { transactionImage ->
            val imageId = existingImages[transactionImage.absolutePath]?.imageId ?: 0
            transactionImage.copy(transactionId = transactionId, imageId = imageId)
        }

        val updatedTransaction = editData.transaction.copy(transactionImage = updatedImages)
        val updatedNoteState = editData.noteState.copy(transactionImages = images)

        _editTransactionState.tryEmit(
            editData.copy(
                transaction = updatedTransaction,
                noteState = updatedNoteState
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

            is NoteIntent.DeleteImages -> deleteImage(noteIntent.transactionImage)

            is NoteIntent.ShowGallery -> {
                _editTransactionState.tryEmit(
                    editTransactionState.value.copy(
                        showImageGallery = true, clickedIndex = noteIntent.selectedIndex
                    )
                )
            }

            is NoteIntent.DeleteFromDB -> {
                deleteDBImage(noteIntent.transactionImage)
            }
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

    private fun deleteImage(images: List<TransactionImage>) {
        _editTransactionState.tryEmit(
            editTransactionState.value.copy(
                transaction = editTransactionState.value.transaction.copy(
                    transactionImage = images
                ), noteState = editTransactionState.value.noteState.copy(
                    transactionImages = images
                )
            )
        )
        images.forEach {
            it.deleteImageFile(
                deleteFromDB = {
                    deleteDBImage(it)
                }
            )
        }
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

    private fun deleteDBImage(transactionImage: TransactionImage) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTransactionImage(transactionImage, onSuccess = {
                appStateManager.showToastState(toastMsg = R.string.image_deleted)
            })
        }
    }
}