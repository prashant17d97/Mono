package com.debugdesk.mono.presentation.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.debugdesk.mono.R
import com.debugdesk.mono.domain.repo.Repository
import com.debugdesk.mono.ui.appconfig.AppConfigManager
import com.debugdesk.mono.ui.appconfig.AppStateManager
import com.debugdesk.mono.ui.appconfig.defaultconfig.SettingModel
import com.debugdesk.mono.ui.appconfig.defaultconfig.SettingNameEnum
import com.debugdesk.mono.utils.CommonColor.inActiveButton
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.getCurrencyIcon
import com.debugdesk.mono.utils.states.AlertState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingVM(
    private val appConfigManager: AppConfigManager,
    private val repository: Repository,
    private val appStateManager: AppStateManager,
) : ViewModel() {
    companion object {
        private const val TAG = "SettingVM"
    }

    val allDataCount = repository.allItemSize

    val settings = { currency: String ->
        listOf(
            SettingModel(icon = R.drawable.ic_category, name = SettingNameEnum.Category),
            SettingModel(icon = R.drawable.ic_wrench, name = SettingNameEnum.Appearance),
            SettingModel(icon = currency.getCurrencyIcon(), name = SettingNameEnum.Currency),
            SettingModel(icon = R.drawable.ic_reminder, name = SettingNameEnum.Reminder),
        )
    }

    val appConfigProperties = appConfigManager.appConfigProperties

    fun deleteAllData() {
        appStateManager.updateAlertState(
            alertState = AlertState(
                show = true,
                iconDrawable = R.drawable.ic_trash,
                iconColor = inActiveButton,
                onPositiveClick = {
                    viewModelScope.launch(Dispatchers.IO) {
                        if (repository.allDailyTransaction.value.isNotEmpty()) {
                            repository.clearDatabase()
                        }
                    }.invokeOnCompletion {
                        appStateManager.hideAlertDialog()
                        if (allDataCount.value == 0) {
                            appStateManager.showToastState(toastMsg = R.string.delete_success)
                        } else {
                            appStateManager.showToastState(toastMsgString = it?.localizedMessage)
                        }
                    }
                },
                onNegativeClick = {
                    appStateManager.hideAlertDialog()
                }
            )
        )
    }

}
