package com.debugdesk.mono.presentation.uicomponents

import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.debugdesk.mono.utils.Dp
import com.debugdesk.mono.utils.states.AlertState

@Composable
fun AlertStateDialog(alertState: AlertState) {
    if (alertState.show) {
        AlertDialog(
            properties = alertState.properties,
            icon = alertState.iconDrawable?.let {
                {
                    Icon(
                        painter = painterResource(id = it),
                        contentDescription = "Icon",
                        modifier = Modifier.size(Dp.dp48),
                        tint = alertState.iconColor
                    )
                }
            },
            title = {
                Text(
                    text = stringResource(id = alertState.title),
                )
            },
            text = {
                Text(
                    text = stringResource(id = alertState.message),
                )
            },
            onDismissRequest = alertState.onNegativeClick,
            confirmButton = {
                Button(onClick = alertState.onPositiveClick) {
                    Text(text = stringResource(id = alertState.positiveButtonText))
                }
            },
            dismissButton = {
                Button(onClick = alertState.onNegativeClick) {
                    Text(text = stringResource(id = alertState.negativeButtonText))
                }
            }
        )
    }
}

@Preview(showBackground = true, showSystemUi = false, backgroundColor = 0xFFFFFFFF)
@Composable
fun AlertStatePre() {
    MaterialTheme {
        AlertStateDialog(
            alertState = AlertState(show = true)
        )
    }
}