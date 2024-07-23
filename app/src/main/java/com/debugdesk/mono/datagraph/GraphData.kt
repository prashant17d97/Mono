package com.debugdesk.mono.datagraph

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.TextStyle

data class GraphData(
    val label: String,
    val value: Double,
)

data class GraphDataOffsets(
    val label: String,
    val offset: Offset,
)

data class GraphDataSet(
    val graphDataSets: List<GraphData>,
    val primaryColor: Color = Color.Red,
    val textStyle: @Composable () -> TextStyle = { MaterialTheme.typography.titleMedium },
    val dottedVerticalLineColor: Color = Color.Gray,
    val curveLineColor: Color = primaryColor,
    val curveStrokeCapStyle: StrokeCap = StrokeCap.Square,
    val gap: Int = 250,
    val height: Int = 100,
) {
    private val normalizedGraphData: List<GraphData> by lazy {
        graphDataSets.map { it.copy(value = (height / it.value)) }
    }

    private val chunkedGraphData: List<List<GraphData>> by lazy {
        normalizedGraphData.chunked(5)
    }

    private val dataList: List<List<GraphData>> by lazy {
        val temp = mutableListOf<List<GraphData>>()
        chunkedGraphData.forEachIndexed { index, graphData ->
            when (index) {
                0 -> temp.add(listOf(GraphData(label = "", value = 1.0)) + graphData)
                chunkedGraphData.lastIndex ->
                    if (graphData.size < 5) {
                        val lastItem = temp.last().last()
                        temp.add(listOf(lastItem) + graphData)
                    } else {
                        temp.add(
                            graphData +
                                listOf(
                                    GraphData(
                                        label = "",
                                        value = 1.0,
                                    ),
                                ),
                        )
                    }

                else -> {
                    val lastItem = temp.last().last()
                    temp.add(listOf(lastItem) + graphData)
                }
            }
        }
        temp
    }

    val dataOffsets: List<List<GraphDataOffsets>> by lazy {
        dataList.map { chunk ->
            chunk.mapIndexed { index, graphData ->
                val xValue =
                    when (index) {
                        0 -> 0f
                        else -> index * 0.2f
                    }
                GraphDataOffsets(
                    label = graphData.label,
                    offset = Offset(x = xValue, y = graphData.value.toFloat()),
                )
            }
        }
    }
}

private fun List<Offset>.assignGapHeight(
    gap: Float = 200f,
    height: Float,
) = map {
    val totalHeight = height * it.y
    val y = if (totalHeight > height) height else totalHeight
    Offset(x = gap * it.x, y = y)
}
