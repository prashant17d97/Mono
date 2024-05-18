package com.debugdesk.mono.presentation.edittrans

import android.util.Log
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.debugdesk.mono.R
import com.debugdesk.mono.domain.data.local.localdatabase.model.CategoryModel
import com.debugdesk.mono.domain.data.local.localdatabase.model.DailyTransaction
import com.debugdesk.mono.presentation.uicomponents.CalendarCard
import com.debugdesk.mono.presentation.uicomponents.CustomButton
import com.debugdesk.mono.presentation.uicomponents.PreviewTheme
import com.debugdesk.mono.presentation.uicomponents.ScreenView
import com.debugdesk.mono.presentation.uicomponents.SpacerHeight
import com.debugdesk.mono.presentation.uicomponents.amounttf.AmountTextFieldCalculator
import com.debugdesk.mono.presentation.uicomponents.amounttf.AmountTfState
import com.debugdesk.mono.presentation.uicomponents.editcategory.EditCategoryCard
import com.debugdesk.mono.presentation.uicomponents.notetf.NoteState
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
    val editTransactionState by editTransactionViewModel.editTransactionState.collectAsState()
    LaunchedEffect(transactionId) {
        Log.e("TAG", "EditTransaction: $transactionId")
        transactionId?.let { editTransactionViewModel.getDailyTransaction(it) }
    }
    EditTransactionContainer(
        editTransactionState = editTransactionState,
        onEditTransactionIntent = {
            editTransactionViewModel.handleTransactionIntent(it, navHostController)
        }
    )
}

@Composable
fun EditTransactionContainer(
    editTransactionState: EditTransactionState,
    onEditTransactionIntent: (EditTransactionIntent) -> Unit = {},
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(vertical = dp10),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        ScreenView(
            heading = editTransactionState.transaction.date.toDateWeek(),
            showBack = true,
            isScrollEnabled = false,
            onTrailClick = {
                onEditTransactionIntent(EditTransactionIntent.OnDeleteClick)
            },
            onBackClick = {
                onEditTransactionIntent(EditTransactionIntent.OnBackClick)
            },
            trailing = stringResource(id = R.string.delete),
            trailingColor = CommonColor.inActiveButton,
            verticalArrangement = Arrangement.spacedBy(dp10, alignment = Alignment.Top),
        ) {
            SpacerHeight(value = dp10)

            CalendarCard(
                date = editTransactionState.transaction.date,
                showDialog = editTransactionState.showCalendarDialog,
                onShowCalendarDialog = {
                    onEditTransactionIntent(
                        EditTransactionIntent.OpenCalendarDialog(
                            it
                        )
                    )
                },
                onDateChange = { onEditTransactionIntent(EditTransactionIntent.UpdateDate(it)) }
            )

            AmountTextFieldCalculator(
                amountTfState = editTransactionState.amountTfState,
                onTextFieldCalculatorIntent = {
                    onEditTransactionIntent(
                        EditTransactionIntent.UpdateAmount(
                            it
                        )
                    )
                }
            )

            NoteTextField(
                noteState = editTransactionState.noteState,
                onNoteChange = {
                    onEditTransactionIntent(EditTransactionIntent.UpdateNote(it))
                }
            )

            EditCategoryCard(
                list = editTransactionState.categoryList,
                onCategoryEdit = {
                    onEditTransactionIntent(EditTransactionIntent.UpdateCategoryIntent(it))
                }
            )
        }

        CustomButton(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
            status = Buttons.Disable.takeIf { editTransactionState.amountTfState.amountValue.isEmpty() }
                ?: Buttons.Active,
            text = stringResource(id = R.string.update),
            onClick = {
                onEditTransactionIntent(EditTransactionIntent.OnUpdateClick)
            })

    }
}


@Preview
@Composable
fun EditTransactionPrev() {
    PreviewTheme {
        EditTransactionContainer(editTransactionState = EditTransactionState(
            transaction = DailyTransaction(
                transactionId = 1,
                date = 1715507975498,
                amount = 100.0,
                category = "Salary",
                note = "note",
                type = "Income",
                categoryIcon = R.drawable.baby,
                images = listOf(),
                currentMonthId = 4, year = 2024,
                categoryId = 2
            ),
            amountTfState = AmountTfState(
                height = 54,
                catIndex = 5464,
                openDialog = false,
                hasFocus = false,
                amountValue = "33.5"
            ),
            noteState = NoteState(
                noteValue = "note",
            ),
            categoryList = listOf(
                CategoryModel(
                    categoryId = 1,
                    category = "Food",
                    categoryIcon = R.drawable.food,
                    categoryType = "Expense",
                    isSelected = true
                ),
                CategoryModel(
                    categoryId = 2,
                    category = "Salary",
                    categoryIcon = R.drawable.baby,
                    categoryType = "Income",
                    isSelected = false
                ),
                CategoryModel(
                    categoryId = 3,
                    category = "Transport",
                    categoryIcon = R.drawable.train,
                    categoryType = "Expense",
                    isSelected = false
                ),
                CategoryModel(
                    categoryId = 4,
                    category = "Shopping",
                    categoryIcon = R.drawable.coffee,
                    categoryType = "Expense",
                    isSelected = false
                ),
                CategoryModel(
                    categoryId = 5,
                    category = "Rent",
                    categoryIcon = R.drawable.baby,
                    categoryType = "Expense",
                    isSelected = false
                ),
                CategoryModel(
                    categoryId = 6,
                    category = "Others",
                    categoryIcon = R.drawable.books,
                    categoryType = "Expense",
                    isSelected = false
                ),
            )
        ), onEditTransactionIntent = {})
    }
}

@Preview
@Composable
fun AmountPrev() {
    PreviewTheme {
        AmountTextFieldCalculator(amountTfState = AmountTfState(),
            onTextFieldCalculatorIntent = {})
    }
}