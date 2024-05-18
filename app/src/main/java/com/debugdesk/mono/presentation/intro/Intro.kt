package com.debugdesk.mono.presentation.intro


import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
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
import com.debugdesk.mono.presentation.uicomponents.ScreenView
import com.debugdesk.mono.presentation.uicomponents.slideAnimation
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Intro(
    navController: NavHostController,
    viewModel: IntroViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val activity = (context as? Activity)
    BackHandler {
        activity?.finishAffinity()
    }/*var direction by remember { mutableStateOf(-1) }
    val coroutineScope = rememberCoroutineScope()*/
    val listState = rememberLazyListState()
    var currentIndex by rememberSaveable {
        mutableIntStateOf(0)
    }
    ScreenView {
        val introModel = viewModel.getIntroModel(context = context)
        LazyRow(
            state = listState,
            userScrollEnabled = false,
            /*modifier = Modifier.pointerInput(Unit) {

                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()

                        val (x, y) = dragAmount
                        if (kotlin.math.abs(x) > kotlin.math.abs(y)) {
                            when {
                                x > 0 -> {
                                    //right
                                    direction = 0
                                }
                                x < 0 -> {
                                    // left
                                    direction = 1
                                }
                            }
                        } else {
                                when {
                                    y > 0 -> {
                                        // down
                                        direction = 2
                                    }
                                    y < 0 -> {
                                        // up
                                        direction = 3
                                    }
                                }
                            }

                    },
                    onDragEnd = {
                        when (direction) {
                            0 -> {
                                //right swipe code here
                                coroutineScope.launch {
                                    listState.animateScrollToItem(
                                        index = if (currentIndex > 0) {
                                            --currentIndex
                                        } else {
                                            0
                                        }
                                    ,)
                                }
                                Log.e("TAG", "Intro: Right")
                            }
                            1 -> {
                                // left swipe code here

                                if (currentIndex <introModel.size-1) {
                                    ++currentIndex
                                }

                                Log.e("TAG", "Intro: Left")
                            }
                            2 -> {
                                // down swipe code here
                            }
                            3 -> {
                                // up swipe code here
                            }
                        }
                    },
                )
            }*/
        ) {
            items(introModel.size) {
                AnimatedContent(
                    targetState = currentIndex, transitionSpec = {
                        slideAnimation(duration = 300).using(
                            SizeTransform(clip = false)
                        )
                    }, label = ""
                ) { index ->
                    IntroCard(
                        modifier = Modifier.fillMaxSize(),
                        skip = {
                            viewModel.introFinished(context, true)
                            navController.navigate(Screens.Report.route) {
                                popUpTo(Screens.Intro.route) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        },
                        onContinue = {
                            if (index >= introModel.size - 1) {
                                viewModel.introFinished(context, true)
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
                        painterResource = introModel[index].img
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun IntroPreview() = Intro(navController = rememberNavController())