package com.debugdesk.mono.domain.repo

import com.debugdesk.mono.domain.data.local.localdatabase.model.CategoryModel
import com.debugdesk.mono.domain.data.local.localdatabase.model.DailyTransaction
import kotlinx.coroutines.flow.StateFlow

interface Repository {
    /**
     * Room DB
     * */
    val allDailyTransaction: StateFlow<List<DailyTransaction>>
    val categoryTransactionAll: StateFlow<List<DailyTransaction>>
    val allDailyMonthTransaction: StateFlow<List<DailyTransaction>>
    val allDailyYearTransaction: StateFlow<List<DailyTransaction>>
    val transaction: StateFlow<DailyTransaction>
    val allItemSize: StateFlow<Int>
    val categoryModelList: StateFlow<List<CategoryModel>>

    suspend fun getTransactionAll()
    suspend fun getYearRange():IntRange
    suspend fun fetchTransactionFromId(transactionId: Int)
    suspend fun insert(dailyTransaction: DailyTransaction)
    suspend fun deleteTransaction(dailyTransaction: DailyTransaction)
    suspend fun deleteAllTransaction()
    suspend fun updateTransaction(dailyTransaction: DailyTransaction)
    suspend fun clearDatabase()
    suspend fun getAllTransactionByMonth(month: Int, year: Int)
    suspend fun getAllTransactionByYear(year: Int)
    suspend fun saveCategories(categoryModel: CategoryModel)
    suspend fun fetchCategories()
    suspend fun fetchAllTransactionFromCategoryID(categoryID:Int)
    suspend fun removeCategories(categories: List<CategoryModel>)
    suspend fun getTransactionByDateRange(startDate: Long, endDate: Long)
}