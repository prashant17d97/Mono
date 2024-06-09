package com.debugdesk.mono.presentation.uicomponents

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.debugdesk.mono.R
import com.debugdesk.mono.utils.Dp.dp180
import com.debugdesk.mono.utils.Dp.dp20
import com.debugdesk.mono.utils.Dp.dp40

@Composable
fun NoDataFoundLayout(
    modifier: Modifier = Modifier,
    @DrawableRes
    image: Int = R.drawable.empty,
    imageSize: Dp = dp180,
    @StringRes
    text: Int = R.string.noTransactionFound,
    show: Boolean = true,
    content: @Composable AnimatedVisibilityScope.() -> Unit = {}
) {
    val density = LocalDensity.current

    Log.d("TAG", "NoDataFoundLayout: $show")
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Common properties for enter and exit transitions
        val enterTransition = slideInVertically {
            with(density) { (dp40).roundToPx() }
        } + expandVertically(
            expandFrom = Alignment.Top,
            animationSpec = tween(500)
        ) + fadeIn(initialAlpha = 0.3f, animationSpec = tween(500))

        val exitTransition = slideOutVertically() + shrinkVertically() + fadeOut()

        AnimatedVisibility(
            visible = show,
            enter = enterTransition,
            exit = exitTransition
        ) {
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(dp20, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = image),
                    contentDescription = "No Data Found",
                    modifier = Modifier.size(imageSize),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                )
                if (text != 0) {
                    Text(text = stringResource(id = text))
                }
            }
        }

        AnimatedVisibility(
            visible = !show,
            enter = enterTransition,
            exit = exitTransition,
            content = content
        )
    }

}


@Preview
@Composable
private fun NDFPreview() {
    PreviewTheme {
        NoDataFoundLayout(text = R.string.no_data_found, show = true)
    }
}