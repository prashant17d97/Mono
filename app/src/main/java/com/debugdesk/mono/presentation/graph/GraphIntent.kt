package com.debugdesk.mono.presentation.graph

sealed class GraphIntent {
    data class UpdateTab(val tabs: Tabs) : GraphIntent()
    data object NavigateBack : GraphIntent()
    data object PromptFilter : GraphIntent()
    data object HideFilter : GraphIntent()
}
