package com.debugdesk.mono.presentation.uicomponents

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.debugdesk.mono.utils.CommonColor.disableButton
import com.debugdesk.mono.utils.Tabs.tabs

@Composable
fun CustomTabs(
    modifier: Modifier = Modifier,
    list: List<Int>,
    selectedIndex: Int,
    onClick: (Int) -> Unit,
) {
    TabRow(
        modifier = modifier,
        selectedTabIndex = selectedIndex,
        containerColor = MaterialTheme.colorScheme.background,
        indicator = { tabPositions ->
            list.forEachIndexed { index, _ ->
                TabRowDefaults.SecondaryIndicator(
                    height = (1).dp,
                    modifier = Modifier.tabIndicatorOffset(tabPositions[index]),
                    color =
                    MaterialTheme.colorScheme.primary.takeIf { selectedIndex == index }
                        ?: disableButton,
                )
            }
        },
    ) {
        list.forEachIndexed { index, text ->
            Tab(
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = disableButton,
                selected = selectedIndex == index,
                onClick = {
                    onClick(index)
                },
                text = {
                    Text(
                        text = stringResource(id = text),
                        style =
                        MaterialTheme.typography.titleMedium.copy(
                            color =
                            MaterialTheme.colorScheme.primary.takeIf { selectedIndex == index }
                                ?: disableButton,
                        ),
                        fontWeight = FontWeight.Bold,
                    )
                },
            )
        }
    }
}

@Preview
@Composable
private fun TabPrev() {
    PreviewTheme {
        CustomTabs(list = tabs, selectedIndex = 0) {
        }
    }
}
