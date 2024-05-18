package com.debugdesk.mono.presentation.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import com.debugdesk.mono.domain.data.local.datastore.DataStoreObjects
import com.debugdesk.mono.domain.data.local.datastore.DataStoreUtil
import com.debugdesk.mono.ui.appconfig.AppConfigManager

class SplashViewModel(
    private val appConfigManager: AppConfigManager,
    private val dataStoreUtil: DataStoreUtil
) : ViewModel() {
    val isIntroCompleted = appConfigManager.isIntroCompleted

    init {
        dataStoreUtil.retrieveKey(DataStoreObjects.INTRO_FINISHED) {
            Log.e("IntroVM", "introFinished: $it,  ${isIntroCompleted.value}")
            appConfigManager.introCompleted(true)
        }
    }

}