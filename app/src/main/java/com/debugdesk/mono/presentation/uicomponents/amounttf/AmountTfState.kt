package com.debugdesk.mono.presentation.uicomponents.amounttf

import com.debugdesk.mono.R

data class AmountTfState(
    val currencyIcon: Int = R.string.inrIcon,
    val height: Int = 56,
    val catIndex: Int = 0,
    val openDialog: Boolean = false,
    val hasFocus: Boolean = false,
    val amountValue: String = "0.0",
)