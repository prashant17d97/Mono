package com.debugdesk.mono.domain.data.local.localdatabase.model

import com.debugdesk.mono.R
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.takeWord
import com.debugdesk.mono.utils.enums.ExpenseType
import java.util.Date


data class DailyTransaction(
    val transactionId: Int = 0,
    val transactionUniqueId: String = "",
    val date: Long = Date().time,
    val type: String,
    val note: String,
    val category: String,
    val categoryIcon: Int,
    val categoryId: Int,
    val amount: Double,
    val transactionImage: List<TransactionImage> = emptyList(),
    val currentMonthId: Int = 0,
    val year: Int? = null
) {

    val notes: String
        get() = if (note.isNotEmpty()) "$category (${note.takeWord(2)})" else category

    val absolutePaths: List<String> get() = transactionImage.map { it.absolutePath }
}

val emptyTransaction: DailyTransaction
    get() = DailyTransaction(
        type = "",
        note = "",
        category = "",
        categoryIcon = 0,
        categoryId = 0,
        amount = 0.0,
    )
val previewTransaction: DailyTransaction
    get() = DailyTransaction(
        type = ExpenseType.Expense.name,
        note = "Biryani",
        category = "Food",
        categoryIcon = R.drawable.food,
        categoryId = 0,
        amount = 500.0,
        currentMonthId = 5,
        year = 2024
    )

val previewIncomeTransaction: DailyTransaction
    get() = DailyTransaction(
        type = ExpenseType.Income.name,
        note = "OpenBet",
        category = "Salary",
        categoryIcon = R.drawable.bank,
        categoryId = 0,
        amount = 500.0,
        currentMonthId = 5,
        year = 2024
    )

val listOfPreviewTransaction =listOf(
    previewTransaction,
    previewTransaction,
    previewTransaction,
    previewTransaction,
    previewTransaction,
    previewIncomeTransaction,
    previewIncomeTransaction,
    previewIncomeTransaction,
    previewIncomeTransaction
)
