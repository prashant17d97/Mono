package com.debugdesk.mono.presentation.uicomponents

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SkipButton(
    modifier: Modifier = Modifier, text: String, onClick: () -> Unit
) {
    OutlinedButton(
        onClick = { onClick() },
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        modifier = modifier,
        shape = CircleShape,
        /*colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = )*/
    ) {
        Text(text = text, style = MaterialTheme.typography.titleMedium)
    }
}