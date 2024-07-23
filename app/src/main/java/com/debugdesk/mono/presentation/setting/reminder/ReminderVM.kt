package com.debugdesk.mono.presentation.setting.reminder

import android.content.Context
import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.debugdesk.mono.R
import com.debugdesk.mono.model.ReminderTimeData
import com.debugdesk.mono.navigation.Screens
import com.debugdesk.mono.notification.MonoAlarmManger
import com.debugdesk.mono.ui.appconfig.AppConfigManager
import com.debugdesk.mono.ui.appconfig.AppStateManager
import com.debugdesk.mono.utils.commonfunctions.TimeUtils.calculateTimeStamps
import com.debugdesk.mono.utils.commonfunctions.TimeUtils.getTime
import com.debugdesk.mono.utils.commonfunctions.TimeUtils.getTimeString
import com.debugdesk.mono.utils.enums.Buttons
import com.debugdesk.mono.utils.states.SnackBarData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReminderVM(
    private val appStateManager: AppStateManager,
    private val alarmManger: MonoAlarmManger,
    private val appConfigManager: AppConfigManager,
) : ViewModel() {
    private val _state = MutableStateFlow(ReminderState())
    val state: StateFlow<ReminderState> get() = _state
    private var reminderTimeData: ReminderTimeData = ReminderTimeData()

    init {
        viewModelScope.launch {
            appConfigManager.reminderTimeData.collect {
                val time = if (it.timeStamp <= 0L) System.currentTimeMillis() else it.timeStamp
                reminderTimeData = it
                _state.value =
                    state.value.copy(
                        isReminderActive = it.isReminderActive,
                        timeStamp = time,
                    )
            }
        }
    }

    fun fetchAppInitialData() {
        appConfigManager.fetchAppInitialData()
    }

    fun handleEvent(
        event: ReminderEvent,
        context: Context,
    ) {
        when (event) {
            is ReminderEvent.SetReminder -> scheduled(context)
            is ReminderEvent.RemoveReminder -> cancelTimer()
            is ReminderEvent.UpdateTime -> updateTime(event)
            is ReminderEvent.Scrolling -> updateScrolling(event.isScrolling)
        }
    }

    private fun scheduled(context: Context) {
        alarmManger.cancelAlarm()
        appConfigManager.saveReminderTimeData(
            ReminderTimeData(
                timeStamp = state.value.timeStamp,
                buttonState = Buttons.Active,
                isReminderActive = true,
            ),
        )

        alarmManger.scheduleDailyNotification(
            hourOfDay = state.value.hour,
            minute = state.value.minute,
            title = context.getString(R.string.reminder),
            content = context.getString(R.string.reminder_content),
            targetScreen = Screens.Input,
        )

        val stringId =
            if (state.value.isBefore) {
                R.string.reminderCaptionBefore
            } else {
                R.string.reminderCaptionAfter
            }
        appStateManager.showSnackBar(
            snackBarData =
            SnackBarData(
                message =
                context.getString(
                    stringId,
                    state.value.timeStamp.getTimeString(),
                ),
                duration = SnackbarDuration.Short,
                display = true,
            ),
        )
    }

    private fun cancelTimer() {
        appConfigManager.saveReminderTimeData(
            ReminderTimeData(
                timeStamp = 0L,
                buttonState = Buttons.Active,
                isReminderActive = false,
            ),
        )
        _state.value =
            state.value.copy(
                canceled = !state.value.canceled,
            )
        appStateManager.showToastState(R.string.reminderRemoved)
        alarmManger.cancelAlarm()
    }

    private fun updateTime(timeStamp: ReminderEvent.UpdateTime) {
        val (timeLong, isBefore) =
            calculateTimeStamps(
                timeStamp.hour,
                timeStamp.minute,
                timeStamp.isPM,
            )
        val timeA = timeLong.getTime()
        val timeB = reminderTimeData.timeStamp.getTime()
        Log.d(
            "TAG",
            "updateTime: $isBefore",
        )

        if (!state.value.isScrolling) {
            _state.value =
                _state.value.copy(
                    timeStamp = timeLong,
                    isBefore = isBefore,
                    isReminderActive =
                    if (reminderTimeData.isReminderActive) {
                        timeA == timeB
                    } else {
                        false
                    },
                )
        }
    }

    private fun updateScrolling(isScrolling: Boolean) {
        _state.value =
            _state.value.copy(
                isScrolling = isScrolling,
            )
    }
}
