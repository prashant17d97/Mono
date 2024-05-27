package com.debugdesk.mono.presentation.uicomponents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.toDate

@Composable
fun CalendarCard(
    date: Long,
    showDialog: Boolean = false,
    onShowCalendarDialog: (Boolean) -> Unit = {},
    onDateChange: (Long) -> Unit = {},
) {

    Box(contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onShowCalendarDialog(true) }
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer, shape = CircleShape
            )) {
        Text(
            text = date.toDate(), modifier = Modifier.padding(10.dp)
        )
    }
    DatePickerDialog(
        openDialog = showDialog,
        initial = date,
        openDialogChange = { onShowCalendarDialog(it) },
        value = { onDateChange(it) }
    )
}
