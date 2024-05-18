package com.debugdesk.mono.presentation.uicomponents

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun CharacterCard(character: String, itemPerRow: Int, action: (String) -> Unit) {
    val cardBackground = MaterialTheme.colorScheme.background
    val configuration = LocalConfiguration.current
    val size =
        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) (configuration.screenWidthDp - 70) / itemPerRow
        else (configuration.screenWidthDp - 70) / (itemPerRow) * 2

    Box(
        modifier = Modifier
            .size(size.dp)
            .clickable {
                action(character)
            }
            .background(
                color = when (character) {
                    "+", "-", "x", "/", "=" -> cardBackground/*.copy(alpha = 0.9f)*/
                    else -> cardBackground
                }
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = character,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall,
        )
    }

}
