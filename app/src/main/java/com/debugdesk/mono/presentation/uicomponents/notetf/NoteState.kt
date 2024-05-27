package com.debugdesk.mono.presentation.uicomponents.notetf

import android.graphics.Bitmap

data class NoteState(
    val images: List<Bitmap> = emptyList(),
    val noteValue: String = ""
)