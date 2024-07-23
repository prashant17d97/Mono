package com.debugdesk.mono.presentation.intro

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.debugdesk.mono.navigation.Screens
import com.debugdesk.mono.presentation.uicomponents.IntroCard
import com.debugdesk.mono.presentation.uicomponents.MonoColumn
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Intro(
    navController: NavHostController,
    viewModel: IntroViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val activity = (context as? Activity)

    BackHandler {
        activity?.finishAffinity()
    }

    val pagerState =
        rememberPagerState {
            viewModel.getIntroModel(context).size
        }

    var currentIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    LaunchedEffect(key1 = currentIndex) {
        pagerState.animateScrollToPage(currentIndex)
    }

    MonoColumn {
        val introModel = viewModel.getIntroModel(context = context)

        HorizontalPager(state = pagerState) { index ->
            IntroCard(
                modifier = Modifier.fillMaxSize(),
                skip = {
                    viewModel.saveSomeCategory(context)
                    viewModel.setIntroFinished()
                    navController.navigate(Screens.Report.route) {
                        popUpTo(Screens.Intro.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onContinue = {
                    if (index >= introModel.size - 1) {
                        viewModel.saveSomeCategory(context)
                        viewModel.setIntroFinished()
                        navController.navigate(Screens.Report.route) {
                            popUpTo(Screens.Intro.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    } else {
                        if (currentIndex < introModel.size - 1) {
                            ++currentIndex
                        }
                    }
                },
                current = index + 1,
                total = introModel.size,
                heading = introModel[index].heading,
                description = introModel[index].description,
                painterResource = introModel[index].img,
            )
        }
    }
}

@Preview
@Composable
fun IntroPreview() = Intro(navController = rememberNavController())
