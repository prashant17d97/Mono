package com.debugdesk.mono.presentation.edittrans

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.debugdesk.mono.R
import com.debugdesk.mono.presentation.uicomponents.CalendarCard
import com.debugdesk.mono.presentation.uicomponents.CustomButton
import com.debugdesk.mono.presentation.uicomponents.ImageGallery
import com.debugdesk.mono.presentation.uicomponents.MonoColumn
import com.debugdesk.mono.presentation.uicomponents.PreviewTheme
import com.debugdesk.mono.presentation.uicomponents.SpacerHeight
import com.debugdesk.mono.presentation.uicomponents.amounttf.AmountTextFieldCalculator
import com.debugdesk.mono.presentation.uicomponents.editcategory.EditCategoryCard
import com.debugdesk.mono.presentation.uicomponents.media.CameraAndGallery
import com.debugdesk.mono.presentation.uicomponents.notetf.NoteTextField
import com.debugdesk.mono.utils.CommonColor
import com.debugdesk.mono.utils.Dp.dp10
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.toDateWeek
import com.debugdesk.mono.utils.enums.Buttons
import org.koin.androidx.compose.koinViewModel


@Composable
fun EditTransaction(
    navHostController: NavHostController,
    transactionId: Int?,
    editTransactionViewModel: EditTransactionVM = koinViewModel(),
) {
    LocalContext.current
    val editTransactionState by editTransactionViewModel.editTransactionState.collectAsState()
    LaunchedEffect(transactionId) {
        Log.e("TAG", "EditTransaction: $transactionId")
        transactionId?.let { editTransactionViewModel.getDailyTransaction(it) }
    }
    EditTransactionContainer(
        transactionState = editTransactionState,
        onEditTransactionIntent = {
            editTransactionViewModel.handleTransactionIntent(it, navHostController)
        }
    )

}

@Composable
fun EditTransactionContainer(
    transactionState: TransactionState,
    onEditTransactionIntent: (TransactionIntent) -> Unit = {},
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(vertical = dp10),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        MonoColumn(
            heading = transactionState.transaction.date.toDateWeek(),
            showBack = true,
            isScrollEnabled = false,
            onTrailClick = {
                onEditTransactionIntent(TransactionIntent.OnDeleteClick)
            },
            onBackClick = {
                onEditTransactionIntent(TransactionIntent.OnBackClick)
            },
            trailing = stringResource(id = R.string.delete),
            trailingColor = CommonColor.inActiveButton,
            verticalArrangement = Arrangement.spacedBy(dp10, alignment = Alignment.Top),
        ) {
            SpacerHeight(value = dp10)

            CalendarCard(
                date = transactionState.transaction.date,
                showDialog = transactionState.showCalendarDialog,
                onShowCalendarDialog = {
                    onEditTransactionIntent(
                        TransactionIntent.OpenCalendarDialog(
                            it
                        )
                    )
                },
                onDateChange = { onEditTransactionIntent(TransactionIntent.UpdateDate(it)) }
            )

            AmountTextFieldCalculator(
                amountTfState = transactionState.amountTfState,
                onTextFieldCalculatorIntent = {
                    onEditTransactionIntent(
                        TransactionIntent.UpdateAmount(
                            it
                        )
                    )
                }
            )

            NoteTextField(
                appStateManager = transactionState.appStateManager,
                noteState = transactionState.noteState,
                onNoteChange = onEditTransactionIntent
            )

            EditCategoryCard(
                list = transactionState.categoryList,
                onCategoryEdit = onEditTransactionIntent
            )
        }

        CustomButton(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
            status = Buttons.Active.takeIf { transactionState.changesFound }
                ?: Buttons.Disable,
            text = stringResource(id = R.string.update),
            onClick = {
                onEditTransactionIntent(TransactionIntent.OnUpdateClick)
            })
    }

    AnimatedVisibility(
        visible = transactionState.showCameraAndGallery,
        enter = slideInVertically { it },
        exit = slideOutVertically { it }) {
        CameraAndGallery(
            appStateManager = transactionState.appStateManager,
            images = transactionState.transaction.transactionImage,
            onProcess = onEditTransactionIntent
        )
    }


    AnimatedVisibility(
        visible = transactionState.showImageGallery,
        enter = scaleIn(
            initialScale = 0.5f,
            animationSpec = tween(durationMillis = 500)
        ),
        exit = scaleOut(
            targetScale = 0.5f,
            animationSpec = tween(durationMillis = 500)
        )
    ) {
        ImageGallery(
            images = transactionState.transaction.transactionImage,
            clickedIndex = transactionState.clickedIndex,
            onDelete = { transactionImage ->
                onEditTransactionIntent(
                    TransactionIntent.DeleteImage(
                        transactionState.noteState.transactionImages.filter { it != transactionImage }
                    )
                )
            },
            close = { onEditTransactionIntent(TransactionIntent.CloseImageGallery) })
    }
}


@Preview
@Composable
fun EditTransactionPrev() {
    PreviewTheme {
        EditTransactionContainer(
            transactionState = TransactionState(),
            onEditTransactionIntent = {})
    }
}
