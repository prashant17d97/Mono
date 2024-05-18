package com.debugdesk.mono.presentation.uicomponents.amounttf

import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.getCurrencyIcon

data class AmountTfState(
    val currencyIcon: String = "₹",
    val height: Int = 56,
    val catIndex: Int = 0,
    val openDialog: Boolean = false,
    val hasFocus: Boolean = false,
    val amountValue: String = "0.0",
){

    val icon: Int
        get() = currencyIcon.getCurrencyIcon()
}