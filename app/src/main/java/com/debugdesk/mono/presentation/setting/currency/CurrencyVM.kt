package com.debugdesk.mono.presentation.setting.currency

import androidx.lifecycle.ViewModel
import com.debugdesk.mono.ui.appconfig.AppConfigManager
import com.debugdesk.mono.ui.appconfig.defaultconfig.AppConfigProperties

class CurrencyVM(
    private val appConfigManager: AppConfigManager,
) : ViewModel() {

    val appConfigProperties = appConfigManager.appConfigProperties


    fun changeCurrency(appConfigProperties: AppConfigProperties) {
        appConfigManager.subscribeNewAppConfig(appConfigProperties)
    }

    fun revertTheAppConfigPropertiesChange() {
        appConfigManager.restorePreviousAppConfig()
    }

    fun saveCurrencyChange() {
        appConfigManager.saveNewAppConfig()
    }
}
