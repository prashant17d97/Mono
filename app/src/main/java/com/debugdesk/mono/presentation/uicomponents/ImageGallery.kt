package com.debugdesk.mono.presentation.uicomponents

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.debugdesk.mono.R
import com.debugdesk.mono.utils.CameraFunction.rememberAbsolutePathPainter
import com.debugdesk.mono.utils.CommonColor.inActiveButton
import com.debugdesk.mono.utils.Dp.dp10
import com.debugdesk.mono.utils.Dp.dp16
import com.debugdesk.mono.utils.Dp.dp40

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageGallery(
    modifier: Modifier = Modifier,
    clickedIndex: Int = 0,
    images: List<String> = emptyList(),
    close: () -> Unit,
    onDelete: (imagePath:String) -> Unit
) {
    val pagerState = rememberPagerState {
        images.size
    }
    BackHandler {
        close()
    }

    LaunchedEffect(key1 = Unit) {
        pagerState.animateScrollToPage(clickedIndex)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(dp10)
            .background(
                MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.large
            )
            .padding(dp16)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                modifier = Modifier
                    .size(dp40)
                    .clickable { close() },
                imageVector = Icons.Rounded.Close,
                contentDescription = "Close"
            )
            IconButton(onClick = {
                onDelete(images[pagerState.currentPage])
                if (images.size == 1) {
                    close()
                }
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_trash),
                    contentDescription = "Delete",
                    modifier = Modifier
                        .size(dp40),
                    tint = inActiveButton
                )
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = modifier
                .clip(MaterialTheme.shapes.large)
        ) {
            ImageGalleryCard(imagePath = images[it])
        }
    }
}

@Composable
private fun ImageGalleryCard(
    modifier: Modifier = Modifier,
    imagePath: String
) {
    Box(
        contentAlignment = Alignment.TopEnd,
        modifier = Modifier
            .padding(dp10)

    ) {
        Image(
            modifier = modifier
                .clip(MaterialTheme.shapes.large),
            painter = rememberAbsolutePathPainter(path = imagePath),
            contentScale = ContentScale.Inside,
            contentDescription = "Image"
        )

    }
}

@Preview
@Composable
private fun ImagePrev() {
    ImageGallery(
        close = {},
        onDelete = {}
    )
}