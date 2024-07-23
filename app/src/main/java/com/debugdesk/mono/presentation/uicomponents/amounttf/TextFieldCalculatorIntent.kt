package com.debugdesk.mono.presentation.uicomponents.amounttf

sealed class TextFieldCalculatorIntent {
    data class OnHeightChange(val height: Int) : TextFieldCalculatorIntent()

    data class OnValueChange(val value: String) : TextFieldCalculatorIntent()

    data class OpenDialog(val openDialog: Boolean) : TextFieldCalculatorIntent()
}
