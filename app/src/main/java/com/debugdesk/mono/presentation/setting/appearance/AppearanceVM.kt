package com.debugdesk.mono.presentation.setting.appearance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.debugdesk.mono.R
import com.debugdesk.mono.ui.appconfig.AppConfigManager
import com.debugdesk.mono.ui.appconfig.AppStateManager
import com.debugdesk.mono.ui.appconfig.defaultconfig.AppConfigProperties
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AppearanceVM(
    private val appConfigManager: AppConfigManager,
    private val appStateManager: AppStateManager,
) : ViewModel() {

    val appConfigProperties = appConfigManager.appConfigProperties
    private val _appearanceState: MutableStateFlow<AppearanceState> =
        MutableStateFlow(AppearanceState())

    val appearanceState: StateFlow<AppearanceState>
        get() = _appearanceState

    private companion object {
        private const val TAG = "AppearanceVM"
    }

    fun initial() {
        viewModelScope.launch {
            appConfigManager.appConfigProperties.collect {
                _appearanceState.tryEmit(appearanceState.value.copy(appConfigProperties = it))
            }
        }
    }


    fun handleAppearanceIntent(
        appearanceIntent: AppearanceIntent,
        navHostController: NavHostController
    ) {
        when (appearanceIntent) {
            AppearanceIntent.Back -> revertTheAppConfigPropertiesChange(navHostController)
            is AppearanceIntent.ChangeFont -> changeFont(appearanceIntent)
            is AppearanceIntent.ChangeLanguage -> changeLanguage(appearanceIntent)
            is AppearanceIntent.ChangeTheme -> changeTheme(appearanceIntent)
            is AppearanceIntent.UpdateDynamicColor -> updateDynamicColor()
            AppearanceIntent.Save -> saveNewAppConfigPropertiesChanges()
        }

    }

    private fun updateDynamicColor() {
        val state = appearanceState.value.appConfigProperties.dynamicColor
        val appearance =
            appearanceState.value.appConfigProperties.copy(dynamicColor = !state)
        _appearanceState.tryEmit(
            appearanceState.value.copy(
                appConfigProperties = appearance
            )
        )
        requestAppConfigChanges(appearance)
    }

    private fun changeTheme(appearanceIntent: AppearanceIntent.ChangeTheme) {
        val appearance =
            appearanceState.value.appConfigProperties.copy(
                themeMode = appearanceIntent.theme
            )

        _appearanceState.tryEmit(
            appearanceState.value.copy(
                isThemeExpended = appearanceIntent.isExpended,
                appConfigProperties = appearance
            )
        )
        requestAppConfigChanges(appearance)
    }

    private fun changeLanguage(appearanceIntent: AppearanceIntent.ChangeLanguage) {
        val appearance =
            appearanceState.value.appConfigProperties.copy(
                language = appearanceIntent.language
            )

        _appearanceState.tryEmit(
            appearanceState.value.copy(
                isLanguageExpended = appearanceIntent.isExpended,
                appConfigProperties = appearance
            )
        )
        requestAppConfigChanges(appearance)
    }

    private fun changeFont(appearanceIntent: AppearanceIntent.ChangeFont) {
        val appearance =
            appearanceState.value.appConfigProperties.copy(
                fontFamily = appearanceIntent.font
            )

        _appearanceState.tryEmit(
            appearanceState.value.copy(
                isFontExpended = appearanceIntent.isExpended,
                appConfigProperties = appearance
            )
        )
        requestAppConfigChanges(appearance)
    }

    private fun requestAppConfigChanges(appConfigProperties: AppConfigProperties) {
        appConfigManager.subscribeNewAppConfig(appConfigProperties = appConfigProperties)
    }

    private fun revertTheAppConfigPropertiesChange(navHostController: NavHostController) {
        navHostController.navigateUp()
        appConfigManager.restorePreviousAppConfig()
    }

    private fun saveNewAppConfigPropertiesChanges() {
        _appearanceState.tryEmit(
            appearanceState.value.copy(
                isFontExpended = false,
                isThemeExpended = false,
                isLanguageExpended = false
            )
        )
        appConfigManager.saveNewAppConfig()
        appStateManager.showToastState(R.string.app_config_saved)
    }
}