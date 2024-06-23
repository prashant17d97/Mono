package com.debugdesk.mono.domain.data.local.localdatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.debugdesk.mono.domain.data.local.localdatabase.model.CategoryModel
import com.debugdesk.mono.domain.data.local.localdatabase.model.Transaction

@Dao
interface DaoInterface {

    @Query("SELECT * FROM transactionEntry ORDER BY date DESC")
    suspend fun getAllTransaction(): List<Transaction>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: Transaction)

    @Delete(Transaction::class)
    suspend fun deleteTransaction(transaction: Transaction)

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Query("SELECT * FROM transactionEntry WHERE year = :year AND currentMonthId = :month ORDER BY date DESC")
    suspend fun getAllTransactionByMonth(year: Int, month: Int): List<Transaction>

    @Query("SELECT * FROM transactionEntry WHERE transactionId = :transactionId ORDER BY date DESC")
    suspend fun fetchTransactionFromId(transactionId: Int): Transaction

    @Query("SELECT * FROM transactionEntry WHERE year = :year  ORDER BY date DESC")
    suspend fun getAllTransactionByYear(year: Int): List<Transaction>

    @Query("SELECT * FROM transactionEntry WHERE date BETWEEN :startDate AND :endDate")
    suspend fun findItemsInDateRange(startDate: Long, endDate: Long): List<Transaction>

    @Query("SELECT * FROM categoryModel")
    suspend fun getAllCategory(): List<CategoryModel>

    @Delete(CategoryModel::class)
    suspend fun deleteCategory(categoryModel: CategoryModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(categoryModel: CategoryModel)

    @Query("DELETE FROM transactionEntry")
    suspend fun deleteAllTransactions()

    @Query("SELECT * FROM transactionEntry WHERE categoryId = :categoryId")
    suspend fun fetchAllTransactionFromCategoryID(categoryId: Int): List<Transaction>
}