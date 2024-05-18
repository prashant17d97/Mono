package com.debugdesk.mono.presentation.uicomponents.canvas

import android.text.TextPaint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.debugdesk.mono.presentation.uicomponents.PreviewTheme

class StringPainter(private val text: String, private val textStyle: TextStyle) : Painter() {
    override val intrinsicSize = androidx.compose.ui.geometry.Size.Unspecified

    override fun DrawScope.onDraw() {
        val paint = TextPaint().apply {
            color = textStyle.color.toArgb()
            textSize = textStyle.fontSize.toPx()
        }
        drawContext.canvas.nativeCanvas.drawText(
            text,
            0f,
            paint.textSize,
            paint
        )
    }
}

@Composable
fun rememberStringPainter(
    text: String,
    textStyle: TextStyle = TextStyle.Default.copy(
        fontSize = 16.sp,
        color = Color.Black
    )
): Painter {
    return remember(text, textStyle) {
        StringPainter(text, textStyle)
    }
}

@Composable
fun DrawStringCanvas(text: String) {
    val painter = rememberStringPainter(text)
    Canvas(modifier = Modifier.fillMaxSize()) {
        with(painter) {
            draw(size)
        }
    }
}

@Preview
@Composable
fun MyApp() {
    PreviewTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            DrawStringCanvas("Hello World!")
        }
    }
}
