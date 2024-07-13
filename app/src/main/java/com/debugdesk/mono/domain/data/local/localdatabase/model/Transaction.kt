package com.debugdesk.mono.domain.data.local.localdatabase.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactionEntry",
)
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val transactionId: Int = 0,
    val date: Long,
    val type: String,
    val note: String,
    val category: String,
    val categoryIcon: Int,
    val categoryId: Int,
    val amount: Double,
    val currentMonthId: Int = 0,
    val imagePath: String,
    val imageSource: String,
    val createdOn: Long,
    val year: Int? = null
)
