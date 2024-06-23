package com.debugdesk.mono.utils.enums

enum class ExpenseType {
    Expense,
    Income,
    Neutral
}

enum class ImageSource {
    CAMERA,
    GALLERY,
    NONE
}

val expenseType = { index: Int -> ExpenseType.Expense.takeIf { index == 0 } ?: ExpenseType.Income }
