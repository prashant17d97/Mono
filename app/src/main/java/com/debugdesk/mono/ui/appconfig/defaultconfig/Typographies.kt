package com.debugdesk.mono.ui.appconfig.defaultconfig

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle.Companion.Italic
import androidx.compose.ui.text.font.FontStyle.Companion.Normal
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.debugdesk.mono.R

class Typographies(
    private val properties: AppConfigProperties,
    private val themeMode: Boolean,
) {
    private val color =
        Color.Unspecified.takeIf { properties.dynamicColor }
            ?: properties.textDarkColor.takeIf { themeMode } ?: properties.textLightColor

    private val googleFontFamily =
        FontFamily(
            Font(
                googleFont =
                GoogleFont(
                    name = properties.fontFamily,
                ),
                fontProvider =
                GoogleFont.Provider(
                    providerAuthority = "com.google.android.gms.fonts",
                    providerPackage = "com.google.android.gms",
                    certificates = R.array.com_google_android_gms_fonts_certs,
                ),
            ),
        )

    // Set of Material typography styles to start with

    private val typographyNormal =
        Typography(
            displayLarge =
            TextStyle(
                fontSize = 57.sp,
                fontFamily = googleFontFamily,
                fontWeight = FontWeight.Bold,
                fontStyle = Normal,
                color = color,
            ),
            displayMedium =
            TextStyle(
                fontSize = 45.sp,
                fontFamily = googleFontFamily,
                fontWeight = FontWeight.Medium,
                fontStyle = Normal,
                color = color,
            ),
            displaySmall =
            TextStyle(
                fontSize = 36.sp,
                fontFamily = googleFontFamily,
                fontWeight = FontWeight.Normal,
                fontStyle = Normal,
                color = color,
            ),
            headlineLarge =
            TextStyle(
                fontSize = 32.sp,
                fontFamily = googleFontFamily,
                fontWeight = FontWeight.Bold,
                fontStyle = Normal,
                color = color,
            ),
            headlineMedium =
            TextStyle(
                fontSize = 28.sp,
                fontFamily = googleFontFamily,
                fontWeight = FontWeight.Medium,
                fontStyle = Normal,
                color = color,
            ),
            headlineSmall =
            TextStyle(
                fontSize = 24.sp,
                fontFamily = googleFontFamily,
                fontWeight = FontWeight.Normal,
                fontStyle = Normal,
                color = color,
            ),
            titleLarge =
            TextStyle(
                fontSize = 22.sp,
                fontFamily = googleFontFamily,
                fontWeight = FontWeight.Bold,
                fontStyle = Normal,
                color = color,
            ),
            titleMedium =
            TextStyle(
                fontSize = 16.sp,
                fontFamily = googleFontFamily,
                fontWeight = FontWeight.Medium,
                fontStyle = Normal,
                color = color,
            ),
            titleSmall =
            TextStyle(
                fontSize = 14.sp,
                fontFamily = googleFontFamily,
                fontWeight = FontWeight.Normal,
                fontStyle = Normal,
                color = color,
            ),
            bodyLarge =
            TextStyle(
                fontSize = 16.sp,
                fontFamily = googleFontFamily,
                fontWeight = FontWeight.Bold,
                fontStyle = Normal,
                color = color,
            ),
            bodyMedium =
            TextStyle(
                fontSize = 14.sp,
                fontFamily = googleFontFamily,
                fontWeight = FontWeight.Medium,
                fontStyle = Normal,
                color = color,
            ),
            bodySmall =
            TextStyle(
                fontSize = 12.sp,
                fontFamily = googleFontFamily,
                fontWeight = FontWeight.Normal,
                fontStyle = Normal,
                color = color,
            ),
            labelLarge =
            TextStyle(
                fontSize = 14.sp,
                fontFamily = googleFontFamily,
                fontWeight = FontWeight.Bold,
                fontStyle = Normal,
                color = color,
            ),
            labelMedium =
            TextStyle(
                fontSize = 12.sp,
                fontFamily = googleFontFamily,
                fontWeight = FontWeight.Medium,
                fontStyle = Normal,
                color = color,
            ),
            labelSmall =
            TextStyle(
                fontSize = 11.sp,
                fontFamily = googleFontFamily,
                fontWeight = FontWeight.Normal,
                fontStyle = Normal,
                color = color,
            ),
        )
    private val typographyItalic =
        Typography(
            displayLarge =
            TextStyle(
                fontSize = 57.sp,
                fontFamily = googleFontFamily,
                fontWeight = FontWeight.Bold,
                fontStyle = Italic,
                color = color,
            ),
            displayMedium =
            TextStyle(
                fontSize = 45.sp,
                fontFamily = googleFontFamily,
                fontWeight = FontWeight.Medium,
                fontStyle = Italic,
                color = color,
            ),
            displaySmall =
            TextStyle(
                fontSize = 36.sp,
                fontFamily = googleFontFamily,
                fontWeight = FontWeight.Normal,
                fontStyle = Italic,
                color = color,
            ),
            headlineLarge =
            TextStyle(
                fontSize = 32.sp,
                fontFamily = googleFontFamily,
                fontWeight = FontWeight.Bold,
                fontStyle = Italic,
                color = color,
            ),
            headlineMedium =
            TextStyle(
                fontSize = 28.sp,
                fontFamily = googleFontFamily,
                fontWeight = FontWeight.Medium,
                fontStyle = Italic,
                color = color,
            ),
            headlineSmall =
            TextStyle(
                fontSize = 24.sp,
                fontFamily = googleFontFamily,
                fontWeight = FontWeight.Normal,
                fontStyle = Italic,
                color = color,
            ),
            titleLarge =
            TextStyle(
                fontSize = 22.sp,
                fontFamily = googleFontFamily,
                fontWeight = FontWeight.Bold,
                fontStyle = Italic,
                color = color,
            ),
            titleMedium =
            TextStyle(
                fontSize = 16.sp,
                fontFamily = googleFontFamily,
                fontWeight = FontWeight.Medium,
                fontStyle = Italic,
                color = color,
            ),
            titleSmall =
            TextStyle(
                fontSize = 14.sp,
                fontFamily = googleFontFamily,
                fontWeight = FontWeight.Normal,
                fontStyle = Italic,
                color = color,
            ),
            bodyLarge =
            TextStyle(
                fontSize = 16.sp,
                fontFamily = googleFontFamily,
                fontWeight = FontWeight.Bold,
                fontStyle = Italic,
                color = color,
            ),
            bodyMedium =
            TextStyle(
                fontSize = 14.sp,
                fontFamily = googleFontFamily,
                fontWeight = FontWeight.Medium,
                fontStyle = Italic,
                color = color,
            ),
            bodySmall =
            TextStyle(
                fontSize = 12.sp,
                fontFamily = googleFontFamily,
                fontWeight = FontWeight.Normal,
                fontStyle = Italic,
                color = color,
            ),
            labelLarge =
            TextStyle(
                fontSize = 14.sp,
                fontFamily = googleFontFamily,
                fontWeight = FontWeight.Bold,
                fontStyle = Italic,
                color = color,
            ),
            labelMedium =
            TextStyle(
                fontSize = 12.sp,
                fontFamily = googleFontFamily,
                fontWeight = FontWeight.Medium,
                fontStyle = Italic,
                color = color,
            ),
            labelSmall =
            TextStyle(
                fontSize = 11.sp,
                fontFamily = googleFontFamily,
                fontWeight = FontWeight.Normal,
                fontStyle = Italic,
                color = color,
            ),
        )

    fun Typography() = typographyNormal.takeIf { properties.fontStyle == Normal } ?: typographyItalic
}
