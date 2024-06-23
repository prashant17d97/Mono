package com.debugdesk.mono.presentation.addcategory

import android.util.Log
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.debugdesk.mono.R
import com.debugdesk.mono.domain.data.local.localdatabase.model.CategoryModel
import com.debugdesk.mono.presentation.uicomponents.CategoryCard
import com.debugdesk.mono.presentation.uicomponents.CustomOutlineTextField
import com.debugdesk.mono.presentation.uicomponents.PreviewTheme
import com.debugdesk.mono.presentation.uicomponents.MonoColumn
import com.debugdesk.mono.utils.Dp.dp2
import com.debugdesk.mono.utils.Dp.dp5
import com.debugdesk.mono.utils.Dp.dp90
import com.debugdesk.mono.utils.Icons.categoryIcons
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddCategory(
    navController: NavHostController,
    argument: String,
    viewModel: AddCategoryVM = koinViewModel()
) {
    val categoryState by viewModel.categoryModel.collectAsState()
    val keyboardFocusManager = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(categoryState.clearKeyboardFocus) {
        Log.e("TAG", "AddCategory: $argument")
        if (categoryState.clearKeyboardFocus) {
            keyboardFocusManager?.hide()
            focusManager.clearFocus()

        }
    }

    AddCategoryContainer(
        categoryState = categoryState
    ) {
        viewModel.onIntentChange(
            intent = it,
            navHostController = navController,
            argument = argument
        )
    }

}


@Composable
fun AddCategoryContainer(
    categoryState: AddCategoryState,
    onIntentChange: (AddCategoryIntent) -> Unit
) {
    MonoColumn(
        verticalArrangement = Arrangement.Top,
        isScrollEnabled = false,
        heading = stringResource(id = R.string.addCategory),
        showBack = true,
        trailing = stringResource(id = R.string.add),
        onBackClick = {
            onIntentChange(
                AddCategoryIntent.NavigateBack
            )
        },
        onTrailClick = {
            onIntentChange(
                AddCategoryIntent.SaveCategory
            )
        },
    ) {
        CustomOutlineTextField(
            placeHolderText = stringResource(id = R.string.please_input),
            value = categoryState.category,
            imeAction = ImeAction.Done,
            charLimit = 50,
            keyboardType = KeyboardType.Ascii,
            onValueChange = {
                onIntentChange(
                    AddCategoryIntent.AddCategory(
                        categoryState.copy(
                            category = it
                        )
                    )
                )
            },
            modifier = Modifier.padding(horizontal = dp2, vertical = dp5)
        )
        Text(
            text = stringResource(id = R.string.icon),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = dp2, vertical = dp5)
        )
        LazyVerticalGrid(
            columns = GridCells.Adaptive(dp90),
        ) {
            items(categoryIcons) { (itemIcon, itemName) ->
                CategoryCard(
                    model = CategoryModel(
                        categoryIcon = itemIcon,
                        category = stringResource(id = itemName),
                        isSelected = itemIcon == categoryState.categoryIcon
                    )
                ) {
                    onIntentChange(
                        AddCategoryIntent.AddCategory(
                            categoryState.copy(
                                categoryIcon = it.categoryIcon,
                                category = it.category
                            )
                        )
                    )
                }
            }

        }
    }
}


@Preview
@Composable
fun AddCategoryPreview() {

    PreviewTheme {
        AddCategoryContainer(
            AddCategoryState(
                category = "",
                categoryIcon = R.drawable.coffee
            )
        ) {

        }
    }
}
