package com.debugdesk.mono.utils.enums

enum class ExpenseType {
    Expense,
    Income,
    Neutral
}

val expenseType = { index: Int -> ExpenseType.Expense.takeIf { index == 0 } ?: ExpenseType.Income }
