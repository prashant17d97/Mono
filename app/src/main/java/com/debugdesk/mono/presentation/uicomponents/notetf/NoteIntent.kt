package com.debugdesk.mono.presentation.uicomponents.notetf

sealed class NoteIntent {
    data class OnValueChange(val value: String) : NoteIntent()
    data object OnTrailIconClick : NoteIntent()
}