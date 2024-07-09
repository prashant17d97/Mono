package com.debugdesk.mono.presentation.setting.appearance

import com.debugdesk.mono.ui.appconfig.defaultconfig.AppConfigProperties

data class AppearanceState(
    val isFontExpended: Boolean = false,
    val isLanguageExpended: Boolean = false,
    val isThemeExpended: Boolean = false,
    val appConfigProperties: AppConfigProperties = AppConfigProperties(),
)
