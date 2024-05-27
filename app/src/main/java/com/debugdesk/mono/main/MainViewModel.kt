package com.debugdesk.mono.main

import android.util.Log
import androidx.lifecycle.ViewModel
import com.debugdesk.mono.domain.data.local.datastore.DataStoreObjects
import com.debugdesk.mono.domain.data.local.datastore.DataStoreUtil
import com.debugdesk.mono.ui.appconfig.AppConfigManager
import com.debugdesk.mono.ui.appconfig.AppStateManager

class MainViewModel(
    private val appStateManager: AppStateManager,
    private val appConfigManager: AppConfigManager,
    private val dataStoreUtil: DataStoreUtil
) : ViewModel() {

    val toastMsg = appStateManager.toastState
    val toastStringMsg = appStateManager.toastStateString
    val alertState = appStateManager.alertState
    val appConfigProperties = appConfigManager.appConfigProperties

    init {
        appConfigManager.restorePreviousAppConfig()
    }

    fun showToast() {
        appStateManager.showToastState()
    }

    val isIntroCompleted = appConfigManager.isIntroCompleted

    init {
        appConfigManager.restorePreviousAppConfig()
        dataStoreUtil.retrieveKey(DataStoreObjects.INTRO_FINISHED) {
            Log.e("IntroVM", "introFinished: $it,  ${isIntroCompleted.value}")
            appConfigManager.introCompleted(it ?: false)
        }
    }
}