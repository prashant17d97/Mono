package com.debugdesk.mono.presentation.uicomponents

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith

fun slideAnimation(duration: Int = 800): ContentTransform {
    return (
        slideInHorizontally(animationSpec = tween(durationMillis = duration)) { height -> height } +
            fadeIn(
                animationSpec = tween(durationMillis = duration),
            )
        ).togetherWith(
        slideOutHorizontally(animationSpec = tween(durationMillis = duration)) { height -> -height } +
            fadeOut(
                animationSpec = tween(durationMillis = duration),
            ),
    )
}
