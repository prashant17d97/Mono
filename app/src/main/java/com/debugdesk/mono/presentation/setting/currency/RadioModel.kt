package com.debugdesk.mono.presentation.setting.currency

import com.debugdesk.mono.R

data class RadioModel(
    val currencyStringIcon: Int = R.string.inrIcon,
    val defaultCurrencyValue: String = "100.0",
    val currencyCode: Int = R.string.inr,
    val currencyIconDrawable: Int = R.drawable.ic_rupee,
    var isSelected: Boolean,
)
