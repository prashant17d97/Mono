package com.debugdesk.mono.domain.data.local.localdatabase.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "transaction_images",
    foreignKeys = [ForeignKey(
        entity = DailyTransaction::class,
        parentColumns = ["transactionId"],
        childColumns = ["transactionId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class TransactionImage(
    @PrimaryKey(autoGenerate = true)
    val imageId: Int = 0,
    val transactionId: Int,
    val imagePath: String
)
