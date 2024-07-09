package com.debugdesk.mono.model

import com.debugdesk.mono.utils.enums.Buttons

data class RemainderTimeData(
    val timeStamp: Long = System.currentTimeMillis(),
    val buttonState: Buttons = Buttons.Active,
    val isReminderActive: Boolean = false
)