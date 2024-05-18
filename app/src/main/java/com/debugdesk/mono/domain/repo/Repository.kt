package com.debugdesk.mono.domain.repo

import com.debugdesk.mono.domain.data.local.localdatabase.model.CategoryModel
import com.debugdesk.mono.domain.data.local.localdatabase.model.DailyTransaction
import com.debugdesk.mono.presentation.addcategory.AddCategoryState
import kotlinx.coroutines.flow.StateFlow

interface Repository {
    /**
     * Room DB
     * */
    val allDailyTransaction: StateFlow<List<DailyTransaction>>
    val allDailyMonthTransaction: StateFlow<List<DailyTransaction>>
    val allDailyYearTransaction: StateFlow<List<DailyTransaction>>
    val transaction: StateFlow<DailyTransaction>
    val allItemSize: StateFlow<Int>
    val categoryModelList: StateFlow<List<CategoryModel>>

    suspend fun getTransactionAll()
    suspend fun fetchTransactionFromId(transaction: Int)
    suspend fun insert(dailyTransaction: DailyTransaction)
    suspend fun deleteTransaction(dailyTransaction: DailyTransaction)
    suspend fun updateTransaction(dailyTransaction: DailyTransaction)
    suspend fun clearDatabase()
    suspend fun getAllTransactionByMonth(month: Int, year: Int)
    suspend fun getAllTransactionByYear(year: Int)
    suspend fun saveCategories(addCategoryState: AddCategoryState)
    suspend fun fetchCategories()
    suspend fun removeCategories(categories: List<CategoryModel>)
    suspend fun getTransactionByDateRange(startDate: Long, endDate: Long)
}