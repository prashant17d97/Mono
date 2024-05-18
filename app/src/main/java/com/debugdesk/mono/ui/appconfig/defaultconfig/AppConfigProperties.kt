package com.debugdesk.mono.ui.appconfig.defaultconfig

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontStyle.Companion.Normal
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.toCurrencyIcon

data class AppConfigProperties(
    val isDarkTheme: ThemeMode = ThemeMode.Dark,
    val fontFamily: String = "Poppins",
    val fontStyle: FontStyle = Normal,
    val textDarkColor: Color = Color.White,
    val textLightColor: Color = Color.Black,
    val dynamicColor: Boolean = false,
    val language: String = "English",
    val dynamicPrimaryColor: Color? = null,
    val selectedCurrencyCode: String = "INR",
) {
    val currencyIcon: String
        get() = selectedCurrencyCode.toCurrencyIcon()
}
