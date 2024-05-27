package com.debugdesk.mono.utils.states

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.DialogProperties
import com.debugdesk.mono.R

data class AlertState(
    @StringRes
    val title: Int = R.string.alert,
    @StringRes
    val message: Int = R.string.delete_all_transactions,
    @StringRes
    val positiveButtonText: Int = R.string.okay,
    @StringRes
    val negativeButtonText: Int = R.string.cancel,
    @DrawableRes
    val iconDrawable: Int? = R.drawable.ic_warning,
    val iconColor: Color = Color.Unspecified,
    val properties: DialogProperties = DialogProperties(
        dismissOnBackPress = true,
        dismissOnClickOutside = true
    ),
    val show: Boolean = false,
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
        ) {}
    }
}
