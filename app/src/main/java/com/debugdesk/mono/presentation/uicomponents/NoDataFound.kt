package com.debugdesk.mono.presentation.uicomponents

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.debugdesk.mono.R
import com.debugdesk.mono.utils.Dp.dp180
import com.debugdesk.mono.utils.Dp.dp20

@Composable
fun NoDataFound(
    modifier: Modifier = Modifier,
    @DrawableRes
    image: Int = R.drawable.empty,
    imageSize: Dp = dp180,
    @StringRes
    text: Int = 0,
    show: Boolean = false
) {
    AnimatedVisibility(
        visible = show,
        enter = fadeIn(animationSpec = tween(durationMillis = 300)) + slideInVertically(
            animationSpec = tween(durationMillis = 300)
        ) { -it },
        exit = fadeOut(animationSpec = tween(durationMillis = 300)) + slideOutVertically(
            animationSpec = tween(durationMillis = 300)
        ) { -it }
    ) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(
                space = dp20,
                alignment = Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = image),
                contentDescription = "No Data Found",
                modifier = Modifier.size(imageSize),
                colorFilter = ColorFilter.tint(
                    color = MaterialTheme.colorScheme.primary,
                )
            )
            if (text != 0) {
                Text(text = stringResource(id = text))
            }
        }
    }
}


@Preview
@Composable
private fun NDFPreview() {
    PreviewTheme {
        NoDataFound(text = R.string.no_data_found, show = true)
    }
}