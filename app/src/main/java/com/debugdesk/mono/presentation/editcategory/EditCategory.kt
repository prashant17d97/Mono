package com.debugdesk.mono.presentation.editcategory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.debugdesk.mono.R
import com.debugdesk.mono.domain.data.local.localdatabase.model.CategoryModel
import com.debugdesk.mono.navigation.Screens
import com.debugdesk.mono.presentation.uicomponents.CategoryCard
import com.debugdesk.mono.presentation.uicomponents.MonoColumn
import com.debugdesk.mono.presentation.uicomponents.SpacerHeight
import com.debugdesk.mono.utils.CommonColor.disableButton
import com.debugdesk.mono.utils.CommonColor.inActiveButton
import com.debugdesk.mono.utils.Dp.dp10
import com.debugdesk.mono.utils.Dp.dp2
import com.debugdesk.mono.utils.Dp.dp5
import com.debugdesk.mono.utils.Dp.dp70
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.filterExpenseType
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.filterIncomeType
import com.debugdesk.mono.utils.enums.ExpenseType
import org.koin.androidx.compose.koinViewModel

@Composable
fun EditCategory(
    navHostController: NavHostController,
    viewModel: EditCategoryVM = koinViewModel(),
) {
    val categoryModels by viewModel.categoryModelList.collectAsState()
    val context = LocalContext.current
    var expenseCategory: List<CategoryModel> by rememberSaveable {
        mutableStateOf(
            emptyList(),
        )
    }
    var incomeCategory: List<CategoryModel> by rememberSaveable {
        mutableStateOf(
            emptyList(),
        )
    }
    LaunchedEffect(categoryModels) {
        expenseCategory = categoryModels.filter { it.categoryType?.filterExpenseType ?: false }
        incomeCategory = categoryModels.filter { it.categoryType?.filterIncomeType ?: false }
    }

    LaunchedEffect(Unit) {
        viewModel.init()
    }
    MonoColumn(
        verticalArrangement = Arrangement.Top,
        isScrollEnabled = false,
        heading = stringResource(id = R.string.editCategory),
        trailing = stringResource(id = R.string.remove),
        trailingColor = disableButton,
        showBack = true,
        onBackClick = { navHostController.popBackStack() },
        onTrailClick = { viewModel.removeCategory(incomeCategory, expenseCategory) },
    ) {
        Text(
            text = stringResource(id = R.string.expense),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = dp2, vertical = dp5),
        )

        LazyVerticalGrid(columns = GridCells.Adaptive(dp70)) {
            items(
                expenseCategory +
                    CategoryModel(
                        category = context.getString(R.string.addMore),
                        isSelected = false,
                        categoryType = ExpenseType.Neutral.name,
                    ),
            ) { model ->
                CategoryCard(
                    model = model,
                    selectedColor = inActiveButton,
                ) {
                    if (model.categoryType == ExpenseType.Neutral.name) {
                        navHostController.navigate(
                            Screens.AddCategory.passAddCategoryArgs(
                                ExpenseType.Expense.name,
                            ),
                        )
                    } else {
                        expenseCategory = viewModel.updateExpenseCategory(expenseCategory, model)
                    }
                }
            }
        }

        SpacerHeight(value = dp10)
        Text(
            text = stringResource(id = R.string.income),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 2.dp, vertical = 5.dp),
        )
        LazyVerticalGrid(columns = GridCells.Adaptive(dp70)) {
            items(
                incomeCategory +
                    CategoryModel(
                        category = context.getString(R.string.addMore),
                        isSelected = false,
                        categoryType = ExpenseType.Neutral.name,
                    ),
            ) { model ->
                CategoryCard(
                    model = model,
                    selectedColor = inActiveButton,
                ) {
                    if (model.categoryType == ExpenseType.Neutral.name) {
                        navHostController.navigate(
                            Screens.AddCategory.passAddCategoryArgs(
                                ExpenseType.Income.name,
                            ),
                        )
                    } else {
                        incomeCategory = viewModel.updateIncomeCategory(incomeCategory, model)
                    }
                }
            }
        }
    }
}
