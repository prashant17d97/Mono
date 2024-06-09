package com.debugdesk.mono.presentation.setting.reminder

import androidx.lifecycle.ViewModel
import com.debugdesk.mono.ui.appconfig.AppStateManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ReminderVM(
    private val appStateManager: AppStateManager
) : ViewModel() {
    val time =
        (SimpleDateFormat("HH:mm", Locale.ENGLISH).format(Calendar.getInstance().time)).split(":")

    fun toast(msg: Int?, message: String?) {
        appStateManager.showToastState(msg, message)
    }


}
