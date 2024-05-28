package com.debugdesk.mono.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.debugdesk.mono.domain.data.local.localdatabase.model.DailyTransaction
import com.debugdesk.mono.domain.data.local.localdatabase.model.Transaction
import com.debugdesk.mono.domain.data.local.localdatabase.model.TransactionImage
import com.debugdesk.mono.utils.ImageUtils.createEmptyBitmap
import com.debugdesk.mono.utils.ImageUtils.toBase64
import java.io.File
import java.io.FileOutputStream

object DBUtils {

    fun DailyTransaction.toTransactionWithId() = Transaction(
        transactionId = this.transactionId,
        date = this.date,
        type = this.type,
        note = this.note,
        category = this.category,
        categoryIcon = this.categoryIcon,
        categoryId = this.categoryId,
        amount = this.amount,
        currentMonthId = this.currentMonthId,
        year = this.year
    )

    fun Transaction.toDailyTransactionWithId(image: List<TransactionImage>) = DailyTransaction(
        transactionId = this.transactionId,
        date = this.date,
        type = this.type,
        note = this.note,
        category = this.category,
        categoryIcon = this.categoryIcon,
        categoryId = this.categoryId,
        images = image.map { it.filePath },
        transactionImage = image,
        amount = this.amount,
        currentMonthId = this.currentMonthId,
        year = this.year
    )

    fun DailyTransaction.toTransactionWithoutId() = Transaction(
        date = this.date,
        type = this.type,
        note = this.note,
        category = this.category,
        categoryIcon = this.categoryIcon,
        categoryId = this.categoryId,
        amount = this.amount,
        currentMonthId = this.currentMonthId,
        year = this.year
    )

    fun List<Bitmap>.toTransactionImage(transactionId: Int) = this.map { bitmap ->
        TransactionImage(transactionId = transactionId, filePath = bitmap.toBase64())
    }

    fun String.toTransactionImage(transactionId: Int) =
        TransactionImage(
            transactionId = transactionId,
            filePath = this
        )

    private fun saveImageToInternalStorage(
        context: Context,
        bitmap: Bitmap,
        imageName: String
    ): String {
        val file = File(context.filesDir, imageName)
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return file.absolutePath
    }

    private fun loadImageFromFilePath(filePath: String): Bitmap {
        return BitmapFactory.decodeFile(filePath) ?: createEmptyBitmap()
    }

    fun String.toBitmap(): Bitmap {
        return BitmapFactory.decodeFile(this) ?: createEmptyBitmap()
    }

}