package com.debugdesk.mono.utils.states

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import com.debugdesk.mono.R
import com.debugdesk.mono.presentation.uicomponents.animatedPainterResource
import com.debugdesk.mono.utils.Dp

data class AlertState(
    @StringRes
    val title: Int = R.string.alert,
    @StringRes
    val message: Int = R.string.delete_all_transactions,
    @StringRes
    val positiveButtonText: Int = R.string.okay,
    @StringRes
    val negativeButtonText: Int = R.string.cancel,
    val drawable: Drawable = Drawable.Static(),
    val iconColor: Color = Color.Unspecified,
    val properties: DialogProperties = DialogProperties(
        dismissOnBackPress = true,
        dismissOnClickOutside = true
    ),
    val show: Boolean = false,
    val showIcon: Boolean = true,
    val onNegativeClick: () -> Unit = {},
    val onPositiveClick: () -> Unit = {}
) {
    companion object {
        val NONE = AlertState(
            title = R.string.alert,
            message = R.string.delete_all_transactions,
            positiveButtonText = R.string.okay,
            negativeButtonText = R.string.cancel,
            onNegativeClick = {}
        )
    }

    val negativeText: String
        @Composable
        get() = stringResource(id = negativeButtonText)


    val positiveText: String
        @Composable
        get() = stringResource(id = positiveButtonText)

    private val drawableRes: Pair<Painter?, Color>
        @Composable
        get() = when (val drawable = drawable) {
            is Drawable.Animated -> drawable.icon?.let {
                animatedPainterResource(
                    it
                )
            } to drawable.tintColor

            is Drawable.Static -> drawable.icon?.let { painterResource(id = it) } to drawable.tintColor
        }

    val iconCompose: @Composable (() -> Unit)?
        get() {
            return if (showIcon) {
                {
                    drawableRes.first?.let {
                        Icon(
                            painter = it,
                            contentDescription = "Icon",
                            modifier = Modifier.size(Dp.dp48),
                            tint = drawableRes.second
                        )
                    }
                }
            } else {
                null
            }

        }


}


sealed class Drawable {
    data class Animated(
        @DrawableRes
        val icon: Int? = R.drawable.ringer_bell,
        val tintColor: Color = Color.Unspecified,
    ) : Drawable()

    data class Static(
        @DrawableRes
        val icon: Int? = R.drawable.ic_warning,
        val tintColor: Color = Color.Unspecified,
    ) : Drawable()
}
