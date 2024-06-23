package com.debugdesk.mono.presentation.uicomponents.notetf

import com.debugdesk.mono.utils.enums.ImageSource

sealed class NoteIntent {
    data class OnValueChange(val value: String) : NoteIntent()
    data object OnTrailIconClick : NoteIntent()
    data class DeleteImage(val imagePath: ByteArray, val imageSource: ImageSource) : NoteIntent()

}