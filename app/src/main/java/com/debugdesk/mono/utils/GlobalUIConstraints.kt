package com.debugdesk.mono.utils

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

object GlobalUIConstraints {

    val Modifier.globalPadding: Modifier
        get() = this.padding(
            vertical = 16.dp,
            horizontal = 24.dp
        )
}