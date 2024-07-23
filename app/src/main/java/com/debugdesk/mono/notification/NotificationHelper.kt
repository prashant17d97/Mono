package com.debugdesk.mono.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.debugdesk.mono.R
import com.debugdesk.mono.main.MainActivity
import com.debugdesk.mono.notification.NotificationObjects.MONO_NOTIFICATION
import com.debugdesk.mono.notification.NotificationObjects.NOTIFICATION_CHANNEL
import com.debugdesk.mono.notification.NotificationObjects.TARGET_SCREEN

class NotificationHelper(
    private val context: Context,
    private val notificationManager: NotificationManager,
) {
    fun showNotification(
        title: String?,
        content: String?,
        targetScreen: String?,
    ) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    NOTIFICATION_CHANNEL,
                    MONO_NOTIFICATION,
                    NotificationManager.IMPORTANCE_HIGH,
                )
            notificationManager.createNotificationChannel(channel)
        }

        val intent =
            Intent(context, MainActivity::class.java).apply {
                putExtra(TARGET_SCREEN, targetScreen)
            }
        val pendingIntent =
            PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        val notificationBuilder =
            NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
                .setSmallIcon(R.drawable.mono)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

        notificationManager.notify(1, notificationBuilder.build())
    }
}
