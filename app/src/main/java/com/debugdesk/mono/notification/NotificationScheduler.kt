package com.debugdesk.mono.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.debugdesk.mono.navigation.Screens
import com.debugdesk.mono.notification.NotificationObjects.CONTENT
import com.debugdesk.mono.notification.NotificationObjects.MONO_DAILY_NOTIFICATION
import com.debugdesk.mono.notification.NotificationObjects.NOTIFICATION_TITLE
import com.debugdesk.mono.notification.NotificationObjects.TARGET_SCREEN
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit

class NotificationScheduler(private val context: Context) : KoinComponent {
    fun scheduleNotification(
        timeOfDayMillis: Long,
        title: String,
        content: String,
        targetScreen: Screens,
    ) {
        val initialDelay = calculateInitialDelay(timeOfDayMillis)

        // Create a periodic work request to repeat every day
        val workRequest =


            PeriodicWorkRequestBuilder<NotificationWorker>(


                repeatInterval = 1, // Repeat interval in days

                repeatIntervalTimeUnit = TimeUnit.DAYS,
                flexTimeInterval = 10, // Flex interval ensures work is not deferred unnecessarily

                flexTimeIntervalUnit = TimeUnit.MINUTES,
            ).setInitialDelay(initialDelay, TimeUnit.MILLISECONDS).setInputData(


                workDataOf(
                    NOTIFICATION_TITLE to title,
                    CONTENT to content,
                    TARGET_SCREEN to targetScreen.route,
                ),
            ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            MONO_DAILY_NOTIFICATION,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest,
        )
    }

    fun cancelScheduledNotification() {
        WorkManager.getInstance(context).cancelUniqueWork(MONO_DAILY_NOTIFICATION)
    }

    private fun calculateInitialDelay(timeOfDayMillis: Long): Long {
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis > timeOfDayMillis) {
            // Time has already passed today, schedule for tomorrow
            val nextDay = TimeUnit.DAYS.toMillis(1)
            return nextDay - (currentTimeMillis - timeOfDayMillis)
        } else {
            // Schedule for today
            return timeOfDayMillis - currentTimeMillis
        }
    }

    class NotificationWorker(context: Context, params: WorkerParameters) :
        CoroutineWorker(context, params), KoinComponent {
        private val notificationHelper: NotificationHelper by inject()

        override suspend fun doWork(): Result {
            val title = inputData.getString(NOTIFICATION_TITLE)
            val content = inputData.getString(CONTENT)
            val targetScreen = inputData.getString(TARGET_SCREEN)
            notificationHelper.showNotification(title, content, targetScreen)
            return Result.success()
        }
    }
}
