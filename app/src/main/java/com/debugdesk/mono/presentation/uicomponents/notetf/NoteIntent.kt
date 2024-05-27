package com.debugdesk.mono.presentation.uicomponents.notetf

import android.graphics.Bitmap

sealed class NoteIntent {
    data class OnValueChange(val value: String) : NoteIntent()
    data object OnTrailIconClick : NoteIntent()
    data class DeleteImage(val bitmaps: List<Bitmap>) : NoteIntent()
    data class ShowGallery(val selectedIndex: Int = 0) : NoteIntent()
}