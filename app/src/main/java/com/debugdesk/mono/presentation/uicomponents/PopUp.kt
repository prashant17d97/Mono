package com.debugdesk.mono.presentation.uicomponents

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun PopUp(
    dismiss: () -> Unit,
    properties: DialogProperties = DialogProperties(dismissOnClickOutside = false),
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = { dismiss() },
        properties = properties,
    ) {
        content()
    }
}
