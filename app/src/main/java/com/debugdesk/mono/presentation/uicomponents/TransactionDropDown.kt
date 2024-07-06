package com.debugdesk.mono.presentation.uicomponents

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.PopupProperties
import com.debugdesk.mono.R
import com.debugdesk.mono.utils.enums.ExpenseType

@Composable
fun TransactionDropDown(
    modifier: Modifier = Modifier,
    isExpended: Boolean = false,
    selectedTransaction: String = ExpenseType.Income.name,
    onDropDownClick: (String) -> Unit = {},
    dismiss: () -> Unit = {},
) {
    val strings = stringArrayResource(id = R.array.expenses)
    Column {
        DropDownItem(
            modifier = modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.onPrimary,
                    shape = MaterialTheme.shapes.large
                ),
            text = selectedTransaction,
            icon = Icons.Default.KeyboardArrowDown,
            onDropDownClick = onDropDownClick
        )
        DropdownMenu(
            modifier = modifier.fillMaxWidth(),
            expanded = isExpended,
            properties = PopupProperties(
                dismissOnBackPress = true, dismissOnClickOutside = true
            ), onDismissRequest = dismiss
        ) {
            strings.forEachIndexed { index, transactionType ->
                DropDownItem(
                    modifier = Modifier.fillMaxWidth(),
                    text = transactionType,
                    visible = transactionType == selectedTransaction,
                    onDropDownClick = onDropDownClick
                )
                if (strings.lastIndex != index) {
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun DropDownItem(
    modifier: Modifier = Modifier,
    text: String,
    visible: Boolean = true,
    icon: ImageVector = Icons.Default.Check,
    onDropDownClick: (String) -> Unit
) {
    DropdownMenuItem(
        modifier = modifier,
        onClick = {
            onDropDownClick(text)
        }, text = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = text)
                AnimatedVisibility(
                    visible = visible
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "Selected Theme"
                    )
                }
            }
        }
    )
}

@Preview
@Composable
private fun TransactionDropPrev() {
    var isExpended by remember {
        mutableStateOf(false)
    }

    var string by remember {
        mutableStateOf(ExpenseType.Income.name)
    }
    PreviewTheme {
        TransactionDropDown(isExpended = isExpended,
            selectedTransaction = string,
            onDropDownClick = {
                isExpended = !isExpended
                string = it
            })
    }
}