package com.debugdesk.mono.utils.commonfunctions

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object TimeUtils {
    private fun calculateTimeOfDayMillis(
        hourOfDay: Int,
        minute: Int,
    ): Pair<Long, Boolean> {
        var isBefore = false
        val calendar =
            Calendar.getInstance().apply {
                set(
                    Calendar.HOUR_OF_DAY,
                    hourOfDay,
                ) // Set the hour you want the notification to trigger
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                if (before(Calendar.getInstance())) {
                    isBefore = true
                    add(Calendar.DAY_OF_MONTH, 1)
                }
            }
        Log.d("TimeUtils", "calculateTimeOfDayMillis: $hourOfDay, $minute, $isBefore")
        return calendar.timeInMillis to isBefore
    }

    fun calculateTimeStamps(
        hourOfDay: Int,
        minute: Int,
        isPM: Boolean,
    ): Pair<Long, Boolean> {
        val hour =
            if (isPM && hourOfDay < 13) {
                hourOfDay + 12 // Convert PM hours to 24-hour format
            } else {
                hourOfDay
            } + 1
        return calculateTimeOfDayMillis(hour, minute)
    }

    fun Long.getTime(format: String = "HH:mm") = SimpleDateFormat(format, Locale.ENGLISH)
        .format(Date(this)).split(":").map { it.toInt() }

    fun Long.getTimeString(format: String = "hh:mm a"): String =
        SimpleDateFormat(format, Locale.ENGLISH).format(Date(this))
}
