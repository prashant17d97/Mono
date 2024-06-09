package com.debugdesk.mono.main

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModel()
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toastMsg()
        installSplashScreen()
        /*enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                lightScrim = md_theme_light_scrim.toArgb(),
                darkScrim = md_theme_dark_scrim.toArgb()
            ), navigationBarStyle = SystemBarStyle.auto(
                lightScrim = md_theme_light_scrim.toArgb(),
                darkScrim = md_theme_dark_scrim.toArgb()
            )
        )*/

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContent {
            val appConfigProperties by mainViewModel.appConfigProperties.collectAsState()
            val alertState by mainViewModel.alertState.collectAsState()
            Mono(
                appConfigProperties = appConfigProperties,
                alertState = alertState
            )

        }
    }

    private fun toastMsg() {
        lifecycleScope.launch {
            combine(
                mainViewModel.toastMsg,
                mainViewModel.toastStringMsg
            ) { toastMsg, toastStringMsg ->
                toastMsg?.let { this@MainActivity.getString(it) } ?: toastStringMsg
            }.collect { message ->
                if (message != null) {
                    Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                    mainViewModel.showToast()
                }
            }
        }
    }

}
