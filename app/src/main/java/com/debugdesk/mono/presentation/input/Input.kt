package com.debugdesk.mono.presentation.input

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.debugdesk.mono.R
import com.debugdesk.mono.domain.data.local.localdatabase.model.CategoryModel
import com.debugdesk.mono.presentation.edittrans.TransactionIntent
import com.debugdesk.mono.presentation.input.state.InputState
import com.debugdesk.mono.presentation.uicomponents.CalendarCard
import com.debugdesk.mono.presentation.uicomponents.CustomButton
import com.debugdesk.mono.presentation.uicomponents.CustomTabs
import com.debugdesk.mono.presentation.uicomponents.ImageGallery
import com.debugdesk.mono.presentation.uicomponents.PreviewTheme
import com.debugdesk.mono.presentation.uicomponents.ScreenView
import com.debugdesk.mono.presentation.uicomponents.amounttf.AmountTextFieldCalculator
import com.debugdesk.mono.presentation.uicomponents.editcategory.EditCategoryCard
import com.debugdesk.mono.presentation.uicomponents.media.CameraAndGallery
import com.debugdesk.mono.presentation.uicomponents.notetf.NoteTextField
import com.debugdesk.mono.utils.CommonColor
import com.debugdesk.mono.utils.Dp.dp10
import com.debugdesk.mono.utils.Tabs.tabs
import com.debugdesk.mono.utils.enums.Buttons
import com.debugdesk.mono.utils.enums.expenseType
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Input(
    navHostController: NavHostController, viewModel: InputVM = koinViewModel()
) {
    val scope = rememberCoroutineScope()
    val incomeState by viewModel.incomeState.collectAsState()
    val expenseState by viewModel.expenseState.collectAsState()
    val pagerState = rememberPagerState {
        tabs.size
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.closeAllDialogs(expenseType(pagerState.currentPage))
        viewModel.fetchInitialData()
    }

    InputContainer(modifier = Modifier,
        inputState = rememberUpdatedState(newValue = expenseState.takeIf { pagerState.currentPage == 0 }
            ?: incomeState).value,
        state = pagerState,
        tabs = tabs,
        onTabClick = {
            scope.launch {
                pagerState.animateScrollToPage(it)
                viewModel.closeAllDialogs(expenseType(pagerState.currentPage))
            }
        },
        onInputIntent = { inputIntent ->
            viewModel.handleTransactionIntent(
                inputIntent = inputIntent,
                transactionType = expenseType(pagerState.currentPage),
                navHostController = navHostController
            )
        })

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InputContainer(
    modifier: Modifier = Modifier,
    inputState: InputState,
    state: PagerState,
    tabs: List<Int>,
    onTabClick: (Int) -> Unit,
    onInputIntent: (TransactionIntent) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomTabs(selectedIndex = state.currentPage, list = tabs, onClick = onTabClick)

            HorizontalPager(
                state = state,
            ) { pageIndex ->
                InputPage(
                    categories = inputState.inputCategories(pageIndex),
                    inputState = inputState,
                    text = R.string.expense.takeIf { pageIndex == 0 } ?: R.string.income,
                    onSave = {
                        onInputIntent(
                            TransactionIntent.OnNewTransactionSaveClick(
                                expenseType(pageIndex)
                            )
                        )
                    },
                    onInputIntent = onInputIntent
                )

            }
        }

        AnimatedVisibility(visible = inputState.showCameraAndGallery,
            enter = slideInVertically { it },
            exit = slideOutVertically { it }) {
            CameraAndGallery(
                appStateManager = inputState.appStateManager,
                images = inputState.transaction.transactionImage,
                onProcess = onInputIntent
            )
        }


        AnimatedVisibility(
            visible = inputState.showImageGallery, enter = scaleIn(
                initialScale = 0.5f, animationSpec = tween(durationMillis = 500)
            ), exit = scaleOut(
                targetScale = 0.5f, animationSpec = tween(durationMillis = 500)
            )
        ) {
            ImageGallery(images = inputState.transaction.transactionImage,
                clickedIndex = inputState.clickedIndex,
                onDelete = { transactionImage ->
                    onInputIntent(TransactionIntent.DeleteImage(inputState.noteState.transactionImages.filter { it != transactionImage }))
                },
                close = { onInputIntent(TransactionIntent.CloseImageGallery) })
        }
    }
}

@Composable
private fun InputPage(
    modifier: Modifier = Modifier,
    categories: List<CategoryModel> = emptyList(),
    inputState: InputState,
    text: Int = R.string.expense,
    onSave: () -> Unit = {},
    onInputIntent: (TransactionIntent) -> Unit
) {

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        ScreenView(
            isScrollEnabled = false,
            onTrailClick = {
                onInputIntent(TransactionIntent.OnDeleteClick)
            },
            onBackClick = {
                onInputIntent(TransactionIntent.OnBackClick)
            },
            trailingColor = CommonColor.inActiveButton,
            verticalArrangement = Arrangement.spacedBy(dp10, alignment = Alignment.Top),
        ) {

            CalendarCard(date = inputState.transaction.date,
                showDialog = inputState.showCalendarDialog,
                onShowCalendarDialog = {
                    onInputIntent(
                        TransactionIntent.OpenCalendarDialog(
                            it
                        )
                    )
                },
                onDateChange = { onInputIntent(TransactionIntent.UpdateDate(it)) })
            Text(
                text = stringResource(id = text),
            )
            AmountTextFieldCalculator(amountTfState = inputState.amountTfState,
                onTextFieldCalculatorIntent = {
                    onInputIntent(
                        TransactionIntent.UpdateAmount(
                            it
                        )
                    )
                })

            NoteTextField(
                appStateManager = inputState.appStateManager,
                noteState = inputState.noteState,
                onNoteChange = onInputIntent
            )

            EditCategoryCard(
                list = categories, onCategoryEdit = onInputIntent
            )
        }

        CustomButton(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
            status = Buttons.Active.takeIf { inputState.changesFound } ?: Buttons.Disable,
            text = stringResource(id = R.string.save),
            onClick = { onSave() })
    }

}


@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
private fun InputPagePrev() {
    PreviewTheme {
        InputContainer(modifier = Modifier,
            inputState = InputState(),
            state = rememberPagerState {
                2
            },
            tabs = tabs,
            onTabClick = { },
            onInputIntent = {}

        )
    }
}