package com.debugdesk.mono.presentation.uicomponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.debugdesk.mono.R
import com.debugdesk.mono.utils.CommonColor.disableButton

@Composable
fun CustomOutlineTextField(
    modifier: Modifier = Modifier,
    top: Dp = 0.dp,
    bottom: Dp = 0.dp,
    start: Dp = 0.dp,
    end: Dp = 0.dp,
    leadingIcon: Int? = null,
    trailingIcon: Int? = null,
    cornerShape: Dp = 10.dp,
    height: Dp = TextFieldDefaults.MinHeight,
    placeHolderText: String,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    singleLine: Boolean = true,
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Decimal,
    imeAction: ImeAction = ImeAction.Done,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.Words,
    charLimit: Int = 10,
    value: String,
    onValueChange: (String) -> Unit,
    action: () -> Unit = {},
    onHeightChange: (Int) -> Unit = {},
    fieldClickBack: () -> Unit = {},
    trailingClick: () -> Unit = {},
    hasFocus: (Boolean) -> Unit = {},
    errorMsg: (String) -> Unit = {},
) {
    val focusManager = LocalFocusManager.current
    val keyBoardControl = LocalSoftwareKeyboardController.current
    val interaction = remember { MutableInteractionSource() }
    val inFocus by interaction.collectIsFocusedAsState()
//        val width = LocalConfiguration.current.screenWidthDp
    val context = LocalContext.current

    Row(horizontalArrangement = when {
        leadingIcon != null && trailingIcon == null -> Arrangement.Start
        leadingIcon == null && trailingIcon != null -> Arrangement.SpaceBetween
        else -> Arrangement.Center
    },
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(
                top = top, start = start, end = end, bottom = bottom
            )
            .onSizeChanged { size ->
                onHeightChange(size.height)
            }
            .border(width = 1.dp,
                color = MaterialTheme.colorScheme.primary.takeIf { inFocus } ?: disableButton,
                shape = RoundedCornerShape(cornerShape))
            .defaultMinSize(
                minWidth = TextFieldDefaults.MinWidth, minHeight = height
            )
            .background(
                MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(cornerShape)
            )) {
        if (leadingIcon != null) {
            Image(
                painter = painterResource(id = leadingIcon),
                contentDescription = "LeadingIcon",
                contentScale = ContentScale.Inside,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.background),
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(
                            topStart = cornerShape, bottomStart = cornerShape
                        )
                    )
                    .size(width = TextFieldDefaults.MinHeight, height = height)
            )
            VerticalDivider(
                modifier = Modifier
                    .size(width = 1.dp, height = height)
                    .background(color = disableButton)
            )
        }
        TextField(value = value,
            textStyle = textStyle,
            singleLine = singleLine,
            enabled = enabled,
            interactionSource = interaction,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction,
                capitalization = capitalization
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    action()
                    keyBoardControl?.hide()
                    focusManager.clearFocus(true)
                },
                onNext = {
                    focusManager.moveFocus(
                        focusDirection = FocusDirection.Next
                    )
                },
                onGo = {
                    action()
                    keyBoardControl?.hide()
                    focusManager.clearFocus(true)
                },
                onSearch = {
                    action()
                    keyBoardControl?.hide()
                    focusManager.clearFocus(true)
                },
                onSend = {
                    action()
                    keyBoardControl?.hide()
                    focusManager.clearFocus(true)
                },
            ),
            onValueChange = {
                when {
                    it.length == charLimit -> {
                        when (imeAction) {
                            ImeAction.Next -> {
                                focusManager.moveFocus(
                                    focusDirection = FocusDirection.Next
                                )
                            }

                            ImeAction.Done, ImeAction.Go, ImeAction.Search -> {
                                action()
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
                                    focusDirection = FocusDirection.Next
                                )
                            }
                        }

                    }
                }
            },
            placeholder = {
                Text(text = placeHolderText, style = textStyle.copy(color = disableButton))
            },
            shape = RoundedCornerShape(cornerShape),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                disabledContainerColor = MaterialTheme.colorScheme.background,
                focusedIndicatorColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.primary,
                disabledIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = if (leadingIcon == null && trailingIcon == null) {
                modifier
                    .weight(1f)
                    .clickable { fieldClickBack() }

            } else modifier.clickable { fieldClickBack() })
        if (trailingIcon != null) {
            Image(painter = painterResource(id = trailingIcon),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                contentDescription = "trailingIcon",
                contentScale = ContentScale.Inside,
                modifier = Modifier
                    .size(height)
                    .clickable { trailingClick() })
        }
    }
    hasFocus(inFocus)
}