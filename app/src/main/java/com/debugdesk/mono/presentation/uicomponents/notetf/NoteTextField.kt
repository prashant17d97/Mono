package com.debugdesk.mono.presentation.uicomponents.notetf

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.debugdesk.mono.R
import com.debugdesk.mono.presentation.uicomponents.PreviewTheme
import com.debugdesk.mono.presentation.uicomponents.tf.MonoOutlineTextField
import com.debugdesk.mono.utils.CommonColor
import com.debugdesk.mono.utils.Dp.dp1
import com.debugdesk.mono.utils.Dp.dp10
import com.debugdesk.mono.utils.Dp.dp250

@Composable
fun NoteTextField(
    noteState: NoteState,
    onNoteChange: (NoteIntent) -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val inFocus by interaction.collectIsFocusedAsState()
    val focusManager = LocalFocusManager.current

    Column {
        Text(
            text = stringResource(id = R.string.note),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(dp10)
        )
        Column(verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .border(width = dp1,
                    color = MaterialTheme.colorScheme.primary.takeIf { inFocus }
                        ?: CommonColor.disableButton,
                    shape = RoundedCornerShape(dp10))) {

            MonoOutlineTextField(
                trailingIcon = R.drawable.ic_camera,
                placeHolderText = stringResource(id = R.string.input),
                textStyle = MaterialTheme.typography.bodyMedium,
                charLimit = 100,
                inFocus = inFocus,
                interactionSource = interaction,
                focusManager = focusManager,
                cornerShape = dp10,
                imeAction = ImeAction.Done,
                value = noteState.noteValue,
                onValueChange = {
                    onNoteChange(NoteIntent.OnValueChange(it))
                },
                enabled = true,
                trailingClick = { onNoteChange(NoteIntent.OnTrailIconClick) },
                fieldClickBack = { }
            )

            AnimatedVisibility(visible = false) {
                Image(
                    modifier = Modifier.size(dp250),
                    painter = painterResource(id = R.drawable.intro_img_one),
                    contentDescription = ""
                )
            }
        }
    }

}

@Preview
@Composable
fun NoteTextFieldPrev() {
    PreviewTheme {
        NoteTextField(noteState = NoteState(noteValue = "")) { }
    }
}