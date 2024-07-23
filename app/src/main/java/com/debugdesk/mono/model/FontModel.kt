package com.debugdesk.mono.model

data class FontModel(
    var font: String,
    var isSelected: Boolean,
)

data class LanguageModel(
    val language: String,
    var isSelected: Boolean,
)
