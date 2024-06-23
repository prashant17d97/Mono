package com.debugdesk.mono.presentation.uicomponents.notetf

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.debugdesk.mono.R
import com.debugdesk.mono.presentation.edittrans.TransactionIntent
import com.debugdesk.mono.presentation.uicomponents.ImageCard
import com.debugdesk.mono.presentation.uicomponents.PreviewTheme
import com.debugdesk.mono.presentation.uicomponents.tf.MonoOutlineTextField
import com.debugdesk.mono.utils.CommonColor
import com.debugdesk.mono.utils.Dp.dp0
import com.debugdesk.mono.utils.Dp.dp1
import com.debugdesk.mono.utils.Dp.dp10

@Composable
fun NoteTextField(
    noteState: NoteState,
    onNoteChange: (TransactionIntent) -> Unit = {},
    onImageClick: () -> Unit = {}
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
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .border(width = dp1,
                    color = MaterialTheme.colorScheme.primary.takeIf { inFocus }
                        ?: CommonColor.disableButton,
                    shape = RoundedCornerShape(dp10))) {

            MonoOutlineTextField(
                trailingIcon = R.drawable.ic_gallary,
                placeHolderText = stringResource(id = R.string.input),
                textStyle = MaterialTheme.typography.bodyMedium,
                charLimit = 100,
                inFocus = inFocus,
                interactionSource = interaction,
                focusManager = focusManager,
                borderWidth = dp0,
                cornerShape = dp10,
                textOutlineEnabled = noteState.imagePath.isEmpty(),
                imeAction = ImeAction.Done,
                value = noteState.noteValue,
                onValueChange = {
                    onNoteChange(TransactionIntent.UpdateNote(NoteIntent.OnValueChange(it)))
                },
                enabled = true,
                trailingClick = {
                    focusManager.clearFocus()
                    onNoteChange(TransactionIntent.UpdateNote(NoteIntent.OnTrailIconClick))
                },
            )

            Log.d(
                "TAG",
                "NoteTextField: ${noteState.imagePath.isEmpty()} ${noteState.imagePath}"
            )
            ImageCard(
                imageByteArray = noteState.imagePath,
                onDelete = {
                    onNoteChange(
                        TransactionIntent.UpdateNote(
                            NoteIntent.DeleteImage
                        )
                    )
                },
                onImageClick = onImageClick
            )
        }
    }
}


@Preview
@Composable
fun NoteTextFieldPrev() {
    PreviewTheme {
        NoteTextField(
            noteState = NoteState()
        )
    }
}