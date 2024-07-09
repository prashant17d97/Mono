package com.debugdesk.mono.ui.appconfig

import com.debugdesk.mono.domain.data.local.datastore.DataStoreObjects
import com.debugdesk.mono.domain.data.local.datastore.DataStoreObjects.INTRO_FINISHED
import com.debugdesk.mono.domain.data.local.datastore.DataStoreObjects.REMINDER_TIME
import com.debugdesk.mono.domain.data.local.datastore.DataStoreUtil
import com.debugdesk.mono.model.RemainderTimeData
import com.debugdesk.mono.ui.appconfig.defaultconfig.AppConfigProperties
import com.debugdesk.mono.ui.appconfig.defaultconfig.DefaultConfigProperties
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AppConfigurationManagerImpl(
    private val dataStoreUtil: DataStoreUtil
) : AppConfigManager {

    private val _appConfigProperties: MutableStateFlow<AppConfigProperties> =
        MutableStateFlow(
            DefaultConfigProperties
        )

    private val _isIntroCompleted: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val isIntroCompleted: StateFlow<Boolean> = _isIntroCompleted

    override val appConfigProperties: StateFlow<AppConfigProperties> = _appConfigProperties

    private val _remainderTimeData: MutableStateFlow<RemainderTimeData> =
        MutableStateFlow(RemainderTimeData())
    override val remainderTimeData: StateFlow<RemainderTimeData>
        get() = _remainderTimeData

    override fun subscribeNewAppConfig(appConfigProperties: AppConfigProperties) {
        _appConfigProperties.tryEmit(appConfigProperties)
    }

    override fun introCompleted(isIntroCompleted: Boolean) {
        _isIntroCompleted.tryEmit(isIntroCompleted)
        dataStoreUtil.saveKey(INTRO_FINISHED, isIntroCompleted)
    }

    override fun resetDefault() {
        _appConfigProperties.tryEmit(AppConfigProperties())
    }

    override fun restorePreviousAppConfig() {
        dataStoreUtil.retrieveObject(
            DataStoreObjects.APP_CONFIG_PROPERTIES,
            AppConfigProperties::class.java
        ) {
            _appConfigProperties.tryEmit(it ?: DefaultConfigProperties)
        }
    }

    override fun saveNewAppConfig() {
        dataStoreUtil.saveObject(DataStoreObjects.APP_CONFIG_PROPERTIES, _appConfigProperties.value)
    }

    override fun saveReminderTimeData(remainderTimeData: RemainderTimeData) {
        dataStoreUtil.saveObject(REMINDER_TIME, remainderTimeData)
        fetchAppInitialData()
    }

    override fun fetchAppInitialData() {
        dataStoreUtil.retrieveObject(
            DataStoreObjects.APP_CONFIG_PROPERTIES,
            AppConfigProperties::class.java
        ) {
            _appConfigProperties.tryEmit(it ?: DefaultConfigProperties)
        }

        dataStoreUtil.retrieveKey(INTRO_FINISHED) {
            _isIntroCompleted.tryEmit(it ?: false)
        }
        dataStoreUtil.retrieveObject(REMINDER_TIME, RemainderTimeData::class.java) {
            _remainderTimeData.tryEmit(it?:RemainderTimeData())
        }
    }
}