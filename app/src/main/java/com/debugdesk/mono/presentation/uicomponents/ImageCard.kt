package com.debugdesk.mono.presentation.uicomponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.debugdesk.mono.R
import com.debugdesk.mono.utils.Dp

@Composable
fun ImageCard(
    painter: Painter,
    onDelete: () -> Unit = {},
    onImageClick: () -> Unit = {},
) {
    Box(
        modifier =
        Modifier
            .size(Dp.dp140)
            .padding(Dp.dp6)
            .background(
                color = Color.Transparent,
                shape = RoundedCornerShape(Dp.dp6),
            ),
        contentAlignment = Alignment.TopEnd,
    ) {
        Image(
            modifier =
            Modifier
                .size(Dp.dp140)
                .clip(RoundedCornerShape(Dp.dp6))
                .clickable { onImageClick() },
            painter = painter,
            contentScale = ContentScale.FillBounds,
            contentDescription = null,
        )
        Image(
            painter = painterResource(id = R.drawable.remove),
            contentDescription = "Delete",
            modifier =
            Modifier
                .size(Dp.dp24)
                .clickable(onClick = onDelete),
        )
    }
}
