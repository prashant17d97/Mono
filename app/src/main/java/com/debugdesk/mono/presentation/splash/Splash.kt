package com.debugdesk.mono.presentation.splash


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.debugdesk.mono.R
import com.debugdesk.mono.navigation.Screens
import com.debugdesk.mono.presentation.uicomponents.ScreenView
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.TimerDelay
import org.koin.androidx.compose.koinViewModel

@Composable
fun WelcomeScreen(
    navController: NavHostController,
    viewModel: SplashViewModel = koinViewModel()
) {
    val width = LocalConfiguration.current.screenWidthDp
    val isIntroCompleted by viewModel.isIntroCompleted.collectAsState()

    ScreenView(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            modifier = Modifier.size((width * 0.3).dp),
            painter = painterResource(id = R.drawable.mono),
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