package com.debugdesk.mono.presentation.imgpreview

sealed class PreviewIntent {
    data object Navigate : PreviewIntent()

    data object Delete : PreviewIntent()
}
