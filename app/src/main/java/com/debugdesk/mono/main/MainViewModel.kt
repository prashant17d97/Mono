package com.debugdesk.mono.main

import androidx.lifecycle.ViewModel
import com.debugdesk.mono.ui.appconfig.AppConfigManager
import com.debugdesk.mono.ui.appconfig.AppStateManager

class MainViewModel(
    private val appStateManager: AppStateManager,
    private val appConfigManager: AppConfigManager
) : ViewModel() {

    val toastMsg = appStateManager.toastState
    val toastStringMsg = appStateManager.toastStateString
    val alertState = appStateManager.alertState
    val appConfigProperties = appConfigManager.appConfigProperties

    init {
        appConfigManager.fetchAppInitialData()
    }

    fun showToast() {
        appStateManager.showToastState()
    }

    val isIntroCompleted = appConfigManager.isIntroCompleted

}