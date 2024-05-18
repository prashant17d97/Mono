package com.debugdesk.mono.presentation.addcategory

sealed class AddCategoryIntent() {
    data class AddCategory(val categoryModel: AddCategoryState) : AddCategoryIntent()
    data object SaveCategory : AddCategoryIntent()
    data object NavigateBack : AddCategoryIntent()
}