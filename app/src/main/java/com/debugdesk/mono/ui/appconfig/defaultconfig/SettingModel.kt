package com.debugdesk.mono.ui.appconfig.defaultconfig

import androidx.annotation.StringRes
import com.debugdesk.mono.R

class SettingModel(
    val icon: Int, val name: SettingNameEnum
)

enum class SettingNameEnum(@StringRes val stringRes: Int) {
    Category(stringRes = R.string.category),
    Appearance(stringRes = R.string.appearance),
    Currency(stringRes = R.string.currency),
    Reminder(stringRes = R.string.reminder)
}

enum class ThemeMode(@StringRes val stringRes: Int) {
    Default(R.string.default_mode),
    Light(R.string.light),
    Dark(R.string.dark)
}