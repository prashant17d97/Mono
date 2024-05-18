package com.debugdesk.mono.presentation.edittrans

import com.debugdesk.mono.presentation.uicomponents.amounttf.TextFieldCalculatorIntent
import com.debugdesk.mono.presentation.uicomponents.editcategory.EditCategoryIntent
import com.debugdesk.mono.presentation.uicomponents.notetf.NoteIntent

sealed class EditTransactionIntent {
    data class UpdateDate(val date: Long) : EditTransactionIntent()
    data class OpenCalendarDialog(val showDialog: Boolean) : EditTransactionIntent()
    data class UpdateAmount(val amountTFIntent: TextFieldCalculatorIntent) : EditTransactionIntent()
    data class UpdateNote(val noteIntent: NoteIntent) : EditTransactionIntent()
    data class UpdateCategoryIntent(val editCategoryIntent: EditCategoryIntent) :
        EditTransactionIntent()

    data object OnUpdateClick : EditTransactionIntent()
    data object OnDeleteClick : EditTransactionIntent()
    data object OnBackClick : EditTransactionIntent()
}