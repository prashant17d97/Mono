package com.debugdesk.mono.presentation.edittrans

import android.content.Context
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
import com.debugdesk.mono.ui.appconfig.AppConfigManager
import com.debugdesk.mono.ui.appconfig.AppStateManager
import com.debugdesk.mono.utils.CameraFunction
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.getCurrentMonthYear
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.showAlertDialog
import com.debugdesk.mono.utils.enums.ImageSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class EditTransactionVM(
    private val appStateManager: AppStateManager,
    appConfigManager: AppConfigManager,
    private val repository: Repository,
) : ViewModel() {
    private val _editTransactionState = MutableStateFlow(TransactionState())
    val editTransactionState: StateFlow<TransactionState> = _editTransactionState

    private val appConfigProperties = appConfigManager.appConfigProperties
    private val categoryModels = repository.categoryModelList

    private var initialTransactionState: TransactionState? = null

    init {
        viewModelScope.launch {
            combine(appConfigProperties, categoryModels) { _, _ ->
                initial()
            }
        }
    }

    fun getDailyTransaction(transactionId: Int) {
        initial()
        viewModelScope.launch(Dispatchers.IO) {
            repository.fetchTransactionFromId(transactionId)
        }
    }

    private fun initial() {
        viewModelScope.launch {
            repository.fetchCategories()
            listenTransactionFetch()
            listenChanges()
        }
    }

    private fun listenTransactionFetch() {
        viewModelScope.launch {
            repository.editTransaction.collect { transaction ->
                transaction?.let {
                    val transactionState = createTransactionState(transaction)
                    initialTransactionState = transactionState
                    _editTransactionState.emit(transactionState)
                }
            }
        }
    }

    private fun listenChanges() {
        viewModelScope.launch {
            editTransactionState.collect { transaction ->
                _editTransactionState.emit(
                    transaction.copy(changesFound = transaction isEqualTo initialTransactionState),
                )
            }
        }
    }

    fun handleTransactionIntent(
        transactionIntent: TransactionIntent,
        navHostController: NavHostController,
        context: Context,
    ) {
        viewModelScope.launch {
            when (transactionIntent) {
                TransactionIntent.OnBackClick -> handleBackClick(navHostController)
                TransactionIntent.OnDeleteClick -> deleteTransactionWithAlert(navHostController)
                TransactionIntent.OnUpdateClick -> updateTransaction(navHostController, context)

                is TransactionIntent.OpenCalendarDialog -> updateCalendarDialog(transactionIntent.showDialog)
                is TransactionIntent.UpdateAmount -> updateAmount(transactionIntent.amountTFIntent)
                is TransactionIntent.UpdateCategoryIntent ->
                    updateCategoryIntent(
                        transactionIntent.editCategoryIntent,
                        navHostController,
                    )

                is TransactionIntent.UpdateDate -> updateDate(transactionIntent.date)
                TransactionIntent.DismissCameraAndGalleryWindow,
                is TransactionIntent.DismissCameraGallery,
                -> closeCameraAndGalleryWindow()

                is TransactionIntent.SaveImage -> saveImages(transactionIntent)
                TransactionIntent.DeleteImage -> deleteImage()
                TransactionIntent.OnTrailIconClick -> openCameraAndGalleryWindow()
                is TransactionIntent.OnValueChange -> updateNote(transactionIntent.value)
                is TransactionIntent.UpdateTransactionType ->
                    updateTransactionType(
                        transactionIntent.expenseType,
                    )

                else -> {}
            }
        }
    }

    private fun handleBackClick(navHostController: NavHostController) {
        if (!editTransactionState.value.changesFound) {
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

    private suspend fun updateTransactionType(value: String) {
        val transaction = editTransactionState.value.transaction
        val newCategoryList =
            if (initialTransactionState?.transaction?.type == value) {
                initialTransactionState?.categoryList ?: emptyList()
            } else {
                val changeModel = categoryModels.value.first { it.categoryType == value }.categoryId
                categoryModels.value.map { categoryModel ->
                    categoryModel.copy(
                        isSelected = categoryModel.categoryId == changeModel,
                        enable = categoryModel.categoryType == value,
                    )
                }
            }

        val selectedCategory =
            newCategoryList.first {
                it.isSelected
            }
        _editTransactionState.emit(
            editTransactionState.value.copy(
                transaction =
                transaction.copy(
                    type = value,
                    category = selectedCategory.category,
                    categoryId = selectedCategory.categoryId,
                    categoryIcon = selectedCategory.categoryIcon ?: R.drawable.mono,
                ),
                categoryList = newCategoryList,
            ),
        )
    }

    private suspend fun updateNote(value: String) {
        _editTransactionState.emit(
            editTransactionState.value.copy(
                transaction = editTransactionState.value.transaction.copy(note = value),
                note = value,
            ),
        )
    }

    private suspend fun saveImages(intent: TransactionIntent.SaveImage) {
        _editTransactionState.emit(
            editTransactionState.value.copy(
                transaction =
                editTransactionState.value.transaction.copy(
                    imagePath = intent.imagePath,
                    imageSource = intent.imageSource,
                    createdOn = intent.createdOn,
                ),
                image = intent.imagePath,
                imageSource = intent.imageSource,
                createdOn = intent.createdOn,
            ),
        )
        closeCameraAndGalleryWindow()
    }

    private fun deleteTransactionWithAlert(navHostController: NavHostController) {
        appStateManager.showAlertDialog(
            message = R.string.transaction_deleted_alert,
            onPositiveClick = {
                deleteTransaction()
                navHostController.navigateUp()
            },
        )
    }

    private suspend fun updateCalendarDialog(showDialog: Boolean) {
        _editTransactionState.emit(
            editTransactionState.value.copy(showCalendarDialog = showDialog),
        )
    }

    private fun updateTransaction(
        navHostController: NavHostController,
        context: Context,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateTransaction(editTransactionState.value.transaction)
            repository.getAllTransactionByMonth(
                getCurrentMonthYear().first,
                getCurrentMonthYear().second,
            )
        }.invokeOnCompletion {
            viewModelScope.launch {
                appStateManager.showToastState(toastMsg = R.string.transaction_updated)
                CameraFunction.clearPicturesFolder(context)
                navHostController.popBackStack()
            }
        }
    }

    private fun deleteTransaction() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTransaction(editTransactionState.value.transaction)
            repository.getAllTransactionByMonth(
                getCurrentMonthYear().first,
                getCurrentMonthYear().second,
            )
        }.invokeOnCompletion {
            appStateManager.showToastState(toastMsg = R.string.transaction_deleted)
        }
    }

    private suspend fun updateAmount(amountIntent: TextFieldCalculatorIntent) {
        val updatedState =
            when (amountIntent) {
                is TextFieldCalculatorIntent.OnHeightChange ->
                    editTransactionState.value.amountTfState.copy(
                        height = amountIntent.height,
                    )

                is TextFieldCalculatorIntent.OnValueChange ->
                    editTransactionState.value.amountTfState.copy(
                        amountValue = amountIntent.value,
                    )

                is TextFieldCalculatorIntent.OpenDialog ->
                    editTransactionState.value.amountTfState.copy(
                        openDialog = amountIntent.openDialog,
                    )
            }

        _editTransactionState.emit(
            editTransactionState.value.copy(amountTfState = updatedState),
        )
    }

    private suspend fun updateCategoryIntent(
        categoryIntent: EditCategoryIntent,
        navHostController: NavHostController,
    ) {
        when (categoryIntent) {
            is EditCategoryIntent.OnCategoryListChange -> {
                val selectedCategory = categoryIntent.list.first { it.isSelected }
                if (selectedCategory.categoryId != editTransactionState.value.transaction.categoryId) {
                    _editTransactionState.emit(
                        editTransactionState.value.copy(
                            categoryList = categoryIntent.list,
                            transaction =
                            editTransactionState.value.transaction.copy(
                                category = selectedCategory.category,
                                categoryId = selectedCategory.categoryId,
                                categoryIcon = selectedCategory.categoryIcon ?: R.drawable.mono,
                            ),
                        ),
                    )
                }
            }

            EditCategoryIntent.OnEditCategoryClicked -> navHostController.navigate(Screens.EditCategory.route)
        }
    }

    private suspend fun updateDate(date: Long) {
        val (month, year) = CommonFunctions.getMonthAndYearFromLong(date)
        _editTransactionState.emit(
            editTransactionState.value.copy(
                transaction =
                editTransactionState.value.transaction.copy(
                    date = date,
                    currentMonthId = month,
                    year = year,
                ),
                date = date,
            ),
        )
    }

    private suspend fun deleteImage() {
        _editTransactionState.emit(
            editTransactionState.value.copy(
                transaction =
                editTransactionState.value.transaction.copy(
                    imagePath = "",
                    imageSource = ImageSource.NONE,
                    createdOn = 0L,
                ),
                image = "",
                imageSource = ImageSource.NONE,
                createdOn = 0L,
            ),
        )
        appStateManager.showToastState(toastMsg = R.string.image_deleted)
    }

    private suspend fun closeCameraAndGalleryWindow() {
        viewModelScope.launch {
            if (editTransactionState.value.showCameraAndGallery) {
                _editTransactionState.emit(
                    editTransactionState.value.copy(showCameraAndGallery = false),
                )
            }
        }
    }

    private suspend fun openCameraAndGalleryWindow() {
        _editTransactionState.emit(
            editTransactionState.value.copy(showCameraAndGallery = true),
        )
    }

    private fun createTransactionState(transaction: DailyTransaction): TransactionState {
        return TransactionState(
            transaction = transaction,
            categoryList =
            categoryModels.value.map { categoryModel ->
                categoryModel.copy(
                    isSelected = categoryModel.categoryId == transaction.categoryId,
                    enable = categoryModel.categoryType == transaction.type,
                )
            },
            date = transaction.date,
            amountTfState =
            AmountTfState(
                currencyIcon = appConfigProperties.value.selectedCurrencyIconDrawable,
                amountValue = transaction.amount.toString(),
            ),
            note = transaction.note,
            image = transaction.imagePath,
            imageSource = transaction.imageSource,
            createdOn = transaction.createdOn,
            appStateManager = appStateManager,
        )
    }

    private infix fun TransactionState.isEqualTo(transactionState: TransactionState?): Boolean {
        return date != transactionState?.date ||
            amountTfState.amountValue != transactionState.amountTfState.amountValue ||
            note != transactionState.note ||
            transaction.type != transactionState.transaction.type ||
            !image.contentEquals(transactionState.image) ||
            categoryList != transactionState.categoryList
    }
}
