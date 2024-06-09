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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.debugdesk.mono.R
import com.debugdesk.mono.utils.Dp.dp10
import com.debugdesk.mono.utils.SP.sp48

@Composable
fun MonoColumn(
    modifier: Modifier = Modifier,
    heading: String = "",
    trailing: String = "",
    trailingColor: Color = MaterialTheme.colorScheme.primary,
    showBack: Boolean = false,
    enableClick: Boolean = true,
    onBackClick: () -> Unit = {},
    onTrailClick: () -> Unit = {},
    headingStyle: TextStyle = MaterialTheme.typography.titleLarge,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    scrollState: ScrollState = rememberScrollState(),
    isScrollEnabled: Boolean = true,
    top: Dp = dp10,
    bottom: Dp = top,
    start: Dp = top,
    end: Dp = top,
    headerBotPadding :Dp= dp10,
    trailingCompose: @Composable () -> Unit = {
        if (trailing != "") {
            Text(text = trailing,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge.copy(color = trailingColor),
                modifier = Modifier
                    .clickable {
                        if (enableClick) {
                            onTrailClick()
                        }
                    }
                    .padding(horizontal = 2.dp))
        }
    },
    header: @Composable () -> Unit = {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = headerBotPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            if (showBack) {
                Text(text = stringResource(id = R.string.back),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = sp48,
                    modifier = Modifier
                        .clickable {
                            if (enableClick) {
                                onBackClick()
                            }
                        }
                        .padding(horizontal = 2.dp))
            }
            if (heading != "") {
                Text(
                    text = heading,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                    style = headingStyle
                )
            }
            trailingCompose()
        }
    },
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
        header()

        content()
    }
}