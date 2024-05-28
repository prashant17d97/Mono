package com.debugdesk.mono.domain.data.local.localdatabase.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "transaction_images",
)
data class TransactionImage(
    @PrimaryKey(autoGenerate = true)
    val imageId: Int = 0,
    val transactionId: Int,
    val filePath: String
)
