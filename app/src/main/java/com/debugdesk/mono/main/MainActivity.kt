package com.debugdesk.mono.main

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.debugdesk.mono.notification.NotificationObjects.TARGET_SCREEN
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModel()
    private var targetScreen by mutableStateOf<String?>(null)

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

        intent.getStringExtra(TARGET_SCREEN)?.let {
            targetScreen = it
        }
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContent {
            val appConfigProperties by mainViewModel.appConfigProperties.collectAsState()
            val alertState by mainViewModel.alertState.collectAsState()
            Mono(
                appConfigProperties = appConfigProperties,
                alertState = alertState,
                targetScreen = targetScreen
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

    override fun onNewIntent(intent: Intent) {
        intent.getStringExtra(TARGET_SCREEN)?.let {
            targetScreen = it
        }
        super.onNewIntent(intent)
    }
}
