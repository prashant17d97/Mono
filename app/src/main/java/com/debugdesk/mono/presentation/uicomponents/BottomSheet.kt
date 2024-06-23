package com.debugdesk.mono.presentation.uicomponents

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
    show: Boolean = false,
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    AnimatedVisibility(
        visible = show,
        enter = slideInVertically { it },
        exit = slideOutVertically { it }) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = modelBottomSheet,
            dragHandle = { BottomSheetDefaults.DragHandle() },
            content = content
        )
    }
}