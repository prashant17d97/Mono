package com.debugdesk.mono.presentation.uicomponents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.debugdesk.mono.R
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Calendar.JANUARY
import java.util.Locale

@Composable
fun Calendars() {
    var selectedDate by rememberSaveable {
        mutableStateOf(LocalDate.now())
    }
    val today = LocalDate.of(2024, JANUARY, 7)
    val daysOfWeek = stringArrayResource(id = R.array.weeks)
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val width = screenWidth / 7

    val monthList = stringArrayResource(id = R.array.months)
    Column(
        modifier = Modifier
            .width(screenWidth.dp)
    ) {
        // Display the current month and year
        Text(
            "${monthList[today.month.value - 1]} ${today.year}",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp)
        )

        // Display the days of the week
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            daysOfWeek.forEach { day ->
                Text(
                    day,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .width(width.dp)
                        .weight(1f)
                )
            }
        }

        // Display the calendar grid
        var week = today.with(WeekFields.of(Locale.US).dayOfWeek(), 1).minusWeeks(1)
        while (week.isBefore(
                today.with(WeekFields.of(Locale.US).dayOfWeek(), 7).plusWeeks(4)
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (i in 0 until 7) {
                    val day = week.plusDays(i.toLong())

                    // Check if the day is the selected date
                    val isSelected = day == selectedDate

                    // Display the day as a button
                    Box(contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(width.dp)
                            .weight(1f)
                            .background(
                                color = if (isSelected) Color.Cyan else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { selectedDate = day }) {
                        Text(text = day.dayOfMonth.toString()
                            .takeIf { day.month == today.month } ?: "",
                            style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            week = week.plusWeeks(1)
        }
    }
}