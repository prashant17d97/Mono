package com.debugdesk.mono.domain.repo

import android.content.Context
import android.util.Log
import com.debugdesk.mono.domain.data.local.localdatabase.AppDatabase
import com.debugdesk.mono.domain.data.local.localdatabase.DaoInterface
import com.debugdesk.mono.domain.data.local.localdatabase.model.CategoryModel
import com.debugdesk.mono.domain.data.local.localdatabase.model.DailyTransaction
import com.debugdesk.mono.domain.data.local.localdatabase.model.emptyTransaction
import com.debugdesk.mono.utils.DBUtils.toDailyTransactionWithId
import com.debugdesk.mono.utils.DBUtils.toTransactionImage
import com.debugdesk.mono.utils.DBUtils.toTransactionWithId
import com.debugdesk.mono.utils.DBUtils.toTransactionWithoutId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

class RepositoryImpl(
    private val daoInterface: DaoInterface,
    private val appDatabase: AppDatabase,
    private val context: Context
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

    private val _transaction: MutableStateFlow<DailyTransaction> =
        MutableStateFlow(emptyTransaction)
    override val transaction: StateFlow<DailyTransaction> = _transaction

    override suspend fun getTransactionAll() {
        val allTransaction = daoInterface.getAllTransaction()
        val dailyTransaction = mutableListOf<DailyTransaction>()
        allTransaction.forEach { transaction ->
            val images = daoInterface.getImage(transaction.transactionId)
            dailyTransaction.add(transaction.toDailyTransactionWithId(images))
        }
        _transactionAll.tryEmit(dailyTransaction)
        _allItemSize.tryEmit(daoInterface.getAllTransaction().size)
    }

    override suspend fun insert(dailyTransaction: DailyTransaction) {
        daoInterface.insert(
            dailyTransaction.toTransactionWithoutId()
        )
        dailyTransaction.images.forEach {
            daoInterface.insertImage(
                it.toTransactionImage(
                    transactionId = dailyTransaction.transactionId
                )
            )
        }
        getTransactionAll()
    }

    override suspend fun updateTransaction(dailyTransaction: DailyTransaction) {
        withContext(Dispatchers.IO) {
            daoInterface.updateTransaction(dailyTransaction.toTransactionWithId())
            val imagesInDB = daoInterface.getImage(dailyTransaction.transactionId)
            val filterPath = imagesInDB.filter { it.filePath !in dailyTransaction.images }

            dailyTransaction.images.forEach { imagePath ->
                if (imagePath !in imagesInDB.map { it.filePath }) {
                    daoInterface.insertImage(
                        imagePath.toTransactionImage(
                            transactionId = dailyTransaction.transactionId,
                        )
                    )
                }
            }
            filterPath.forEach {
                daoInterface.deleteImage(it)
            }
            getTransactionAll()
        }
    }


    override suspend fun deleteTransaction(dailyTransaction: DailyTransaction) {
        daoInterface.deleteTransaction(dailyTransaction.toTransactionWithId())
        dailyTransaction.images.forEach { _ ->
            daoInterface.deleteImagesForTransaction(dailyTransaction.transactionId)
        }
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
        val allMonthTransaction = daoInterface.getAllTransactionByMonth(
            month = month, year = year
        )
        _allDailyMonthTransaction.tryEmit(allMonthTransaction.map { transaction ->
            val images = daoInterface.getImage(transaction.transactionId)
            transaction.toDailyTransactionWithId(images)
        })
    }

    override suspend fun getAllTransactionByYear(year: Int) {
        _allDailyYearTransaction.tryEmit(daoInterface.getAllTransactionByYear(year = year)
            .map { transaction ->
                val images = daoInterface.getImage(transaction.transactionId)
                transaction.toDailyTransactionWithId(images)
            })
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
        _categoryModelList.tryEmit(daoInterface.getAllCategory())
    }

    override suspend fun removeCategories(categories: List<CategoryModel>) {
        for (category in categories) {
            daoInterface.deleteCategory(category)
        }
        fetchCategories()
    }

    override suspend fun getTransactionByDateRange(startDate: Long, endDate: Long) {
        val allMonthTransaction = daoInterface.findItemsInDateRange(
            startDate = startDate, endDate = endDate
        )
        _allDailyMonthTransaction.tryEmit(allMonthTransaction.map { transaction ->
            val images = daoInterface.getImage(transaction.transactionId)
            transaction.toDailyTransactionWithId(images)
        })
    }

    override suspend fun fetchTransactionFromId(transactionId: Int) {
        val fetchedTransaction = daoInterface.fetchTransactionFromId(transactionId)
            .toDailyTransactionWithId(daoInterface.getImage(transactionId))

        Log.e(
            TAG,
            "fetchTransactionFromId: $fetchedTransaction, \nImage:${
                daoInterface.getImage(transactionId)
            }",
        )
        _transaction.tryEmit(
            fetchedTransaction
        )
    }
}