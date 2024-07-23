package com.debugdesk.mono.presentation.uicomponents

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.debugdesk.mono.R
import com.debugdesk.mono.utils.states.AlertState
import com.debugdesk.mono.utils.states.Drawable

@Composable
fun AlertStateDialog(alertState: AlertState) {
    if (alertState.show) {
        Log.d("Dialog", "AlertStateDialog: ")
        AlertDialog(
            properties = alertState.properties,
            icon = alertState.iconCompose,
            title = {
                Text(
                    text = stringResource(id = alertState.title),
                )
            },
            text = {
                Text(
                    text = stringResource(id = alertState.message),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = if (alertState.showIcon) TextAlign.Center else TextAlign.Start,
                )
            },
            onDismissRequest = alertState.onNegativeClick,
            confirmButton = {
                if (alertState.positiveText.isNotEmpty()) {
                    Button(onClick = alertState.onPositiveClick) {
                        Text(text = alertState.positiveText)
                    }
                }
            },
            dismissButton = {
                if (alertState.negativeText.isNotEmpty()) {
                    Button(onClick = alertState.onNegativeClick) {
                        Text(text = alertState.negativeText)
                    }
                }
            },
        )
    }
}

@Preview(showBackground = true, showSystemUi = false, backgroundColor = 0xFFFFFFFF)
@Composable
fun AlertStatePre() {
    PreviewTheme {
        AlertStateDialog(
            alertState =
            AlertState(
                show = true,
                drawable = Drawable.Animated(R.drawable.ringer_bell),
                showIcon = false,
                negativeButtonText = R.string.empty,
            ),
        )
    }
}
