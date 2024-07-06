package com.debugdesk.mono.domain.repo

import android.util.Log
import com.debugdesk.mono.domain.data.local.localdatabase.AppDatabase
import com.debugdesk.mono.domain.data.local.localdatabase.DaoInterface
import com.debugdesk.mono.domain.data.local.localdatabase.model.CategoryModel
import com.debugdesk.mono.domain.data.local.localdatabase.model.DailyTransaction
import com.debugdesk.mono.utils.DBUtils.orIfEmpty
import com.debugdesk.mono.utils.DBUtils.toDailyTransaction
import com.debugdesk.mono.utils.DBUtils.toTransaction
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.getCurrentYear
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RepositoryImpl(
    private val daoInterface: DaoInterface,
    private val appDatabase: AppDatabase
) : Repository {

    companion object {
        private const val TAG = "RepositoryImpl"
    }

    /**
     * Room DB
     * */
    private val _transactionAll: MutableStateFlow<List<DailyTransaction>> =
        MutableStateFlow(emptyList())
    override val allDailyTransaction: StateFlow<List<DailyTransaction>> = _transactionAll

    private val _categoryTransactionAll: MutableStateFlow<List<DailyTransaction>> =
        MutableStateFlow(emptyList())
    override val categoryTransactionAll: StateFlow<List<DailyTransaction>> = _categoryTransactionAll

    private val _allItemSize: MutableStateFlow<Int> = MutableStateFlow(0)
    override val allItemSize: StateFlow<Int> = _allItemSize


    private val _allDailyMonthTransaction: MutableStateFlow<List<DailyTransaction>> =
        MutableStateFlow(emptyList())
    override val allDailyMonthTransaction: StateFlow<List<DailyTransaction>> =
        _allDailyMonthTransaction


    private val _allDailyYearTransaction: MutableStateFlow<List<DailyTransaction>> =
        MutableStateFlow(emptyList())
    override val allDailyYearTransaction: StateFlow<List<DailyTransaction>> =
        _allDailyYearTransaction


    private val _categoryModelList: MutableStateFlow<List<CategoryModel>> =
        MutableStateFlow(emptyList())
    override val categoryModelList: StateFlow<List<CategoryModel>> = _categoryModelList

    private val _editTransaction: MutableStateFlow<DailyTransaction?> =
        MutableStateFlow(null)
    override val editTransaction: StateFlow<DailyTransaction?> = _editTransaction

    override suspend fun getYearRange(): IntRange {
        val year = daoInterface.getAllTransaction().sortedBy { it.year }
        return IntRange(
            start = (year.firstOrNull()?.year ?: getCurrentYear) - 3,
            endInclusive = (year.lastOrNull()?.year ?: getCurrentYear) + 3
        )
    }

    override suspend fun getTransactionAll() {
        val allTransaction = daoInterface.getAllTransaction()
        val dailyTransaction = mutableListOf<DailyTransaction>()
        allTransaction.forEach { transaction ->
            dailyTransaction.add(transaction.toDailyTransaction())
        }
        _transactionAll.tryEmit(dailyTransaction.orIfEmpty())
        _allItemSize.tryEmit(daoInterface.getAllTransaction().size)
    }

    override suspend fun insert(dailyTransaction: DailyTransaction) {
        val transactionWithImgId = dailyTransaction.toTransaction()
        daoInterface.insert(
            transactionWithImgId
        )
        getTransactionAll()
    }

    override suspend fun updateTransaction(dailyTransaction: DailyTransaction) {
        daoInterface.updateTransaction(dailyTransaction.toTransaction())
        getTransactionAll()
    }


    override suspend fun deleteTransaction(dailyTransaction: DailyTransaction) {
        daoInterface.deleteTransaction(dailyTransaction.toTransaction())
        getTransactionAll()
    }

    override suspend fun deleteAllTransaction() {
        daoInterface.deleteAllTransactions()
        getTransactionAll()
    }

    override suspend fun clearDatabase() {
        appDatabase.clearAllTables()
        getTransactionAll()
    }

    override suspend fun getAllTransactionByMonth(month: Int, year: Int) {
        _allDailyMonthTransaction.tryEmit(
            daoInterface.getAllTransactionByMonth(
                month = month, year = year
            ).map { transaction ->
                transaction.toDailyTransaction()
            }.orIfEmpty()
        )
    }

    override suspend fun getAllTransactionByYear(year: Int) {
        _allDailyYearTransaction.tryEmit(
            daoInterface.getAllTransactionByYear(year = year)
                .map { transaction ->
                    transaction.toDailyTransaction()
                }.orIfEmpty()
        )
    }

    override suspend fun saveCategories(categoryModel: CategoryModel) {
        fetchCategories()
        delay(100)
        if (categoryModel !in categoryModelList.value) {
            daoInterface.insertCategory(categoryModel)
            delay(100)
            fetchCategories()
        }
    }

    override suspend fun fetchCategories() {
        _categoryModelList.tryEmit(daoInterface.getAllCategory().orIfEmpty())
    }

    override suspend fun removeCategories(categories: List<CategoryModel>) {
        for (category in categories) {
            daoInterface.deleteCategory(category)
        }
        fetchCategories()
    }

    override suspend fun fetchAllTransactionFromCategoryID(categoryID: Int) {
        val categoryTransaction = daoInterface.fetchAllTransactionFromCategoryID(categoryID)
        val transaction = categoryTransaction.map { transaction ->
            transaction.toDailyTransaction()
        }.orIfEmpty()
        _categoryTransactionAll.tryEmit(transaction)
    }

    override suspend fun getTransactionByDateRange(startDate: Long, endDate: Long) {
        val allMonthTransaction = daoInterface.findItemsInDateRange(
            startDate = startDate, endDate = endDate
        )
        _allDailyMonthTransaction.tryEmit(allMonthTransaction.map { transaction ->
            transaction.toDailyTransaction()
        }.orIfEmpty())
    }

    override suspend fun fetchTransactionFromId(transactionId: Int) {
        // Fetch the transaction and store it in a variable
        val transaction = daoInterface.fetchTransactionFromId(transactionId).toDailyTransaction()

        // Log the fetched transaction
        Log.d(TAG, "fetchTransactionFromId: $transaction")

        // Emit the fetched transaction
        val emitted = _editTransaction.tryEmit(transaction)

        // Log whether the emission was successful
        Log.d(TAG, "Emission successful: $emitted")

        // Log the current value of _editTransaction
        Log.d(TAG, "fetchTransactionFromIdPost: ${_editTransaction.value}")

    }
}