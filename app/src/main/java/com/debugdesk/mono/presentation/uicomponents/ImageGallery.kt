package com.debugdesk.mono.presentation.uicomponents

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.debugdesk.mono.R
import com.debugdesk.mono.domain.data.local.localdatabase.model.TransactionImage
import com.debugdesk.mono.utils.CameraFunction
import com.debugdesk.mono.utils.CommonColor.inActiveButton
import com.debugdesk.mono.utils.Dp.dp16
import com.debugdesk.mono.utils.Dp.dp40

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageGallery(
    modifier: Modifier = Modifier,
    clickedIndex: Int = 0,
    images: List<TransactionImage> = emptyList(),
    close: () -> Unit,
    onDelete: (image: TransactionImage) -> Unit
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
            .background(
                MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.large
            )
            .padding(dp16)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                modifier = Modifier
                    .size(dp40)
                    .clickable { close() },
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                contentDescription = "Close"
            )

            Text(
                text = stringResource(R.string.preview),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
        HorizontalPager(
            state = pagerState,
            modifier = modifier
                .clip(MaterialTheme.shapes.large)
                .weight(1f)
        ) {
            val image = images[it]
            if (!image.isEmpty) {
                ImageGalleryCard(
                    imageDetails = image,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

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
    }
}

@Composable
private fun ImageGalleryCard(
    modifier: Modifier = Modifier,
    imageDetails: TransactionImage
) {
    Column(modifier = Modifier) {

        Image(
            modifier = modifier
                .clip(MaterialTheme.shapes.large),
            painter = CameraFunction.rememberAbsolutePathPainter(path = imageDetails.absolutePath,),
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