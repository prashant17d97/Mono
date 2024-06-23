package com.debugdesk.mono.utils

import com.debugdesk.mono.domain.data.local.localdatabase.model.DailyTransaction
import com.debugdesk.mono.domain.data.local.localdatabase.model.Transaction
import com.debugdesk.mono.utils.enums.ImageSource

object DBUtils {

    fun DailyTransaction.toTransaction() = Transaction(
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
        year = this.year
    )

    fun Transaction.toDailyTransaction() = DailyTransaction(
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

}