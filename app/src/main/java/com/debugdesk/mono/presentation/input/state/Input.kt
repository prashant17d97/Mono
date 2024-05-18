package com.debugdesk.mono.presentation.input.state

import android.app.Activity
import android.content.ContentResolver
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.debugdesk.mono.R
import com.debugdesk.mono.domain.data.local.localdatabase.model.CategoryModel
import com.debugdesk.mono.domain.data.local.localdatabase.model.DailyTransaction
import com.debugdesk.mono.navigation.Screens
import com.debugdesk.mono.presentation.input.InputVM
import com.debugdesk.mono.presentation.input.PageState
import com.debugdesk.mono.presentation.input.pageStateSaver
import com.debugdesk.mono.presentation.uicomponents.Calculator
import com.debugdesk.mono.presentation.uicomponents.CategoryCard
import com.debugdesk.mono.presentation.uicomponents.CustomButton
import com.debugdesk.mono.presentation.uicomponents.CustomOutlineTextField
import com.debugdesk.mono.presentation.uicomponents.CustomTabs
import com.debugdesk.mono.presentation.uicomponents.DatePickerDialog
import com.debugdesk.mono.presentation.uicomponents.PopUp
import com.debugdesk.mono.presentation.uicomponents.ScreenView
import com.debugdesk.mono.presentation.uicomponents.SpacerHeight
import com.debugdesk.mono.ui.appconfig.defaultconfig.AppConfigProperties
import com.debugdesk.mono.utils.CalculatorEnum
import com.debugdesk.mono.utils.CommonColor.disableButton
import com.debugdesk.mono.utils.Dp.dp0
import com.debugdesk.mono.utils.Dp.dp10
import com.debugdesk.mono.utils.Dp.dp30
import com.debugdesk.mono.utils.Dp.dp40
import com.debugdesk.mono.utils.Dp.dp80
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.filterExpenseType
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.filterIncomeType
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.getCurrencyIcon
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.getMonthAndYearFromLong
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.toDate
import com.debugdesk.mono.utils.enums.Buttons
import com.debugdesk.mono.utils.enums.expenseType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import kotlin.math.absoluteValue

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class
)
@Composable
fun Input(
    navHostController: NavHostController, viewModel: InputVM = koinViewModel()
) {
    val context = LocalContext.current
    val activity = (context as? Activity)
    LaunchedEffect(Unit) {
        viewModel.init()
    }
    val coroutineScope = rememberCoroutineScope()
    val categoryModels by viewModel.categoryModels.collectAsState()

    var inputState by rememberSaveable(stateSaver = InputStatesSaver) {
        mutableStateOf(InputStates())
    }

    val state = rememberPagerState {
        inputState.tabs.size
    }
    var expensesCategory: List<CategoryModel> by rememberSaveable {
        mutableStateOf(
            emptyList()
        )
    }
    var incomeCategory: List<CategoryModel> by rememberSaveable {
        mutableStateOf(
            emptyList()
        )
    }

    LaunchedEffect(categoryModels) {
        expensesCategory = categoryModels.filter { it.categoryType?.filterExpenseType ?: false }
        incomeCategory = categoryModels.filter { it.categoryType?.filterIncomeType ?: false }
    }
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = inputState.skipPartiallyExpanded
    )


    val appConfigProperties by viewModel.appConfigProperties.collectAsState()

    BackHandler {
        activity?.finishAffinity()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CustomTabs(selectedIndex = state.currentPage, list = inputState.tabs, onClick = { index ->
            coroutineScope.launch {
                state.animateScrollToPage(index)
            }
        })
        HorizontalPager(
            state = state,
        ) { pageIndex ->
            var showDialog by remember {
                mutableStateOf(false)
            }
            var date by remember {
                mutableLongStateOf(System.currentTimeMillis())
            }

            DatePickerDialog(
                openDialog = showDialog,
                openDialogChange = { showDialog = it }
            ) {
                date = it
            }


            // Add the layout

            Page(list = expensesCategory.takeIf { pageIndex == 0 } ?: incomeCategory,
                appConfigProperties = appConfigProperties,
                selectedIndex = pageIndex,
                amountValue = inputState.amount(pageIndex),
                onAmountChange = { amount ->
                    inputState = if (pageIndex == 0) inputState.copy(expense = amount)
                    else inputState.copy(income = amount)
                },
                note = inputState.note(pageIndex),
                onNoteChange = { note ->
                    inputState = if (pageIndex == 0) inputState.copy(expensesNote = note)
                    else inputState.copy(incomeNote = note)

                },
                onListChange = { list ->
                    if (pageIndex == 0) {
                        expensesCategory = list
                    } else {
                        incomeCategory = list
                    }
                },
                navHostController = navHostController,
                onCameraClick = {
                    Log.e("TAG", "Input: cameraClick$it")
                    inputState = inputState.copy(
                        openBottomSheet = !inputState.openBottomSheet,
                        skipPartiallyExpanded = !inputState.skipPartiallyExpanded
                    )
                },
                submitClick = { tabIndex, catIndex ->
                    val category = expensesCategory[catIndex].takeIf { tabIndex == 0 }
                        ?: incomeCategory[catIndex]

                    val (month, year) = getMonthAndYearFromLong(date)
                    viewModel.insertTransaction(
                        transaction = DailyTransaction(
                            date = date,
                            type = expenseType(tabIndex).name,
                            note = inputState.note(pageIndex),
                            category = category.category,
                            categoryIcon = category.categoryIcon ?: R.drawable.logo,
                            amount = inputState.amountDouble(tabIndex),
                            images = emptyList(),
                            currentMonthId = month,
                            categoryId = category.categoryId,
                            year = year,
                        )
                    )

                    if (tabIndex == 0) {
                        inputState = InputStates()
                        expensesCategory =
                            expensesCategory.mapIndexed { expenseIndex, categoriesModel ->
                                categoriesModel.copy(isSelected = true.takeIf { 0 == expenseIndex }
                                    ?: false)
                            }
                    } else {
                        inputState = InputStates()
                        incomeCategory = incomeCategory.mapIndexed { incomeIndex, categoriesModel ->
                            categoriesModel.copy(isSelected = true.takeIf { 0 == incomeIndex }
                                ?: false)
                        }
                    }

                },
                date = date,
                onDateChange = { showDialog = !showDialog },
                state = state
            )


        }
    }

    AnimatedVisibility(visible = inputState.openBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { inputState = inputState.copy(openBottomSheet = false) },
            sheetState = bottomSheetState,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable {
                            scope
                                .launch { bottomSheetState.hide() }
                                .invokeOnCompletion {
                                    if (!bottomSheetState.isVisible) {
                                        inputState = inputState.copy(openBottomSheet = false)
                                    }
                                }
                        }
                        .padding(10.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_camera),
                        contentDescription = stringResource(id = R.string.camera),
                        colorFilter = ColorFilter.tint(
                            MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.size(60.dp)
                    )

                    SpacerHeight(value = dp10)
                    Text(
                        text = stringResource(id = R.string.camera),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Column(verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable {
                            scope
                                .launch { bottomSheetState.hide() }
                                .invokeOnCompletion {
                                    if (!bottomSheetState.isVisible) {
                                        inputState = inputState.copy(openBottomSheet = false)
                                    }
                                }
                        }
                        .padding(10.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_gallary),
                        contentDescription = stringResource(id = R.string.gallery),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                        modifier = Modifier.size(60.dp)
                    )
                    SpacerHeight(value = dp10)
                    Text(
                        text = stringResource(id = R.string.gallery),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Page(
    list: List<CategoryModel>,
    appConfigProperties: AppConfigProperties,
    selectedIndex: Int,
    amountValue: String,
    onAmountChange: (String) -> Unit,
    note: String,
    onNoteChange: (String) -> Unit,
    onListChange: (List<CategoryModel>) -> Unit,
    navHostController: NavHostController,
    onCameraClick: (Int) -> Unit,
    submitClick: (Int, Int) -> Unit,
    date: Long = System.currentTimeMillis(),
    onDateChange: () -> Unit,
    state: PagerState
) {
    val density = LocalDensity.current
    var pageState by rememberSaveable(
        stateSaver = pageStateSaver,
    ) { mutableStateOf(PageState()) }


    val screenHeight = (LocalConfiguration.current).screenHeightDp
    val screenWidth = (LocalConfiguration.current).screenWidthDp
    ConstraintLayout(modifier = Modifier
        .fillMaxWidth()
        .graphicsLayer {
            // Calculate the absolute offset for the current page from the
            // scroll position. We use the absolute value which allows us to mirror
            // any effects for both directions
            val pageOffset =
                ((state.currentPage + state.currentPageOffsetFraction) - selectedIndex).absoluteValue

            // We animate the scaleX + scaleY, between 85% and 100%
            lerp(
                start = 1f, stop = 1f, fraction = 1f - pageOffset.coerceIn(0f, 1f)
            ).also { scale ->
                scaleX = scale
                scaleY = scale
            }

            // We animate the alpha, between 50% and 100%
            alpha = lerp(
                start = 1f, stop = 1f, fraction = 1f - pageOffset.coerceIn(0f, 1f)
            )
        }) {
        val (mainScreen, button) = createRefs()
        ScreenView(top = dp10,
            start = 0.dp,
            end = 0.dp,
            bottom = dp40,
            verticalArrangement = Arrangement.Top,
            isScrollEnabled = false,
            modifier = Modifier
                .fillMaxWidth()/*width(screenWidth.dp)*/
                .height(screenHeight.dp)
                .constrainAs(mainScreen) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(button.bottom)
                }) {
            Box(contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDateChange() }
                    .padding(
                        start = dp10,
                        end = dp10,
                    )
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer, shape = CircleShape
                    )) {
                Text(
                    text = date.toDate(), modifier = Modifier.padding(10.dp)
                )
            }

            Text(text = stringResource(id = R.string.expense).takeIf {
                selectedIndex == 0
            } ?: stringResource(id = R.string.income),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(10.dp))


            CustomOutlineTextField(leadingIcon = appConfigProperties.selectedCurrencyCode.getCurrencyIcon(),
                placeHolderText = stringResource(id = R.string.zero),
                textStyle = MaterialTheme.typography.headlineLarge,
                start = 10.dp,
                end = 10.dp,
                height = with(density) {
                    pageState.height.toDp()
                },
                onHeightChange = {
                    pageState = pageState.copy(height = it)
                },
                imeAction = ImeAction.Next,
                value = amountValue,
                onValueChange = {
                    onAmountChange(it)
                },
                enabled = false,
                fieldClickBack = { pageState = pageState.copy(openDialog = !pageState.openDialog) })
            if (pageState.openDialog) {
                PopUp(dismiss = {
                    pageState = pageState.copy(openDialog = !pageState.openDialog)
                }) {
                    Calculator(dotCount = 1.takeIf { amountValue.contains(".") } ?: 0,
                        priorValue = amountValue,
                        onValueReturn = { operationType, value ->
                            onAmountChange(value)
                            pageState = pageState.copy(
                                openDialog = when (operationType) {
                                    CalculatorEnum.Cancel, CalculatorEnum.Okay -> false
                                }
                            )
                        })
                }
            }

            Text(
                text = stringResource(id = R.string.note),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(10.dp)
            )

            Column(verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .border(width = 1.dp,
                        color = MaterialTheme.colorScheme.primary.takeIf { pageState.hasFocus }
                            ?: disableButton,
                        shape = RoundedCornerShape(10.dp))) {
                CustomOutlineTextField(hasFocus = { pageState = pageState.copy(hasFocus = it) },
                    trailingIcon = R.drawable.ic_camera,
                    placeHolderText = stringResource(id = R.string.input),
                    textStyle = MaterialTheme.typography.bodyMedium,
                    charLimit = 100,
                    keyboardType = KeyboardType.Text,
                    singleLine = false,
                    imeAction = ImeAction.Done,
                    height = with(density) {
                        pageState.inputHeight.toDp()
                    },
                    onHeightChange = {
                        pageState = pageState.copy(inputHeight = it)
                    },
                    value = note,
                    onValueChange = {
                        onNoteChange(it)
                    },
                    trailingClick = {
                        onCameraClick(selectedIndex)
                    })
                AnimatedVisibility(visible = pageState.hasFocus) {
                    Image(
                        modifier = Modifier.size(250.dp),
                        painter = painterResource(id = R.drawable.intro_img_one),
                        contentDescription = ""
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()/*width(screenWidth.dp)*/
                    .padding(
                        start = 10.dp,
                        end = 10.dp,
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.category),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
                )
                Text(text = stringResource(id = R.string.editCategory),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(top = 20.dp, bottom = 10.dp)
                        .clickable {
                            navHostController.navigate(Screens.EditCategory.route)
                        })
            }

            LazyVerticalGrid(columns = GridCells.Adaptive(dp80),
                contentPadding = PaddingValues(top = dp0, bottom = dp30, start = dp10, end = dp10),
                content = {
                    itemsIndexed(list) { index, item ->
                        CategoryCard(model = item) {
                            onListChange(list.mapIndexed { j, item ->
                                if (index == j) {
                                    pageState = pageState.copy(catIndex = j)
                                }
                                item.copy(isSelected = index == j)
                            })
                        }
                    }
                })
        }

        Box(
            Modifier
                .fillMaxWidth()/*width(screenWidth.dp)*/
                .background(color = MaterialTheme.colorScheme.background)
                .constrainAs(button) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }) {
            CustomButton(modifier = Modifier
                .fillMaxWidth()/*width(screenWidth.dp)*/
                .padding(10.dp),
                status = Buttons.Disable.takeIf { amountValue.isEmpty() } ?: Buttons.Active,
                text = stringResource(id = R.string.submit),
                onClick = {
                    submitClick(selectedIndex, pageState.catIndex)
                })
        }
    }
}


@Preview
@Composable
fun InputPreview() = Input(rememberNavController())


suspend fun uriToImageBitmap(contentResolver: ContentResolver, uri: Uri): ImageBitmap {
    return withContext(Dispatchers.IO) {
        val inputStream = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        bitmap.asImageBitmap()
    }
}
