package com.debugdesk.mono.presentation.imgpreview


import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.debugdesk.mono.R
import com.debugdesk.mono.presentation.uicomponents.PreviewTheme
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.toDate

@Composable
fun ImagePreview(
    showPreview: Boolean = false,
    createdOn: Long = System.currentTimeMillis(),
    imageBitmap: ImageBitmap,
    onClick: (PreviewIntent) -> Unit
) {

    AnimatedContent(
        targetState = showPreview,
        transitionSpec = {
            if (targetState > initialState) {
                (slideInVertically { height -> height } + fadeIn()).togetherWith(
                    slideOutVertically { height -> -height } + fadeOut())
            } else {
                (slideInVertically { height -> -height } + fadeIn()).togetherWith(
                    slideOutVertically { height -> height } + fadeOut())
            }.using(
                SizeTransform(clip = false)
            )
        },
        label = ""
    ) {
        if (it) {
            BackHandler {
                onClick(PreviewIntent.Navigate)
            }
            Scaffold(
                bottomBar = {
                    BottomBar(onClick = onClick)
                },
                content = { padding ->
                    ViewContainer(modifier = Modifier.padding(padding), imageBitmap = imageBitmap)
                },
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    AppBar(
                        onClick = onClick,
                        createdOn = createdOn
                    )
                },
            )
        }
    }
}


@Composable
private fun AppBar(
    modifier: Modifier = Modifier,
    createdOn: Long = System.currentTimeMillis(),
    onClick: (PreviewIntent) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(color = MaterialTheme.colorScheme.surface),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            space = 10.dp,
            alignment = Alignment.Start
        )
    ) {

        IconButton(onClick = { onClick(PreviewIntent.Navigate) }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_caret_down),
                contentDescription = stringResource(
                    id = R.string.back
                ),
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.rotate(90f)
            )
        }
        Column {
            Text(text = stringResource(id = R.string.app_name))
            Text(
                text = createdOn.toDate("MMM dd, yyyy hh:mm a"),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun ViewContainer(modifier: Modifier = Modifier, imageBitmap: ImageBitmap) {
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Image(
            bitmap = imageBitmap,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )

    }
}

@Composable
private fun BottomBar(onClick: (PreviewIntent) -> Unit) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background, modifier = Modifier.height(70.dp)
    ) {
        NavigationBarItem(selected = false,
            onClick = { onClick(PreviewIntent.Delete) },
            icon = {
                BottomBarItem(
                    icon = Icons.Rounded.Delete,
                    label = stringResource(id = R.string.delete)
                )
            }
        )
    }
}

@Composable
private fun BottomBarItem(icon: ImageVector, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, fontSize = 12.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PreviewTheme {
        ImagePreview(imageBitmap = ImageBitmap.imageResource(id = R.drawable.intro_img_two)) {}
    }
}