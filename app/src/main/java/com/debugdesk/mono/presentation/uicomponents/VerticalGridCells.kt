package com.debugdesk.mono.presentation.uicomponents

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.debugdesk.mono.utils.Dp.dp80

@Composable
fun <Generic> VerticalGridCells(
    modifier: Modifier = Modifier,
    list: List<Generic>,
    spanCounts: Int? = null,
    top: Dp = 5.dp,
    bottom: Dp = 5.dp,
    start: Dp = 5.dp,
    end: Dp = 5.dp,
    itemScope: @Composable (Generic, Int, Int) -> Unit = { _, _, _ -> },
) {
    val listSize = list.size
    var index = 0
    val configuration = LocalConfiguration.current
    val spanCount = spanCounts ?: (configuration.screenWidthDp.dp / dp80).toInt()
    val span =
        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) spanCount else spanCount * 2
    val totalRow =
        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) (listSize + spanCount) / span else ((listSize + spanCount) / span) + 1

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(start = start, top = top, end = end, bottom = bottom),
    ) {
        for (row in 1..totalRow) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                userScrollEnabled = false,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                content = {
                    items(count = span, itemContent = {
                        if (index < listSize) {
                            itemScope(list[index], index, spanCount)
                        }
                        index++
                    })
                },
            )
        }
    }
}
