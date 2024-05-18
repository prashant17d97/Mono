package com.debugdesk.mono.presentation.input

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver


data class PageState(
    val height: Int = 56,
    val inputHeight: Int = 56,
    val catIndex: Int = 0,
    val openDialog: Boolean = false,
    val hasFocus: Boolean = false,
)

val pageStateSaver: Saver<PageState, *> = mapSaver(
    save = {
        mapOf(
            "height" to it.height,
            "inputHeight" to it.inputHeight,
            "catIndex" to it.catIndex,
            "openDialog" to it.openDialog,
            "hasFocus" to it.hasFocus,
        )
    },
    restore = {
        PageState(
            height = it["height"] as Int? ?: 56,
            inputHeight = it["inputHeight"] as Int? ?: 56,
            catIndex = it["catIndex"] as Int? ?: 0,
            openDialog = it["openDialog"] as Boolean? ?: false,
            hasFocus = it["hasFocus"] as Boolean? ?: false,
        )
    }
)
