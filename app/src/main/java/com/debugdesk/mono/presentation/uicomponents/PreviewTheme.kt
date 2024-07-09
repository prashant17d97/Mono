package com.debugdesk.mono.presentation.uicomponents

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.debugdesk.mono.ui.appconfig.defaultconfig.AppConfigProperties
import com.debugdesk.mono.ui.appconfig.defaultconfig.ThemeMode
import com.debugdesk.mono.ui.theme.MonoTheme

@Composable
fun PreviewTheme(
    isDarkTheme: Boolean = true,
    appConfigProperties: AppConfigProperties = AppConfigProperties(
        themeMode = isDarkTheme(
            isDarkTheme
        )
    ),
    content: @Composable () -> Unit
) {
    MonoTheme(appConfigProperties = appConfigProperties) {
        Surface(
            content = content,
            color = MaterialTheme.colorScheme.background
        )

    }
}

private val isDarkTheme = { bool: Boolean ->
    when (bool) {
        true -> ThemeMode.Dark
        false -> ThemeMode.Light
    }
}