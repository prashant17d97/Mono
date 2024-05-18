package com.debugdesk.mono.domain.data.local.localdatabase.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.debugdesk.mono.R
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.takeWord

typealias URI = String

@Entity(
    tableName = "dailyTransaction",
)
data class DailyTransaction(
    @PrimaryKey(autoGenerate = true)
    val transactionId: Int = 0,
    val date: Long,
    val type: String,
    val note: String,
    val category: String,
    val categoryIcon: Int,
    val categoryId: Int,
    val amount: Double,
    val images: List<String> = emptyList(),
    val currentMonthId: Int = 0,
    val year: Int? = null
) {
    val notes: String
        get() = if (note.isNotEmpty()) "$category (${note.takeWord(2)})" else category
}

val emptyTransaction: DailyTransaction
    get() = DailyTransaction(
        transactionId = 8014,
        date = 1715507975498,
        type = "Expense",
        note = "None",
        category = "Salary",
        categoryIcon = R.drawable.bank,
        amount = 65500.0,
        images = listOf(),
        currentMonthId = 4,
        categoryId = 4,
        year = 2024
    )
