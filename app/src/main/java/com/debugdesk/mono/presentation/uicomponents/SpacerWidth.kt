package com.debugdesk.mono.presentation.uicomponents

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
fun SpacerWidth(value: Dp) {
    Spacer(modifier = Modifier.width(value))
}