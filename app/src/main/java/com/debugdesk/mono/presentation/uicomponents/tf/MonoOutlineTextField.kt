package com.debugdesk.mono.presentation.uicomponents.tf

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.debugdesk.mono.R
import com.debugdesk.mono.presentation.uicomponents.PreviewTheme
import com.debugdesk.mono.utils.CommonColor
import com.debugdesk.mono.utils.Dp.dp1
import com.debugdesk.mono.utils.Dp.dp10
import com.debugdesk.mono.utils.Dp.dp56

@Composable
fun MonoOutlineTextField(
    modifier: Modifier = Modifier,
    leadingIcon: Int? = null,
    leadingIconCompose: @Composable (RowScope.() -> Unit)? = null,
    trailingIcon: Int? = null,
    placeHolderText: String,
    textStyle: TextStyle,
    cornerShape: Dp = dp10,
    charLimit: Int = 10,
    borderWidth: Dp = dp1,
    keyBoardControl: SoftwareKeyboardController? = LocalSoftwareKeyboardController.current,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    height: Dp = TextFieldDefaults.MinHeight,
    imeAction: ImeAction,
    inFocus: Boolean = interactionSource.collectIsFocusedAsState().value,
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    value: String,
    textOutlineEnabled: Boolean = true,
    focusManager: FocusManager = LocalFocusManager.current,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    trailingClick: () -> Unit,
    errorMsg: (String) -> Unit = {},
    onAction: () -> Unit = {},
    fieldClickBack: () -> Unit = {},
) {
    val context = LocalContext.current
    val color by animateColorAsState(
        targetValue =
        when {
            inFocus -> MaterialTheme.colorScheme.primary
            textOutlineEnabled -> CommonColor.disableButton
            else -> Color.Transparent
        },
        label = "color",
    )

    Row(
        modifier =
        modifier
            .fillMaxWidth()
            .height(height)
            .border(
                width = borderWidth,
                color = color,
                shape = RoundedCornerShape(cornerShape),
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        leadingIcon?.let {
            Image(
                painter = painterResource(id = leadingIcon),
                contentDescription = "LeadingIcon",
                contentScale = ContentScale.Inside,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.background),
                modifier =
                Modifier
                    .background(
                        MaterialTheme.colorScheme.primary,
                        shape =
                        RoundedCornerShape(
                            topStart = cornerShape, bottomStart = cornerShape,
                        ),
                    )
                    .size(width = TextFieldDefaults.MinHeight, height = height),
            )
            VerticalDivider(
                modifier =
                Modifier
                    .size(width = 1.dp, height = height)
                    .background(color = CommonColor.disableButton),
            )
        } ?: leadingIconCompose?.invoke(this)

        TextField(
            value = value,
            onValueChange = {
                when {
                    it.length == charLimit -> {
                        when (imeAction) {
                            ImeAction.Next -> {
                                focusManager.moveFocus(
                                    focusDirection = FocusDirection.Next,
                                )
                            }

                            ImeAction.Done, ImeAction.Go, ImeAction.Search -> {
                                onAction()
                                keyBoardControl?.hide()
                                focusManager.clearFocus(true)
                            }
                        }
                        onValueChange(it)
                    }

                    it.length <= charLimit -> {
                        onValueChange(it)
                    }

                    else -> {
                        errorMsg(context.getString(R.string.characterWarning))
                        when (imeAction) {
                            ImeAction.Done -> {
                                keyBoardControl?.hide()
                                focusManager.clearFocus()
                            }

                            ImeAction.Next -> {
                                focusManager.moveFocus(
                                    focusDirection = FocusDirection.Next,
                                )
                            }
                        }
                    }
                }
            },
            textStyle = textStyle,
            placeholder = {
                Text(
                    text = placeHolderText,
                    style = textStyle.copy(color = CommonColor.disableButton),
                )
            },
            enabled = enabled,
            interactionSource = interactionSource,
            shape = RoundedCornerShape(cornerShape),
            singleLine = singleLine,
            keyboardOptions = KeyboardOptions(imeAction = imeAction, keyboardType = keyboardType),
            keyboardActions =
            KeyboardActions(
                onDone = {
                    keyBoardControl?.hide()
                    focusManager.clearFocus()
                },
                onNext = {
                    focusManager.moveFocus(FocusDirection.Next)
                },
            ),
            colors =
            TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                disabledContainerColor = MaterialTheme.colorScheme.background,
                focusedIndicatorColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.primary,
                disabledIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            modifier =
            Modifier
                .weight(1f)
                .height(height)
                .clickable {
                    focusManager.clearFocus()
                    fieldClickBack()
                }
                .background(MaterialTheme.colorScheme.background),
        )

        trailingIcon?.let {
            Image(
                painter = painterResource(id = trailingIcon),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                contentDescription = "trailingIcon",
                contentScale = ContentScale.Inside,
                modifier =
                Modifier
                    .size(height)
                    .clickable { trailingClick() },
            )
        }
    }
}

@Preview
@Composable
fun MonoOutlineTextFieldPrev() {
    PreviewTheme {
        MonoOutlineTextField(
            leadingIcon = R.drawable.ic_rupee,
            trailingIcon = R.drawable.camera,
            placeHolderText = "Enter Amount",
            textStyle = TextStyle(),
            height = dp56,
            imeAction = ImeAction.Done,
            value = "",
            textOutlineEnabled = false,
            onValueChange = {},
            enabled = true,
            trailingClick = {},
            fieldClickBack = {},
        )
    }
}
