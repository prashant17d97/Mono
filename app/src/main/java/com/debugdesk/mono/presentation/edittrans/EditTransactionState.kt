package com.debugdesk.mono.presentation.edittrans

import com.debugdesk.mono.domain.data.local.localdatabase.model.CategoryModel
import com.debugdesk.mono.domain.data.local.localdatabase.model.DailyTransaction
import com.debugdesk.mono.domain.data.local.localdatabase.model.emptyTransaction
import com.debugdesk.mono.presentation.uicomponents.amounttf.AmountTfState
import com.debugdesk.mono.presentation.uicomponents.notetf.NoteState

data class EditTransactionState(
    val transaction: DailyTransaction = emptyTransaction,
    val showCalendarDialog: Boolean = false,
    val amountTfState: AmountTfState = AmountTfState(),
    val noteState: NoteState = NoteState(),
    val categoryList: List<CategoryModel> = emptyList()
)