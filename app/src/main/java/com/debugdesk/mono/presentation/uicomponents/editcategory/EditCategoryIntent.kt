package com.debugdesk.mono.presentation.uicomponents.editcategory

import com.debugdesk.mono.domain.data.local.localdatabase.model.CategoryModel

sealed class EditCategoryIntent {

    data object OnEditCategoryClicked : EditCategoryIntent()
    data class OnCategoryListChange(val list: List<CategoryModel>) : EditCategoryIntent()
}