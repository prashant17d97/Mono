package com.debugdesk.mono.presentation.uicomponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.debugdesk.mono.R
import com.debugdesk.mono.utils.CameraFunction.getImageHeightInDp
import com.debugdesk.mono.utils.CameraFunction.rememberAbsolutePathPainter
import com.debugdesk.mono.utils.Dp.dp140
import com.debugdesk.mono.utils.Dp.dp24
import com.debugdesk.mono.utils.Dp.dp6

@Composable
fun ImageCard(
    absolutePath: String,
    applyFixedSize: Boolean = true,
    onDelete: (absolutePaths: String) -> Unit = {},
    onImageClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .padding(dp6)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(dp6)
            ), contentAlignment = Alignment.TopEnd
    ) {
        Image(
            modifier = if (applyFixedSize) {
                Modifier.size(dp140)
            } else {
                Modifier
                    .fillMaxWidth()
                    .height(getImageHeightInDp(absolutePath = absolutePath))
            }
                .clip(RoundedCornerShape(dp6))
                .clickable { onImageClick() },
            painter = rememberAbsolutePathPainter(path = absolutePath),
            contentScale = ContentScale.FillBounds,
            contentDescription = null
        )
        Image(
            painter = painterResource(id = R.drawable.remove),
            contentDescription = "Delete",
            modifier = Modifier
                .size(dp24)
                .clickable(onClick = { onDelete(absolutePath) }
                )
        )
    }
}

@Preview
@Composable
private fun ImageCardPrev() {
    PreviewTheme {
        ImageCard(
            absolutePath = "/storage/emulated/0/Android/data/com.debugdesk.mono/files/Pictures/MONO_20240528_103742_2037037548647594284.jpg"
        )
    }
}