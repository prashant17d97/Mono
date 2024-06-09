package com.debugdesk.mono.presentation.uicomponents

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.debugdesk.mono.utils.Dp.dp2
import com.debugdesk.mono.utils.SP.sp48

@Composable
fun DropDown(
    expanded: Boolean = false,
    selectedValue: String, items: List<String>,
    onSelected: (String, index: Int) -> Unit,
    onExpend: (Boolean) -> Unit = {},
    heading: @Composable () -> Unit = {
        Text(
            text = selectedValue,
            modifier = Modifier.clickable { onExpend(!expanded) },
            lineHeight = sp48
        )

    }
) {
    Box {
        heading()
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                onExpend(false)
            }) {
            items.forEachIndexed { index, item ->
                DropdownMenuItem(onClick = {
                    onSelected(item, index)
                    onExpend(false)
                }, text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(
                            space = dp2, alignment = Alignment.CenterHorizontally
                        )
                    ) {
                        Text(item)
                        AnimatedVisibility(visible = item == selectedValue) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                })
            }
        }
    }
}