package com.debugdesk.mono.utils

import com.debugdesk.mono.domain.data.local.localdatabase.model.DailyTransaction
import com.debugdesk.mono.domain.data.local.localdatabase.model.Transaction
import com.debugdesk.mono.domain.data.local.localdatabase.model.TransactionImage

object DBUtils {

    fun DailyTransaction.toTransactionWithId() = Transaction(
        transactionId = this.transactionId,
        transactionUniqueId = this.transactionUniqueId,
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
        transactionUniqueId = this.transactionUniqueId,
        date = this.date,
        type = this.type,
        note = this.note,
        category = this.category,
        categoryIcon = this.categoryIcon,
        categoryId = this.categoryId,
        amount = this.amount,
        transactionImage = image,
        currentMonthId = this.currentMonthId,
        year = this.year
    )

    fun DailyTransaction.toTransactionWithoutId() = Transaction(
        date = this.date,
        type = this.type,
        transactionUniqueId = ObjectIdGenerator.generate(),
        note = this.note,
        category = this.category,
        categoryIcon = this.categoryIcon,
        categoryId = this.categoryId,
        amount = this.amount,
        currentMonthId = this.currentMonthId,
        year = this.year
    )

    inline fun <T> List<T>.orIfEmpty(): List<T> = this.ifEmpty { emptyList() }

}