package com.debugdesk.mono.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.debugdesk.mono.notification.NotificationObjects.CONTENT
import com.debugdesk.mono.notification.NotificationObjects.NOTIFICATION_TITLE
import com.debugdesk.mono.notification.NotificationObjects.TARGET_SCREEN
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NotificationReceiver : BroadcastReceiver(), KoinComponent {
    private val notificationHelper: NotificationHelper by inject()

    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        val title = intent.extras?.getString(NOTIFICATION_TITLE)
        val content = intent.extras?.getString(CONTENT)
        val targetScreen = intent.extras?.getString(TARGET_SCREEN)
        Log.d(
            "NotificationReceiver",
            "onReceive: ${intent.data} $title $content, $targetScreen",
        )
        notificationHelper.showNotification(title, content, targetScreen)
    }
}
