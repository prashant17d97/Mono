package com.debugdesk.mono.presentation.setting.appearance

import com.debugdesk.mono.ui.appconfig.defaultconfig.ThemeMode

sealed class AppearanceIntent {
    data object Back : AppearanceIntent()
    data class ChangeFont(val font: String, val isExpended: Boolean) : AppearanceIntent()
    data class ChangeLanguage(val language: String, val isExpended: Boolean) : AppearanceIntent()
    data class ChangeTheme(val theme: ThemeMode, val isExpended: Boolean) : AppearanceIntent()
    data object UpdateDynamicColor : AppearanceIntent()
    data object Save : AppearanceIntent()
}