package com.debugdesk.mono.presentation.imgpreview


import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.debugdesk.mono.R
import com.debugdesk.mono.presentation.uicomponents.PreviewTheme
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.toDate

@Composable
fun ImagePreview(
    showPreview: Boolean = false,
    createdOn: Long = System.currentTimeMillis(),
    size: String = "",
    painter: Painter,
    onClick: (PreviewIntent) -> Unit
) {
    AnimatedContent(
        targetState = showPreview, transitionSpec = {
            if (targetState > initialState) {
                (slideInVertically { height -> height } + fadeIn()).togetherWith(slideOutVertically { height -> -height } + fadeOut())
            } else {
                (slideInVertically { height -> -height } + fadeIn()).togetherWith(slideOutVertically { height -> height } + fadeOut())
            }.using(
                SizeTransform(clip = false)
            )
        }, label = ""
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
                    ViewContainer(
                        modifier = Modifier.padding(padding), painter = painter
                    )
                },
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    AppBar(
                        onClick = onClick, createdOn = createdOn, imageSize = size
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
    imageSize: String,
    onClick: (PreviewIntent) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(color = MaterialTheme.colorScheme.surface),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            space = 10.dp, alignment = Alignment.Start
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
        Column(verticalArrangement = Arrangement.spacedBy(4.dp, alignment = Alignment.Top)) {
            Text(text = stringResource(id = R.string.app_name))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp, alignment = Alignment.Start)) {
                Text(
                    text = createdOn.toDate("MMM dd, yyyy hh:mm a"),
                    style = MaterialTheme.typography.bodySmall
                )
                if (imageSize.isNotEmpty()) {
                    Text(
                        text = imageSize, style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun ViewContainer(modifier: Modifier = Modifier, painter: Painter) {
    var scale by remember { mutableFloatStateOf(1f) }
    val animatedScale by animateFloatAsState(targetValue = scale, label = "")

    var zoomed by remember { mutableStateOf(false) }
    var zoomOffset by remember { mutableStateOf(Offset(0f, 0f)) }
    val animatedZoomOffset by animateOffsetAsState(targetValue = zoomOffset, label = "zoom")
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(1f, 5f)
                    val maxOffsetX = (size.width * (scale - 1)) / 2
                    val maxOffsetY = (size.height * (scale - 1)) / 2
                    if (zoomed) {
                        zoomOffset = Offset(
                            (zoomOffset.x + pan.x).coerceIn(-maxOffsetX, maxOffsetX),
                            (zoomOffset.y + pan.y).coerceIn(-maxOffsetY, maxOffsetY)
                        )
                    }
                }

            }, contentAlignment = Alignment.BottomCenter

    ) {

        Image(painter = painter,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
                .pointerInput(Unit) {
                    detectTapGestures(onDoubleTap = { tapOffSet ->
                        if (zoomed) {
                            scale = 1f
                            zoomed = false
                            zoomOffset = Offset.Zero
                        } else {
                            zoomed = true
                            scale = 2.5f
                            zoomOffset = calculateOffset(tapOffSet, size)
                        }
                    })
                }
                .graphicsLayer {
                    scaleX = maxOf(.5f, minOf(5f, animatedScale))
                    scaleY = maxOf(.5f, minOf(5f, animatedScale))
                    translationX = animatedZoomOffset.x
                    translationY = animatedZoomOffset.y
                })

    }
}

@Composable
private fun BottomBar(onClick: (PreviewIntent) -> Unit) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background, modifier = Modifier.height(70.dp)
    ) {
        NavigationBarItem(selected = false, onClick = { onClick(PreviewIntent.Delete) }, icon = {
            BottomBarItem(
                icon = Icons.Rounded.Delete, label = stringResource(id = R.string.delete)
            )
        })
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
        AppBar(imageSize = "2MB") {}
        ViewContainer(
            painter = painterResource(id = R.drawable.intro_img_two)
        )
    }
}

private fun calculateOffset(tapOffset: Offset, size: IntSize): Offset {
    val offsetX =
        (-(tapOffset.x - (size.width / 2f)) * 2f).coerceIn(-size.width / 2f, size.width / 2f)
    return Offset(offsetX, 0f)
}