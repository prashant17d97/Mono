package com.debugdesk.mono.presentation.uicomponents

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    modelBottomSheet: SheetState = rememberModalBottomSheetState(),
    onDismiss: () -> Unit,
    show: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    AnimatedVisibility(visible = show) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = modelBottomSheet,
            dragHandle = { BottomSheetDefaults.DragHandle() },
            content = content
        )
    }
}