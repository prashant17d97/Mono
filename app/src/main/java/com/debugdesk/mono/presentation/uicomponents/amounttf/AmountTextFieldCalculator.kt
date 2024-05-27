package com.debugdesk.mono.presentation.uicomponents.amounttf

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.debugdesk.mono.R
import com.debugdesk.mono.presentation.uicomponents.Calculator
import com.debugdesk.mono.presentation.uicomponents.PopUp
import com.debugdesk.mono.presentation.uicomponents.PreviewTheme
import com.debugdesk.mono.presentation.uicomponents.tf.MonoOutlineTextField
import com.debugdesk.mono.utils.CalculatorEnum
import com.debugdesk.mono.utils.Dp.dp65

@Composable
fun AmountTextFieldCalculator(
    amountTfState: AmountTfState,
    onTextFieldCalculatorIntent: (TextFieldCalculatorIntent) -> Unit,
) {

    MonoOutlineTextField(
        leadingIcon = amountTfState.currencyIcon,
        placeHolderText = stringResource(id = R.string.zero),
        textStyle = MaterialTheme.typography.titleLarge,
        imeAction = ImeAction.Next,
        value = amountTfState.amountValue,
        enabled = false,
        height = dp65,
        onValueChange = { onTextFieldCalculatorIntent(TextFieldCalculatorIntent.OnValueChange(it)) },
        trailingClick = {
            onTextFieldCalculatorIntent(
                TextFieldCalculatorIntent.OpenDialog(
                    openDialog = !amountTfState.openDialog
                )
            )
        },
        fieldClickBack = {
            onTextFieldCalculatorIntent(
                TextFieldCalculatorIntent.OpenDialog(
                    openDialog = !amountTfState.openDialog
                )
            )
        }
    )

    if (amountTfState.openDialog) {
        PopUp(dismiss = {
            onTextFieldCalculatorIntent(TextFieldCalculatorIntent.OpenDialog(openDialog = false))
        }) {
            Calculator(dotCount = 1.takeIf { amountTfState.amountValue.contains(".") } ?: 0,
                priorValue = amountTfState.amountValue,
                onValueReturn = { operationType, value ->
                    onTextFieldCalculatorIntent(TextFieldCalculatorIntent.OnValueChange(value))
                    onTextFieldCalculatorIntent(
                        TextFieldCalculatorIntent.OpenDialog(
                            openDialog = when (operationType) {
                                CalculatorEnum.Cancel, CalculatorEnum.Okay -> false
                            }
                        )
                    )
                }
            )
        }
    }
}

@Preview
@Composable
fun AmountPrev() {
    PreviewTheme {
        AmountTextFieldCalculator(
            amountTfState = AmountTfState(
                currencyIcon = R.drawable.ic_currency,
                amountValue = "0.0"
            )
        ) {}
    }
}
