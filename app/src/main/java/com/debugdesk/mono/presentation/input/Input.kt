package com.debugdesk.mono.presentation.input

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.debugdesk.mono.R
import com.debugdesk.mono.domain.data.local.localdatabase.model.CategoryModel
import com.debugdesk.mono.presentation.edittrans.TransactionIntent
import com.debugdesk.mono.presentation.imgpreview.ImagePreview
import com.debugdesk.mono.presentation.imgpreview.PreviewIntent
import com.debugdesk.mono.presentation.input.state.InputState
import com.debugdesk.mono.presentation.uicomponents.CalendarCard
import com.debugdesk.mono.presentation.uicomponents.CustomButton
import com.debugdesk.mono.presentation.uicomponents.CustomTabs
import com.debugdesk.mono.presentation.uicomponents.MonoColumn
import com.debugdesk.mono.presentation.uicomponents.PreviewTheme
import com.debugdesk.mono.presentation.uicomponents.amounttf.AmountTextFieldCalculator
import com.debugdesk.mono.presentation.uicomponents.editcategory.EditCategoryCard
import com.debugdesk.mono.presentation.uicomponents.media.MediaBottomSheet
import com.debugdesk.mono.presentation.uicomponents.notetf.NoteTextField
import com.debugdesk.mono.utils.CommonColor
import com.debugdesk.mono.utils.Dp.dp10
import com.debugdesk.mono.utils.Tabs.tabs
import com.debugdesk.mono.utils.enums.Buttons
import com.debugdesk.mono.utils.enums.ExpenseType
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
    val context = LocalContext.current
    var showPreview by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.closeAllDialogs(expenseType(pagerState.currentPage))
        viewModel.fetchInitialData()
    }

    InputContainer(
        modifier = Modifier,
        expenseState = expenseState,
        incomeState = incomeState,
        state = pagerState,
        tabs = tabs,
        onTabClick = {
            scope.launch {
                pagerState.animateScrollToPage(it)
                viewModel.closeAllDialogs(expenseType(pagerState.currentPage))
            }
        },
        onImageClick = {
            showPreview = true
        },
        onInputIntent = { inputIntent ->
            viewModel.handleTransactionIntent(
                inputIntent = inputIntent,
                transactionType = expenseType(pagerState.currentPage),
                navHostController = navHostController,
                context = context
            )
        }
    )

    val state by rememberUpdatedState(
        newValue = if (pagerState.currentPage == 0) {
            expenseState
        } else {
            incomeState
        }
    )

    ImagePreview(
        showPreview = showPreview,
        createdOn = state.createdOn,
        painter = state.transaction.painter,
        size = state.transaction.imageSize
    ) { previewIntent ->
        when (previewIntent) {
            PreviewIntent.Delete -> {
                viewModel.handleTransactionIntent(
                    inputIntent = TransactionIntent.DeleteImage,
                    transactionType = expenseType(pagerState.currentPage),
                    navHostController = navHostController,
                    context = context
                )
                showPreview = false
            }

            PreviewIntent.Navigate -> {
                showPreview = false
            }

        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InputContainer(
    modifier: Modifier = Modifier,
    expenseState: InputState,
    incomeState: InputState,
    state: PagerState,
    tabs: List<Int>,
    onTabClick: (Int) -> Unit,
    onImageClick: () -> Unit = {},
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
                when (pageIndex) {
                    0 -> InputPage(
                        categories = expenseState.categoryList,
                        inputState = expenseState,
                        text = R.string.expense,
                        onSave = {
                            onInputIntent(
                                TransactionIntent.OnNewTransactionSaveClick(
                                    ExpenseType.Expense
                                )
                            )
                        },
                        onImageClick = onImageClick,
                        onInputIntent = onInputIntent
                    )

                    1 -> InputPage(
                        categories = incomeState.categoryList,
                        inputState = incomeState,
                        text = R.string.income,
                        onSave = {
                            onInputIntent(
                                TransactionIntent.OnNewTransactionSaveClick(
                                    ExpenseType.Income
                                )
                            )
                        },
                        onImageClick = onImageClick,
                        onInputIntent = onInputIntent
                    )

                }
            }
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
    onImageClick: () -> Unit = {},
    onInputIntent: (TransactionIntent) -> Unit
) {

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        MonoColumn(
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

            CalendarCard(
                date = inputState.date,
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
                note = inputState.note,
                textOutlineEnabled = inputState.image.isNotEmpty(),
                image = inputState.transaction.painter,
                onNoteChange = {
                    onInputIntent(TransactionIntent.OnValueChange(it))
                },
                onImageClick = onImageClick,
                onDelete = {
                    onInputIntent(TransactionIntent.DeleteImage)
                },
                onTrailClick = {
                    onInputIntent(TransactionIntent.OnTrailIconClick)
                }

            )

            EditCategoryCard(
                list = categories,
                onCategoryEdit = onInputIntent
            )
        }

        CustomButton(modifier = Modifier
            .fillMaxWidth()
            .padding(dp10),
            status = Buttons.Active.takeIf { inputState.changesFound } ?: Buttons.Disable,
            text = stringResource(id = R.string.save),
            onClick = { onSave() })
    }

    MediaBottomSheet(
        visible = inputState.showCameraAndGallery,
        appStateManager = inputState.appStateManager,
        onProcess = onInputIntent
    )
}


@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
private fun InputPagePrev() {
    PreviewTheme {
        InputContainer(modifier = Modifier,
            expenseState = InputState(),
            incomeState = InputState(),
            state = rememberPagerState {
                2
            },
            tabs = tabs,
            onTabClick = { },
            onInputIntent = {}

        )
    }
}