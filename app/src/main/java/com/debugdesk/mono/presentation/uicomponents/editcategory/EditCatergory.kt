package com.debugdesk.mono.presentation.uicomponents.editcategory

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.debugdesk.mono.R.drawable
import com.debugdesk.mono.R.string
import com.debugdesk.mono.domain.data.local.localdatabase.model.CategoryModel
import com.debugdesk.mono.presentation.edittrans.TransactionIntent
import com.debugdesk.mono.presentation.uicomponents.CategoryCard
import com.debugdesk.mono.presentation.uicomponents.PreviewTheme
import com.debugdesk.mono.utils.Dp
import com.debugdesk.mono.utils.Dp.dp0

@Composable
fun EditCategoryCard(
    list: List<CategoryModel> = emptyList(),
    onCategoryEdit: (TransactionIntent) -> Unit,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = string.category),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
            )
            Text(text = stringResource(id = string.editCategory),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(top = 20.dp, bottom = 10.dp)
                    .clickable {
                        onCategoryEdit(TransactionIntent.UpdateCategoryIntent(EditCategoryIntent.OnEditCategoryClicked))
                    })
        }

        LazyVerticalGrid(columns = GridCells.Adaptive(Dp.dp80),
            contentPadding = PaddingValues(dp0),
            content = {
                itemsIndexed(list) { index, item ->
                    CategoryCard(model = item) {
                        onCategoryEdit(
                            TransactionIntent.UpdateCategoryIntent(
                                EditCategoryIntent.OnCategoryListChange(
                                    list.mapIndexed { j, item ->
                                        item.copy(isSelected = index == j)
                                    })
                            )

                        )
                    }
                }
            }
        )
    }
}

@Preview
@Composable
fun EditCatPrev() {
    PreviewTheme {
        EditCategoryCard(
            list = listOf(
                CategoryModel(
                    categoryId = 1,
                    category = "Food",
                    categoryIcon = drawable.food,
                    categoryType = "Expense",
                    isSelected = false
                ),
                CategoryModel(
                    categoryId = 2,
                    category = "Salary",
                    categoryIcon = drawable.baby,
                    categoryType = "Income",
                    isSelected = false
                ),
                CategoryModel(
                    categoryId = 3,
                    category = "Transport",
                    categoryIcon = drawable.train,
                    categoryType = "Expense",
                    isSelected = false
                ),
                CategoryModel(
                    categoryId = 4,
                    category = "Shopping",
                    categoryIcon = drawable.coffee,
                    categoryType = "Expense",
                    isSelected = false
                ),
                CategoryModel(
                    categoryId = 5,
                    category = "Rent",
                    categoryIcon = drawable.baby,
                    categoryType = "Expense",
                    isSelected = false
                ),
                CategoryModel(
                    categoryId = 6,
                    category = "Others",
                    categoryIcon = drawable.books,
                    categoryType = "Expense",
                    isSelected = false
                ),
            )
        ) {}
    }
}