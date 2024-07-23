package com.debugdesk.mono.presentation.uicomponents

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Switch2(
    modifier: Modifier = Modifier,
    value: Boolean,
    onValueChange: (Boolean) -> Unit,
    scale: Float = 2f,
    width: Dp = 40.dp,
    height: Dp = 20.dp,
    strokeWidth: Dp = 2.dp,
    checkedTrackColor: Color = MaterialTheme.colorScheme.primary,
    uncheckedTrackColor: Color = MaterialTheme.colorScheme.primary,
    gapBetweenThumbAndTrackEdge: Dp = 2.dp,
) {
    val thumbRadius = (height / 2) - gapBetweenThumbAndTrackEdge

    // To move thumb, we need to calculate the position (along x axis)
    val animatePosition =
        animateFloatAsState(
            targetValue =
            if (value) {
                with(LocalDensity.current) { (width - thumbRadius - gapBetweenThumbAndTrackEdge).toPx() }
            } else {
                with(LocalDensity.current) { (thumbRadius + gapBetweenThumbAndTrackEdge).toPx() }
            },
            label = "animatePosition",
        )

    Box {
        Canvas(
            modifier =
            modifier
                .size(width = width, height = height)
                .scale(scale = scale)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        // This is called when the user taps on the canvas
                        onValueChange(!value)
                    })
                },
        ) {
            // Track
            drawRoundRect(
                color = if (value) checkedTrackColor else uncheckedTrackColor,
                cornerRadius = CornerRadius(x = 10.dp.toPx(), y = 10.dp.toPx()),
                style = Stroke(width = strokeWidth.toPx()),
            )

            // Thumb
            drawCircle(
                color = if (value) checkedTrackColor else uncheckedTrackColor,
                radius = thumbRadius.toPx(),
                center =
                Offset(
                    x = animatePosition.value,
                    y = size.height / 2,
                ),
            )
        }
    }
}
