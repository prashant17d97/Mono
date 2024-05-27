package com.debugdesk.mono.presentation.uicomponents

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.debugdesk.mono.R

@Composable
fun ScreenView(
    modifier: Modifier = Modifier,
    heading: String = "",
    trailing: String = "",
    trailingColor: Color = MaterialTheme.colorScheme.primary,
    showBack: Boolean = false,
    onBackClick: () -> Unit = {},
    onTrailClick: () -> Unit = {},
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    scrollState: ScrollState = rememberScrollState(),
    isScrollEnabled: Boolean = true,
    bottom: Dp = 10.dp,
    start: Dp = 10.dp,
    top: Dp = 10.dp,
    end: Dp = 10.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        modifier = (modifier
            .fillMaxWidth()
            .padding(top = top, bottom = bottom, start = start, end = end)
            .verticalScroll(scrollState)).takeIf { isScrollEnabled } ?: (modifier.padding(
            top = top, bottom = bottom, start = start, end = end
        ))) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            if (showBack) {
                Text(text = stringResource(id = R.string.back),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .clickable { onBackClick() }
                        .padding(horizontal = 2.dp))
            }
            if (heading != "") {
                Text(
                    text = heading,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleLarge
                )
            }
            if (trailing != "") {
                Text(text = trailing,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge.copy(color = trailingColor),
                    modifier = Modifier
                        .clickable { onTrailClick() }
                        .padding(horizontal = 2.dp))
            }
        }

        content()
    }
}