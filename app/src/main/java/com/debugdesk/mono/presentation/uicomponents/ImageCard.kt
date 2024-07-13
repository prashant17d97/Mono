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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.rememberAsyncImagePainter
import com.debugdesk.mono.R
import com.debugdesk.mono.utils.CameraFunction.toBitmap
import com.debugdesk.mono.utils.Dp

@Composable
fun ImageCard(
    imagePath: String,
    onDelete: () -> Unit = {},
    onImageClick: () -> Unit = {}
) {
    if (imagePath.isNotEmpty()) {
        Box(
            modifier = Modifier
                .size(Dp.dp140)
                .padding(Dp.dp6)
                .background(
                    color = Color.Transparent,
                    shape = RoundedCornerShape(Dp.dp6)
                ), contentAlignment = Alignment.TopEnd
        ) {
            imagePath.let {
                Image(
                    modifier = Modifier
                        .size(Dp.dp140)
                        .clip(RoundedCornerShape(Dp.dp6))
                        .clickable { onImageClick() },
                    painter = rememberAsyncImagePainter(model = it.toBitmap()),
                    contentScale = ContentScale.FillBounds,
                    contentDescription = null
                )
                Image(
                    painter = painterResource(id = R.drawable.remove),
                    contentDescription = "Delete",
                    modifier = Modifier
                        .size(Dp.dp24)
                        .clickable(onClick = onDelete)
                )
            }
        }
    }
}


@Preview
@Composable
private fun ImageCardPrev() {
    PreviewTheme {
        ImageCard(
            imagePath = "",
        )
    }
}