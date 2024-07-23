package com.debugdesk.mono.presentation.addcategory

data class AddCategoryState(
    val category: String = "",
    val categoryIcon: Int? = null,
    val categoryType: String? = null,
    val clearKeyboardFocus: Boolean = false,
    val error: String = "",
)
