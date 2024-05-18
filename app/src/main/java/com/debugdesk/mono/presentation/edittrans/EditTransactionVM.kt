package com.debugdesk.mono.presentation.edittrans

import android.util.Log
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
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.double
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.getCurrentMonthYear
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class EditTransactionVM(
    private val appStateManager: AppStateManager,
    private val appConfigManager: AppConfigManager,
    private val repository: Repository,
) : ViewModel() {

    private val _editTransactionState = MutableStateFlow(EditTransactionState())
    val editTransactionState: StateFlow<EditTransactionState> = _editTransactionState

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
                EditTransactionState(
                    transaction = transaction,
                    amountTfState = AmountTfState(
                        currencyIcon = appConfigProperties.currencyIcon,
                        amountValue = transaction.amount.toString(),
                    ), noteState = NoteState(
                        images = transaction.images,
                        noteValue = transaction.note
                    ), categoryList = categoryModels.map { categoryModel ->
                        categoryModel.copy(
                            isSelected = categoryModel.categoryId == transaction.categoryId
                        )
                    }
                )
            }.collect {
                _editTransactionState.tryEmit(it)
            }
        }
    }

    fun handleTransactionIntent(
        transactionIntent: EditTransactionIntent,
        navHostController: NavHostController
    ) {
        when (transactionIntent) {
            EditTransactionIntent.OnBackClick -> navHostController.popBackStack()
            EditTransactionIntent.OnDeleteClick -> deleteTransaction(
                navHostController
            )

            EditTransactionIntent.OnUpdateClick -> updateTransaction(navHostController)
            is EditTransactionIntent.OpenCalendarDialog -> updateCalendarDialog(transactionIntent.showDialog)
            is EditTransactionIntent.UpdateAmount -> updateAmount(transactionIntent.amountTFIntent)
            is EditTransactionIntent.UpdateCategoryIntent -> updateCategoryIntent(
                transactionIntent.editCategoryIntent,
                navHostController
            )

            is EditTransactionIntent.UpdateDate -> updateDate(transactionIntent.date)
            is EditTransactionIntent.UpdateNote -> updateNote(transactionIntent.noteIntent)
        }
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
                getCurrentMonthYear().first,
                getCurrentMonthYear().second
            )
        }.invokeOnCompletion {
            appStateManager.showToastState(toastMsg = R.string.transaction_updated)
        }
        navHostController.popBackStack()
    }

    private fun deleteTransaction(
        navHostController: NavHostController
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTransaction(
                editTransactionState.value.transaction
            )
            repository.getAllTransactionByMonth(
                getCurrentMonthYear().first,
                getCurrentMonthYear().second
            )
        }.invokeOnCompletion {
            appStateManager.showToastState(toastMsg = R.string.transaction_deleted)
        }
        navHostController.popBackStack()
    }

    private fun updateNote(noteIntent: NoteIntent) {
        when (noteIntent) {
            NoteIntent.OnTrailIconClick -> {
                Log.e("TAG", "OpenCamera: ")
            }

            is NoteIntent.OnValueChange -> {
                _editTransactionState.tryEmit(
                    editTransactionState.value.copy(
                        transaction = editTransactionState.value.transaction.copy(
                            note = noteIntent.value
                        ),
                        noteState = editTransactionState.value.noteState.copy(
                            noteValue = noteIntent.value
                        )
                    )
                )
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
                        ),
                        amountTfState = editTransactionState.value.amountTfState.copy(
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
        categoryIntent: EditCategoryIntent,
        navHostController: NavHostController
    ) {
        when (categoryIntent) {
            is EditCategoryIntent.OnCategoryListChange -> {
                _editTransactionState.tryEmit(
                    editTransactionState.value.copy(
                        categoryList = categoryIntent.list
                    )
                )
            }

            EditCategoryIntent.OnEditCategoryClicked -> {
                navHostController.navigate(Screens.EditCategory.route)
            }
        }
    }

    private fun updateDate(date: Long) {
        _editTransactionState.tryEmit(
            editTransactionState.value.copy(
                transaction = editTransactionState.value.transaction.copy(
                    date = date
                )
            )
        )
    }
}