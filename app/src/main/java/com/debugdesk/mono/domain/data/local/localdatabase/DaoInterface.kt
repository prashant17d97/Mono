package com.debugdesk.mono.domain.data.local.localdatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.debugdesk.mono.domain.data.local.localdatabase.model.CategoryModel
import com.debugdesk.mono.domain.data.local.localdatabase.model.DailyTransaction
import com.debugdesk.mono.utils.enums.ExpenseType

@Dao
interface DaoInterface {

    @Query("SELECT * FROM dailyTransaction ORDER BY date DESC")
    suspend fun getAllTransaction(): List<DailyTransaction>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dailyTransaction: DailyTransaction)

    @Delete(DailyTransaction::class)
    suspend fun deleteTransaction(dailyTransaction: DailyTransaction)

    @Update
    suspend fun updateTransaction(dailyTransaction: DailyTransaction)

    @Query("SELECT * FROM dailyTransaction WHERE year = :year AND currentMonthId = :month ORDER BY date DESC")
    suspend fun getAllTransactionByMonth(year: Int, month: Int): List<DailyTransaction>

    @Query("SELECT * FROM dailyTransaction WHERE transactionId = :transactionId ORDER BY date DESC")
    suspend fun fetchTransactionFromId(transactionId:Int): DailyTransaction

    @Query("SELECT * FROM dailyTransaction WHERE year = :year  ORDER BY date DESC")
    suspend fun getAllTransactionByYear(year: Int): List<DailyTransaction>

    @Query("SELECT * FROM dailyTransaction WHERE date BETWEEN :startDate AND :endDate")
    suspend fun findItemsInDateRange(startDate: Long, endDate: Long): List<DailyTransaction>


    @Query("SELECT * FROM dailyTransaction WHERE type = :income ORDER BY date DESC")
    suspend fun getAllIncomeTransaction(income:String=ExpenseType.Income.name): List<DailyTransaction>

    @Query("SELECT * FROM dailyTransaction WHERE type = :expense ORDER BY date DESC")
    suspend fun getAllExpenseTransaction(expense:String=ExpenseType.Expense.name): List<DailyTransaction>

    @Query("SELECT * FROM categoryModel")
    suspend fun getAllCategory(): List<CategoryModel>

    @Delete(CategoryModel::class)
    suspend fun deleteCategory(categoryModel: CategoryModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(categoryModel: CategoryModel)




}