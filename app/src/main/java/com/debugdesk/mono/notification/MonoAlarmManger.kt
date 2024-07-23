package com.debugdesk.mono.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.debugdesk.mono.navigation.Screens
import com.debugdesk.mono.notification.NotificationObjects.CONTENT
import com.debugdesk.mono.notification.NotificationObjects.NOTIFICATION_TITLE
import com.debugdesk.mono.notification.NotificationObjects.TARGET_SCREEN
import java.util.Calendar

class MonoAlarmManger(private val context: Context) {
    fun scheduleDailyNotification(
        hourOfDay: Int,
        minute: Int,
        title: String,
        content: String,
        targetScreen: Screens,
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Calculate initial time for today at the specified hour and minute
        val calendar =
            Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, hourOfDay)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)

                // Check if the specified time has already passed today
                if (timeInMillis <= System.currentTimeMillis()) {
                    // If yes, schedule for tomorrow
                    add(Calendar.DAY_OF_MONTH, 1)
                }
            }

        val intent =
            Intent(context, NotificationReceiver::class.java).apply {
                putExtra(NOTIFICATION_TITLE, title)
                putExtra(CONTENT, content)
                putExtra(TARGET_SCREEN, targetScreen.route)
            }

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        // Schedule the alarm to repeat daily
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent,
        )
    }

    fun cancelAlarm() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }
}
