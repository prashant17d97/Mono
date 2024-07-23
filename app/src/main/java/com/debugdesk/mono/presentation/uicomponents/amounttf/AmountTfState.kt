package com.debugdesk.mono.presentation.uicomponents.amounttf

import androidx.annotation.DrawableRes
import com.debugdesk.mono.R

data class AmountTfState(
    @DrawableRes
    val currencyIcon: Int = R.drawable.ic_rupee,
    val height: Int = 56,
    val catIndex: Int = 0,
    val openDialog: Boolean = false,
    val hasFocus: Boolean = false,
    val amountValue: String = "",
)
