package com.debugdesk.mono.utils.enums

enum class ExpenseType {
    Expense,
    Income,
    Neutral
}

enum class ImageFrom {
    CAMERA,
    GALLERY,
}

val expenseType = { index: Int -> ExpenseType.Expense.takeIf { index == 0 } ?: ExpenseType.Income }
