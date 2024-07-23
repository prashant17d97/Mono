package com.debugdesk.mono.presentation.uicomponents

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.debugdesk.mono.utils.CommonColor.disableButton
import com.debugdesk.mono.utils.CommonColor.inActiveButton
import com.debugdesk.mono.utils.enums.Buttons

@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    status: Buttons = Buttons.Active,
    text: String,
    onClick: (Buttons) -> Unit,
) {
    val (color, contentColor) =
        when (status) {
            Buttons.Active -> MaterialTheme.colorScheme.primary to Color.White
            Buttons.Inactive -> inActiveButton to Color.White
            Buttons.Disable -> disableButton to MaterialTheme.colorScheme.onPrimaryContainer
        }
    Button(
        onClick = { onClick(status) },
        modifier = modifier,
        enabled = true.takeIf { status != Buttons.Disable } ?: false,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(color),
    ) {
        Text(text = text, style = MaterialTheme.typography.titleMedium, color = contentColor)
    }
}
