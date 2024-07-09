package com.debugdesk.mono.ui.appconfig

import com.debugdesk.mono.model.RemainderTimeData
import com.debugdesk.mono.ui.appconfig.defaultconfig.AppConfigProperties
import kotlinx.coroutines.flow.StateFlow

interface AppConfigManager {

    val isIntroCompleted: StateFlow<Boolean>

    val appConfigProperties: StateFlow<AppConfigProperties>

    val remainderTimeData:StateFlow<RemainderTimeData>

    fun subscribeNewAppConfig(appConfigProperties: AppConfigProperties)

    fun introCompleted(isIntroCompleted: Boolean)

    fun resetDefault()

    fun restorePreviousAppConfig()

    fun saveNewAppConfig()

    fun saveReminderTimeData(remainderTimeData: RemainderTimeData)

    fun fetchAppInitialData()
}