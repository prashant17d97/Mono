package com.debugdesk.mono.presentation.uicomponents.notetf

sealed class NoteIntent {
    data class OnValueChange(val value: String) : NoteIntent()
    data object OnTrailIconClick : NoteIntent()
    data class DeleteImage(val absolutePath: List<String>) : NoteIntent()
    data class ShowGallery(val selectedIndex: Int = 0) : NoteIntent()
}