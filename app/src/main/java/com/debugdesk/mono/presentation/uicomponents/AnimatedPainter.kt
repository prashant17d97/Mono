package com.debugdesk.mono.presentation.uicomponents

import androidx.annotation.DrawableRes
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.painter.Painter
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun animatedPainterResource(
    @DrawableRes id: Int,
    animate: Boolean = true,
): Painter {
    val image = AnimatedImageVector.animatedVectorResource(id = id)
    var atEnd by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = Unit) {
        if (animate) {
            delay(50)
            atEnd = !atEnd
        }
    }
    return rememberAnimatedVectorPainter(image, atEnd)
}
