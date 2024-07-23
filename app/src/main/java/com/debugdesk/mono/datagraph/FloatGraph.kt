package com.debugdesk.mono.datagraph

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.debugdesk.mono.presentation.uicomponents.PreviewTheme
import com.debugdesk.mono.utils.CommonColor.brandColor

val graphDataSet =
    GraphDataSet(
        graphDataSets =
        listOf(
            GraphData(label = "Jan", value = 1000.0),
            GraphData(label = "Feb", value = 100.0),
            GraphData(label = "March", value = 3000.0),
            GraphData(label = "Apr", value = 400.0),
            GraphData(label = "May", value = 3500.0),
            GraphData(label = "June", value = 500.0),
            GraphData(label = "July", value = 60000.0),
            GraphData(label = "August", value = 75.0),
            GraphData(label = "September", value = 1700.0),
            GraphData(label = "Oct", value = 100.0),
            GraphData(label = "Nov", value = 1000.0),
        ),
        primaryColor = brandColor,
    )

@Composable
fun FloatingGraph(
    modifier: Modifier = Modifier,
    graphDataSet: GraphDataSet,
) {
    Row(
        modifier =
        modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
    ) {
        graphDataSet.dataOffsets.forEach { graphDataOffsets ->
            Log.d("FloatingGraph", "FloatingGraph: $graphDataOffsets")
            CurveGraph(
                labels = graphDataOffsets.map { it.label.toCharArray().take(3).joinToString("") },
                pointsOffset = graphDataOffsets.map { Offset(x = it.offset.x, y = it.offset.y) },
                graphDataSet = graphDataSet,
            )
        }
    }
}

@Composable
private fun CurveGraph(
    modifier: Modifier = Modifier,
    labels: List<String>,
    graphDataSet: GraphDataSet,
    pointsOffset: List<Offset>,
) {
    val textMeasurer = rememberTextMeasurer()
    val style = graphDataSet.textStyle()
    Canvas(
        modifier =
        modifier
            .width(graphDataSet.gap.dp)
            .height((graphDataSet.height + 40).dp),
    ) {
        val height = graphDataSet.height.dp.toPx()

        val points: List<Offset> =
            pointsOffset.assignGapHeight(gap = size.width, height = height)

        // Draw the shaded area
        val shadedPath =
            Path().apply {
                moveTo(points.first().x, points.first().y)
                for (i in 0 until points.size - 1) {
                    val controlPoint1 =
                        Offset(
                            (points[i].x + points[i + 1].x) / 2,
                            points[i].y,
                        )
                    val controlPoint2 =
                        Offset(
                            (points[i].x + points[i + 1].x) / 2,
                            points[i + 1].y,
                        )
                    cubicTo(
                        controlPoint1.x,
                        controlPoint1.y,
                        controlPoint2.x,
                        controlPoint2.y,
                        points[i + 1].x,
                        points[i + 1].y,
                    )
                }
                lineTo(points.last().x, height)
                lineTo(points.first().x, height)
                close()
            }

        drawPath(
            path = shadedPath,
            brush =
            Brush.verticalGradient(
                colors =
                listOf(
                    graphDataSet.primaryColor,
                    graphDataSet.primaryColor.copy(alpha = 0.1f),
                    Color.Transparent,
                ),
            ),
        )

        // Draw the graph line
        val graphPath =
            Path().apply {
                moveTo(points.first().x, points.first().y)
                for (i in 0 until points.size - 1) {
                    val controlPoint1 =
                        Offset(
                            (points[i].x + points[i + 1].x) / 2,
                            points[i].y,
                        )
                    val controlPoint2 =
                        Offset(
                            (points[i].x + points[i + 1].x) / 2,
                            points[i + 1].y,
                        )
                    cubicTo(
                        controlPoint1.x,
                        controlPoint1.y,
                        controlPoint2.x,
                        controlPoint2.y,
                        points[i + 1].x,
                        points[i + 1].y,
                    )
                }
            }

        drawPath(
            path = graphPath,
            color = graphDataSet.curveLineColor,
            style = Stroke(width = 5.dp.toPx(), cap = graphDataSet.curveStrokeCapStyle),
        )
        for (i in points.indices) {
            drawText(
                textMeasurer = textMeasurer,
                text = labels[i],
                style = style,
                softWrap = false,
                topLeft =
                Offset(
                    x = points[i].x - 10.dp.toPx(),
                    y = height + 10.dp.toPx(),
                ),
            )

            // Draw labels and dotted lines
            drawLine(
                color = graphDataSet.dottedVerticalLineColor,
                start = Offset(points[i].x, points[i].y),
                end = Offset(points[i].x, height + 10.dp.toPx()),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f)),
            )
        }
    }
}

private fun List<Offset>.assignGapHeight(
    gap: Float = 200f,
    height: Float,
) = map {
    val totalHeight = height * it.y
    val y =
        if (totalHeight > height) {
            height
        } else {
            totalHeight
        }
    Offset(x = gap * it.x, y = y)
}

@Preview
@Composable
private fun GraphPrev() {
    PreviewTheme(false) {
        FloatingGraph(graphDataSet = graphDataSet)
    }
}
