package com.debugdesk.mono.presentation.uicomponents

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.debugdesk.mono.R
import com.debugdesk.mono.utils.Dp.dp160
import com.debugdesk.mono.utils.Dp.dp20

@Composable
fun NoDataFoundLayout(
    modifier: Modifier = Modifier,
    @DrawableRes
    image: Int = R.drawable.intro_img_two,
    imageSize: Dp = dp160,
    @StringRes
    text: Int = R.string.noTransactionFound,
    show: Boolean = false,
    content: @Composable AnimatedVisibilityScope.() -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AnimatedContent(
            targetState = show,
            transitionSpec = {
                if (targetState > initialState) {
                    (slideInVertically { height -> height } + fadeIn()).togetherWith(
                        slideOutVertically { height -> -height } + fadeOut(),
                    )
                } else {
                    (slideInVertically { height -> -height } + fadeIn()).togetherWith(
                        slideOutVertically { height -> height } + fadeOut(),
                    )
                }.using(
                    SizeTransform(clip = false),
                )
            },
            label = "",
        ) {
            if (it) {
                Column(
                    modifier = modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(dp20, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painter = painterResource(id = image),
                        contentDescription = "No Data Found",
                        modifier = Modifier.size(imageSize),
                    )
                    if (text != 0) {
                        Text(text = stringResource(id = text))
                    }
                }
            } else {
                content()
            }
        }
    }
}

@Preview
@Composable
private fun NDFPreview() {
    PreviewTheme {
        NoDataFoundLayout(text = R.string.no_data_found, show = true)
    }
}
