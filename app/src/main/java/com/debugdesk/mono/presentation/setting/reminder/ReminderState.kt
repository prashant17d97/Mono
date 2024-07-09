package com.debugdesk.mono.presentation.setting.reminder

import com.debugdesk.mono.R
import com.debugdesk.mono.utils.commonfunctions.TimeUtils.getTime
import com.debugdesk.mono.utils.enums.Buttons

data class ReminderState(
    val timeStamp: Long = 0L,
    val isScrolling: Boolean = false,
    private val isReminderActive: Boolean = false
) {
    private val time =
        (System.currentTimeMillis().takeIf { timeStamp <= 0L } ?: timeStamp).getTime()
    val hour = time[0]
    val minute = time[1]
    val buttonState: Buttons
        get() = when {
            isScrolling -> Buttons.Disable
            isReminderActive -> Buttons.Inactive
            else -> Buttons.Active
        }

    val buttonString: Int
        get() = if (!isReminderActive) R.string.setReminder else R.string.removeReminder
}

sealed class ReminderEvent {
    data object SetReminder : ReminderEvent()
    data object RemoveReminder : ReminderEvent()
    data class UpdateTime(val timeStamp: Long) : ReminderEvent()
    data class Scrolling(val isScrolling: Boolean) : ReminderEvent()
}
