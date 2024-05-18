package com.debugdesk.mono.presentation.setting.currency

data class RadioModel(
    val currencyIcon: String,
    val defaultCurrencyValue: String = "100.0",
    val currencyCode: String,
    var isSelected: Boolean,
)