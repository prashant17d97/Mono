package com.debugdesk.mono.presentation.uicomponents


import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    openDialog: Boolean,
    openDialogChange: (Boolean) -> Unit,
    value: (Long) -> Unit
) {

    if (openDialog) {
        val datePickerState = rememberDatePickerState(
            yearRange = IntRange(2000, 2200),
        )

        DatePickerDialog(
            onDismissRequest = {
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onDismissRequest.
                openDialogChange(false)
            },
            properties = DialogProperties(),
            confirmButton = {
                TextButton(
                    onClick = {
                        openDialogChange(false)
                        value(datePickerState.selectedDateMillis ?: 0L)
                    },
                    enabled = true
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDialogChange(false)
                    }
                ) {
                    Text("Cancel")
                }
                value(datePickerState.selectedDateMillis ?: 0L)
            }
        ) {
            DatePicker(state = datePickerState, showModeToggle = false)
        }
    }
}