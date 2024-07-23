package com.debugdesk.mono.ui.appconfig.defaultconfig

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontStyle.Companion.Normal
import com.debugdesk.mono.R

data class AppConfigProperties(
    val themeMode: ThemeMode = ThemeMode.Default,
    val fontFamily: String = "Poppins",
    val fontStyle: FontStyle = Normal,
    val textDarkColor: Color = Color.White,
    val textLightColor: Color = Color.Black,
    val dynamicColor: Boolean = false,
    val language: String = "English",
    val dynamicPrimaryColor: Color? = null,
    val selectedCurrencyCode: Int = R.string.inr,
    val selectedCurrencyIconString: Int = R.string.inrIcon,
    val selectedCurrencyIconDrawable: Int = R.drawable.ic_rupee,
) {
    val isNightTheme: Boolean
        @Composable
        get() =
            when (themeMode) {
                ThemeMode.Dark -> true
                ThemeMode.Light -> false
                else -> isSystemInDarkTheme()
            }
}

val DefaultConfigProperties =
    AppConfigProperties(
        themeMode = ThemeMode.Default,
        fontFamily = "Poppins",
        fontStyle = Normal,
        textDarkColor = Color.White,
        textLightColor = Color.Black,
        dynamicColor = false,
        language = "English",
        dynamicPrimaryColor = null,
        selectedCurrencyCode = R.string.inr,
        selectedCurrencyIconString = R.string.inrIcon,
        selectedCurrencyIconDrawable = R.drawable.ic_rupee,
    )
