package com.debugdesk.mono.utils

import com.debugdesk.mono.domain.data.local.localdatabase.model.DailyTransaction
import com.debugdesk.mono.domain.data.local.localdatabase.model.Transaction
import com.debugdesk.mono.utils.enums.ImageSource
import java.util.Locale

object DBUtils {
    fun DailyTransaction.toTransaction() =
        Transaction(
            transactionId = this.transactionId,
            date = this.date,
            type = this.type,
            note = this.note,
            category = this.category,
            categoryIcon = this.categoryIcon,
            categoryId = this.categoryId,
            amount = this.amount,
            currentMonthId = this.currentMonthId,
            imagePath = this.imagePath,
            imageSource = this.imageSource.name,
            createdOn = this.createdOn,
            year = this.year,
        )

    fun Transaction.toDailyTransaction() =
        DailyTransaction(
            transactionId = this.transactionId,
            date = this.date,
            type = this.type,
            note = this.note,
            category = this.category,
            categoryIcon = this.categoryIcon,
            categoryId = this.categoryId,
            amount = this.amount,
            imageSize = getBase64ImageSize(this.imagePath),
            currentMonthId = this.currentMonthId,
            imagePath = this.imagePath,
            imageSource = this.imageSource.toImageSource(),
            createdOn = this.createdOn,
            year = this.year,
        )

    fun <T> List<T>.orIfEmpty(): List<T> = this.ifEmpty { emptyList() }

    private fun String.toImageSource(): ImageSource {
        return when (this) {
            ImageSource.CAMERA.name -> ImageSource.CAMERA
            ImageSource.GALLERY.name -> ImageSource.GALLERY
            else -> ImageSource.NONE
        }
    }

    private fun getBase64ImageSize(base64Image: String): String {
        val dataSize = base64Image.length * 6 / 8.0 // Base64 size in bytes
        val kilobytes = dataSize / 1024.0
        val megabytes = kilobytes / 1024.0
        val gigabytes = megabytes / 1024.0

        return when {
            gigabytes >= 1 -> String.format(Locale.getDefault(), "%.2f GB", gigabytes)
            megabytes >= 1 -> String.format(Locale.getDefault(), "%.2f MB", megabytes)
            kilobytes >= 1 -> String.format(Locale.getDefault(), "%.2f KB", kilobytes)
            else -> String.format(Locale.getDefault(), "%.2f bytes", dataSize)
        }
    }
}
