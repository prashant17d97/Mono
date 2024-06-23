package com.debugdesk.mono.presentation.input.state

import com.debugdesk.mono.domain.data.local.localdatabase.model.CategoryModel
import com.debugdesk.mono.domain.data.local.localdatabase.model.DailyTransaction
import com.debugdesk.mono.domain.data.local.localdatabase.model.emptyTransaction
import com.debugdesk.mono.presentation.uicomponents.amounttf.AmountTfState
import com.debugdesk.mono.presentation.uicomponents.notetf.NoteState
import com.debugdesk.mono.ui.appconfig.AppStateManager
import com.debugdesk.mono.ui.appconfig.AppStateManagerImpl
import com.debugdesk.mono.utils.enums.ExpenseType

data class InputState(
    val transaction: DailyTransaction = emptyTransaction,
    val amountTfState: AmountTfState = AmountTfState(),
    val noteState: NoteState = NoteState(),
    val categoryList: List<CategoryModel> = emptyList(),
    val showCalendarDialog: Boolean = false,
    val showCameraAndGallery: Boolean = false,
    val changesFound: Boolean = false,
    val clickedIndex: Int = 0,
    val appStateManager: AppStateManager = AppStateManagerImpl(),
    val transactionType: ExpenseType = ExpenseType.Expense
) {
    private val expenseCategory: List<CategoryModel> get() = categoryList.filter { it.categoryType == ExpenseType.Expense.name }
    private val incomeCategory: List<CategoryModel> get() = categoryList.filter { it.categoryType == ExpenseType.Income.name }

    val inputCategories = { index: Int -> expenseCategory.takeIf { index == 0 } ?: incomeCategory }
}
