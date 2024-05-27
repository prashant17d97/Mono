package com.debugdesk.mono.presentation.splash


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.debugdesk.mono.R
import com.debugdesk.mono.main.MainViewModel
import com.debugdesk.mono.navigation.Screens
import com.debugdesk.mono.presentation.uicomponents.ScreenView
import com.debugdesk.mono.utils.Dp.dp10
import com.debugdesk.mono.utils.Dp.dp240
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.TimerDelay
import org.koin.androidx.compose.koinViewModel

@Composable
fun WelcomeScreen(
    navController: NavHostController,
    viewModel: MainViewModel = koinViewModel()
) {
    val isIntroCompleted by viewModel.isIntroCompleted.collectAsState()
    ScreenView(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(R.color.logoBackground))
            .padding(dp10),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            modifier = Modifier.size(dp240),
            painter = painterResource(id = R.drawable.mono_splash),
            contentDescription = "Logo"
        )
    }


    TimerDelay {
        if (isIntroCompleted) {
            navController.navigate(Screens.Report.route) {
                popUpTo(Screens.WelcomeScreen.route) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        } else {
            navController.navigate(Screens.Intro.route) {
                popUpTo(Screens.WelcomeScreen.route) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }

}


@Preview
@Composable
fun WelcomeScreenPreview() = WelcomeScreen(rememberNavController())