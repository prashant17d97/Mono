package com.debugdesk.mono.presentation.uicomponents.notetf

import com.debugdesk.mono.domain.data.local.localdatabase.model.TransactionImage

data class NoteState(
    val transactionImages: List<TransactionImage> = emptyList(),
    val noteValue: String = ""
)