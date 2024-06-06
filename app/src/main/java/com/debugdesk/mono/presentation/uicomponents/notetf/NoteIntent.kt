package com.debugdesk.mono.presentation.uicomponents.notetf

import com.debugdesk.mono.domain.data.local.localdatabase.model.TransactionImage

sealed class NoteIntent {
    data class OnValueChange(val value: String) : NoteIntent()
    data object OnTrailIconClick : NoteIntent()
    data class DeleteImages(val transactionImage: List<TransactionImage>) : NoteIntent()
    data class ShowGallery(val selectedIndex: Int = 0) : NoteIntent()
    data class DeleteFromDB(val transactionImage: TransactionImage) : NoteIntent()

}