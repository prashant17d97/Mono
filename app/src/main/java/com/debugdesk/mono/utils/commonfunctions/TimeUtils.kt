package com.debugdesk.mono.utils.commonfunctions

import android.util.Log
import com.debugdesk.mono.utils.commonfunctions.TimeUtils.getTimeString
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object TimeUtils {
    private fun calculateTimeOfDayMillis(hourOfDay: Int, minute: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    fun calculateTimeStamps(
        hourOfDay: Int,
        minute: Int,
        isPM: Boolean
    ): Long {
        Log.d("TAG", "calculateTimeStamps: $hourOfDay, $minute")
        val hour = if (isPM && hourOfDay < 13) {
            hourOfDay + 12 // Convert PM hours to 24-hour format
        } else {
            hourOfDay
        }+1
        return calculateTimeOfDayMillis(hour, minute)
    }

    fun Long.getTime(format: String = "HH:mm") =
        SimpleDateFormat(format, Locale.ENGLISH).format(Date(this)).split(":").map { it.toInt() }

    fun Long.getTimeString(format: String = "HH:mm a"): String =
        SimpleDateFormat(format, Locale.ENGLISH).format(Date(this))
}


fun main() {
    println(System.currentTimeMillis().getTimeString("HH:mm a"))
}