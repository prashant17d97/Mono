package com.debugdesk.mono.ui.appconfig

import com.debugdesk.mono.model.ReminderTimeData
import com.debugdesk.mono.ui.appconfig.defaultconfig.AppConfigProperties
import kotlinx.coroutines.flow.StateFlow

interface AppConfigManager {
    val isIntroCompleted: StateFlow<Boolean>

    val appConfigProperties: StateFlow<AppConfigProperties>

    val reminderTimeData: StateFlow<ReminderTimeData>

    fun subscribeNewAppConfig(appConfigProperties: AppConfigProperties)

    fun introCompleted(isIntroCompleted: Boolean)

    fun resetDefault()

    fun restorePreviousAppConfig()

    fun saveNewAppConfig()

    fun saveReminderTimeData(reminderTimeData: ReminderTimeData)

    fun fetchAppInitialData()
}
