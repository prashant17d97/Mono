package com.debugdesk.mono.presentation.setting.appearance

import androidx.lifecycle.ViewModel
import com.debugdesk.mono.ui.appconfig.AppConfigManager
import com.debugdesk.mono.ui.appconfig.AppStateManager
import com.debugdesk.mono.ui.appconfig.defaultconfig.AppConfigProperties

class AppearanceVM(
    private val appConfigManager: AppConfigManager,
    private val appStateManager: AppStateManager,
) : ViewModel() {

    val appConfigProperties = appConfigManager.appConfigProperties

    private companion object {
        private const val TAG = "AppearanceVM"
    }


    fun requestAppConfigChanges(appConfigProperties: AppConfigProperties) {
        appConfigManager.subscribeNewAppConfig(appConfigProperties = appConfigProperties)

    }

    fun revertTheAppConfigPropertiesChange() {
        appConfigManager.restorePreviousAppConfig()
    }

    fun saveNewAppConfigPropertiesChanges() {
        appConfigManager.saveNewAppConfig()
    }

    fun showToastState(toastMsg: Int? = null, toastMsgString: String? = null) {
        appStateManager.showToastState(toastMsg, toastMsgString)
    }

}