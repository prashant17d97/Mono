package com.debugdesk.mono.presentation.edittrans

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.debugdesk.mono.R
import com.debugdesk.mono.presentation.imgpreview.ImagePreview
import com.debugdesk.mono.presentation.imgpreview.PreviewIntent
import com.debugdesk.mono.presentation.uicomponents.CalendarCard
import com.debugdesk.mono.presentation.uicomponents.CustomButton
import com.debugdesk.mono.presentation.uicomponents.MonoColumn
import com.debugdesk.mono.presentation.uicomponents.PreviewTheme
import com.debugdesk.mono.presentation.uicomponents.TransactionDropDown
import com.debugdesk.mono.presentation.uicomponents.amounttf.AmountTextFieldCalculator
import com.debugdesk.mono.presentation.uicomponents.editcategory.EditCategoryCard
import com.debugdesk.mono.presentation.uicomponents.media.MediaBottomSheet
import com.debugdesk.mono.presentation.uicomponents.notetf.NoteTextField
import com.debugdesk.mono.utils.CommonColor
import com.debugdesk.mono.utils.Dp.dp10
import com.debugdesk.mono.utils.Dp.dp8
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.toDateWeek
import com.debugdesk.mono.utils.enums.Buttons
import org.koin.androidx.compose.koinViewModel

@Composable
fun EditTransaction(
    navHostController: NavHostController,
    transactionId: Int?,
    editTransactionViewModel: EditTransactionVM = koinViewModel(),
) {
    val editTransactionState by editTransactionViewModel.editTransactionState.collectAsState()
    val context = LocalContext.current
    LaunchedEffect(transactionId) {
        transactionId?.let { editTransactionViewModel.getDailyTransaction(it) }
    }
    EditTransactionContainer(
        transactionState = editTransactionState,
        onEditTransactionIntent = {
            editTransactionViewModel.handleTransactionIntent(it, navHostController, context)
        },
    )
}

@Composable
fun EditTransactionContainer(
    transactionState: TransactionState,
    onEditTransactionIntent: (TransactionIntent) -> Unit = {},
) {
    var showPreview by remember {
        mutableStateOf(false)
    }

    var showTransactionType by remember {
        mutableStateOf(false)
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(vertical = dp10),
        verticalArrangement = Arrangement.SpaceBetween,
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
            verticalArrangement = Arrangement.spacedBy(dp8, alignment = Alignment.Top),
        ) {
            CalendarCard(
                date = transactionState.date,
                showDialog = transactionState.showCalendarDialog,
                onShowCalendarDialog = {
                    onEditTransactionIntent(
                        TransactionIntent.OpenCalendarDialog(
                            it,
                        ),
                    )
                },
                onDateChange = { onEditTransactionIntent(TransactionIntent.UpdateDate(it)) },
            )

            AmountTextFieldCalculator(
                amountTfState = transactionState.amountTfState,
                onTextFieldCalculatorIntent = {
                    onEditTransactionIntent(
                        TransactionIntent.UpdateAmount(
                            it,
                        ),
                    )
                },
            )

            TransactionDropDown(
                isExpended = showTransactionType,
                selectedTransaction = transactionState.transaction.type,
                onDropDownClick = {
                    showTransactionType = !showTransactionType
                    onEditTransactionIntent(TransactionIntent.UpdateTransactionType(it))
                },
                dismiss = { showPreview = false },
            )

            NoteTextField(
                note = transactionState.note,
                image = transactionState.transaction.painter,
                textOutlineEnabled = transactionState.transaction.showImage,
                onNoteChange = {
                    onEditTransactionIntent(TransactionIntent.OnValueChange(it))
                },
                onImageClick = {
                    showPreview = true
                },
                onDelete = {
                    onEditTransactionIntent(TransactionIntent.DeleteImage)
                },
                onTrailClick = {
                    onEditTransactionIntent(TransactionIntent.OnTrailIconClick)
                },
            )

            EditCategoryCard(
                list = transactionState.categoryList,
                onCategoryEdit = onEditTransactionIntent,
            )
        }

        CustomButton(
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(10.dp),
            status =
            Buttons.Active.takeIf { transactionState.changesFound }
                ?: Buttons.Disable,
            text = stringResource(id = R.string.update),
            onClick = {
                onEditTransactionIntent(TransactionIntent.OnUpdateClick)
            },
        )
    }

    MediaBottomSheet(
        visible = transactionState.showCameraAndGallery,
        appStateManager = transactionState.appStateManager,
        onProcess = onEditTransactionIntent,
    )

    ImagePreview(
        showPreview = showPreview,
        createdOn = transactionState.createdOn,
        painter = transactionState.transaction.painter,
        size = transactionState.transaction.imageSize,
    ) { previewIntent ->
        when (previewIntent) {
            PreviewIntent.Delete -> {
                showPreview = false
                onEditTransactionIntent(
                    TransactionIntent.DeleteImage,
                )
            }

            PreviewIntent.Navigate -> {
                showPreview = false
            }
        }
    }
}

@Preview
@Composable
fun EditTransactionPrev() {
    PreviewTheme {
        EditTransactionContainer(
            transactionState = TransactionState(),
            onEditTransactionIntent = {},
        )
    }
}
