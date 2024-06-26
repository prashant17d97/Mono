package com.debugdesk.mono.presentation.edittrans

import com.debugdesk.mono.presentation.uicomponents.amounttf.TextFieldCalculatorIntent
import com.debugdesk.mono.presentation.uicomponents.editcategory.EditCategoryIntent
import com.debugdesk.mono.presentation.uicomponents.notetf.NoteIntent
import com.debugdesk.mono.utils.enums.ExpenseType
import com.debugdesk.mono.utils.enums.ImageSource

sealed class TransactionIntent {
    data class UpdateDate(val date: Long) : TransactionIntent()
    data class OpenCalendarDialog(val showDialog: Boolean) : TransactionIntent()
    data class UpdateAmount(val amountTFIntent: TextFieldCalculatorIntent) : TransactionIntent()
    data class UpdateNote(val noteIntent: NoteIntent) : TransactionIntent()
    data class UpdateCategoryIntent(val editCategoryIntent: EditCategoryIntent) :
        TransactionIntent()

    data object OnUpdateClick : TransactionIntent()
    data class OnNewTransactionSaveClick(val type: ExpenseType) : TransactionIntent()
    data object OnDeleteClick : TransactionIntent()
    data object OnBackClick : TransactionIntent()
    data object DismissCameraAndGalleryWindow : TransactionIntent()
    data class SaveImage(
        val imagePath: ByteArray,
        val imageSource: ImageSource,
        val createdOn: Long = System.currentTimeMillis()
    ) :
        TransactionIntent()

    data object DismissCameraGallery : TransactionIntent()
}