package com.debugdesk.mono.utils.states

import androidx.compose.material3.SnackbarDuration

data class SnackBarData(
    val message: String = "",
    val actionLabel: String? = null,
    val duration: SnackbarDuration = SnackbarDuration.Short,
    val withDismissAction: Boolean = false,
    val display: Boolean = false,
    val onActionClick: () -> Unit = {},
    val onDismissClick: () -> Unit = {},
) {
    companion object {
        val defaultSnackBarData = SnackBarData(display = false)
    }
}
