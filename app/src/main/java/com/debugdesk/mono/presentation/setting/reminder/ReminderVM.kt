package com.debugdesk.mono.presentation.setting.reminder

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.debugdesk.mono.R
import com.debugdesk.mono.model.RemainderTimeData
import com.debugdesk.mono.navigation.Screens
import com.debugdesk.mono.notification.NotificationScheduler
import com.debugdesk.mono.ui.appconfig.AppConfigManager
import com.debugdesk.mono.ui.appconfig.AppStateManager
import com.debugdesk.mono.utils.commonfunctions.TimeUtils.getTime
import com.debugdesk.mono.utils.enums.Buttons
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReminderVM(
    private val appStateManager: AppStateManager,
    private val notificationScheduler: NotificationScheduler,
    private val appConfigManager: AppConfigManager
) : ViewModel() {
    private val _state = MutableStateFlow(ReminderState())
    val state: StateFlow<ReminderState> get() = _state
    private var remainderTimeData: RemainderTimeData = RemainderTimeData()

    init {
        viewModelScope.launch {
            appConfigManager.remainderTimeData.collect {
                Log.d("ReminderVM", "init: ${it}, ${it.timeStamp.getTime()}")
                remainderTimeData = it
                _state.value = state.value.copy(
                    isReminderActive = it.isReminderActive,
                    timeStamp = it.timeStamp
                )
            }
        }
    }

    fun fetchAppInitialData() {
        appConfigManager.fetchAppInitialData()

    }

    fun handleEvent(event: ReminderEvent, context: Context) {
        when (event) {
            is ReminderEvent.SetReminder -> scheduled(context)
            is ReminderEvent.RemoveReminder -> cancelTimer()
            is ReminderEvent.UpdateTime -> updateTime(event.timeStamp)
            is ReminderEvent.Scrolling -> updateScrolling(event.isScrolling)
        }
    }


    private fun scheduled(context: Context) {
        notificationScheduler.cancelScheduledNotification()
        appConfigManager.saveReminderTimeData(
            RemainderTimeData(
                timeStamp = state.value.timeStamp,
                buttonState = Buttons.Active,
                isReminderActive = true
            )
        )
        notificationScheduler.scheduleNotification(
            timeOfDayMillis = state.value.timeStamp,
            title = context.getString(R.string.reminder),
            content = context.getString(R.string.reminder_content),
            targetScreen = Screens.Input
        )
        appStateManager.showToastState(R.string.reminderCaption)
    }

    private fun cancelTimer() {
        appConfigManager.saveReminderTimeData(
            RemainderTimeData(
                timeStamp = 0L,
                buttonState = Buttons.Active,
                isReminderActive = false
            )
        )
        appStateManager.showToastState(R.string.reminderRemoved)
        notificationScheduler.cancelScheduledNotification()
    }


    private fun updateTime(timeStamp: Long) {
        val timeA = timeStamp.getTime()
        val timeB = remainderTimeData.timeStamp.getTime()
        Log.d("TAG", "updateTime: $timeA, $timeB ${timeA == timeB}")

        _state.value = _state.value.copy(
            timeStamp = timeStamp,
            isReminderActive = if (remainderTimeData.isReminderActive){
                timeA == timeB
            }else{
                false
            }
        )
    }

    private fun updateScrolling(isScrolling: Boolean) {
        _state.value = _state.value.copy(
            isScrolling = isScrolling,
        )
    }
}
