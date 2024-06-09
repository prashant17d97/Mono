package com.debugdesk.mono.presentation.uicomponents.notetf

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.debugdesk.mono.ui.appconfig.AppStateManager
import com.debugdesk.mono.ui.appconfig.AppStateManagerImpl
import com.debugdesk.mono.utils.CameraFunction.deleteFile
import com.debugdesk.mono.utils.CommonColor
import com.debugdesk.mono.utils.Dp.dp0
import com.debugdesk.mono.utils.Dp.dp1
import com.debugdesk.mono.utils.Dp.dp10
import com.debugdesk.mono.utils.Dp.dp16
import com.debugdesk.mono.utils.enums.ImageFrom

@Composable
fun NoteTextField(
    noteState: NoteState,
    appStateManager: AppStateManager,
    onNoteChange: (TransactionIntent) -> Unit = {},
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
                trailingIcon = R.drawable.ic_gallary,
                placeHolderText = stringResource(id = R.string.input),
                textStyle = MaterialTheme.typography.bodyMedium,
                charLimit = 100,
                inFocus = inFocus,
                interactionSource = interaction,
                focusManager = focusManager,
                borderWidth = dp0,
                cornerShape = dp10,
                textOutlineEnabled = noteState.transactionImages.isEmpty(),
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

            AnimatedVisibility(visible = noteState.transactionImages.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.Top, modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = dp16,
                            end = dp16,
                            top = if (!inFocus) dp0 else dp16,
                            bottom = dp16
                        )
                ) {
                    itemsIndexed(noteState.transactionImages) { index, item ->
                        ImageCard(
                            absolutePath = item.absolutePath,
                            onDelete = {
                                Log.e("NoteTextField", "CameraAndGallery: ${item.from}")

                                if (item.from == ImageFrom.CAMERA.name) {
                                    deleteFile(item.absolutePath, onResult = { success, notFound ->
                                        val message = if (notFound) {
                                            R.string.image_deleted
                                        } else if (success) {
                                            R.string.image_deleted
                                        } else {
                                            R.string.image_deleted_failed
                                        }
                                        appStateManager.showToastState(toastMsg = message)
                                    })
                                } else {
                                    onNoteChange(
                                        TransactionIntent.UpdateNote(
                                            NoteIntent.DeleteFromDB(
                                                item
                                            )
                                        )
                                    )
                                    appStateManager.showToastState(toastMsg = R.string.image_deleted)
                                }
                                onNoteChange(
                                    TransactionIntent.UpdateNote(
                                        NoteIntent.DeleteImages(
                                            noteState.transactionImages.filter {
                                                it.absolutePath != item.absolutePath
                                            })
                                    )
                                )
                            },
                            onImageClick = {
                                onNoteChange(
                                    TransactionIntent.UpdateNote(
                                        NoteIntent.ShowGallery(index)
                                    )
                                )
                            })
                    }
                }
            }
        }
    }

}

@Preview
@Composable
fun NoteTextFieldPrev() {
    PreviewTheme {
        NoteTextField(
            appStateManager = AppStateManagerImpl(),
            noteState = NoteState(noteValue = "")
        )
    }
}