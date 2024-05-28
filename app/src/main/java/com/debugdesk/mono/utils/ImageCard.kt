package com.debugdesk.mono.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.debugdesk.mono.R
import com.debugdesk.mono.presentation.uicomponents.PreviewTheme
import com.debugdesk.mono.utils.CameraFunction.rememberAbsolutePathPainter
import com.debugdesk.mono.utils.CommonColor.inActiveButton
import com.debugdesk.mono.utils.Dp.dp120
import com.debugdesk.mono.utils.Dp.dp16
import com.debugdesk.mono.utils.Dp.dp160
import com.debugdesk.mono.utils.Dp.dp2
import com.debugdesk.mono.utils.Dp.dp24
import com.debugdesk.mono.utils.Dp.dp6

@Composable
fun ImageCard(
    modifier: Modifier = Modifier,
    absolutePath: String,
    onDelete: (absolutePaths: String) -> Unit = {},
    onImageClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .padding(dp6)
            .background(color = Color.White, shape = RoundedCornerShape(dp16))
            .border(
                width = dp2,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(dp16)
            ), contentAlignment = Alignment.TopEnd
    ) {
        Image(
            modifier = modifier
                .width(dp120)
                .height(dp160)
                .border(
                    width = dp2,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(dp16)
                )
                .clip(RoundedCornerShape(dp16))
                .clickable { onImageClick() },
            painter = rememberAbsolutePathPainter(path = absolutePath),
            contentScale = ContentScale.FillBounds,
            contentDescription = null
        )
        IconButton(onClick = { onDelete(absolutePath) }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_trash),
                contentDescription = "Delete",
                tint = inActiveButton,
                modifier = Modifier.size(dp24)
            )
        }
    }
}

@Preview
@Composable
private fun ImageCardPrev() {
    PreviewTheme {
        ImageCard(absolutePath = "/storage/emulated/0/Android/data/com.debugdesk.mono/files/Pictures/MONO_20240528_103742_2037037548647594284.jpg")
    }
}