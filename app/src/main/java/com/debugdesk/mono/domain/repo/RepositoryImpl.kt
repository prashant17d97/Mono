package com.debugdesk.mono.domain.repo

import com.debugdesk.mono.domain.data.local.localdatabase.AppDatabase
import com.debugdesk.mono.domain.data.local.localdatabase.DaoInterface
import com.debugdesk.mono.domain.data.local.localdatabase.model.CategoryModel
import com.debugdesk.mono.domain.data.local.localdatabase.model.DailyTransaction
import com.debugdesk.mono.domain.data.local.localdatabase.model.TransactionImage
import com.debugdesk.mono.domain.data.local.localdatabase.model.emptyTransaction
import com.debugdesk.mono.utils.CameraFunction
import com.debugdesk.mono.utils.DBUtils.orIfEmpty
import com.debugdesk.mono.utils.DBUtils.toDailyTransactionWithId
import com.debugdesk.mono.utils.DBUtils.toTransactionWithId
import com.debugdesk.mono.utils.DBUtils.toTransactionWithoutId
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.getCurrentYear
import com.debugdesk.mono.utils.enums.ImageFrom
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
            val images = daoInterface.getImage(transaction.transactionUniqueId)
            dailyTransaction.add(transaction.toDailyTransactionWithId(images))
        }
        _transactionAll.tryEmit(dailyTransaction.orIfEmpty())
        _allItemSize.tryEmit(daoInterface.getAllTransaction().size)
    }

    override suspend fun insert(dailyTransaction: DailyTransaction) {
        val idAppliedTransaction = dailyTransaction.toTransactionWithoutId()
        daoInterface.insert(
            idAppliedTransaction
        )
        dailyTransaction.transactionImage.forEach {
            daoInterface.insertImage(
                it.copy(
                    transactionId = idAppliedTransaction.transactionId,
                    transactionUniqueId = idAppliedTransaction.transactionUniqueId
                )
            )
        }
        getTransactionAll()
    }

    override suspend fun updateTransaction(dailyTransaction: DailyTransaction) {
        daoInterface.updateTransaction(dailyTransaction.toTransactionWithId())
        val imagesInDB = daoInterface.getImage(dailyTransaction.transactionUniqueId)
        val filterPath =
            imagesInDB.filter { it.absolutePath !in dailyTransaction.absolutePaths }

        dailyTransaction.transactionImage.forEach { imagePath ->
            if (imagePath !in imagesInDB.map { it }) {
                daoInterface.insertImage(
                    imagePath.copy(
                        transactionId = dailyTransaction.transactionId,
                        transactionUniqueId = dailyTransaction.transactionUniqueId,
                    )
                )
            }
        }
        filterPath.forEach {
            daoInterface.deleteImage(it)
        }
        getTransactionAll()
    }


    override suspend fun deleteTransaction(dailyTransaction: DailyTransaction) {
        daoInterface.deleteTransaction(dailyTransaction.toTransactionWithId())
        dailyTransaction.transactionImage.forEach { _ ->
            daoInterface.deleteImagesForTransaction(dailyTransaction.transactionUniqueId)
        }
        getTransactionAll()
    }

    override suspend fun deleteAllTransaction() {
        daoInterface.deleteAllTransactions()
        daoInterface.getAllImage().forEach {
            if (it.from == ImageFrom.CAMERA.name) {
                CameraFunction.deleteFile(it.absolutePath)
            }
        }
        daoInterface.deleteAllImage()
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
                val images = daoInterface.getImage(transaction.transactionUniqueId)
                transaction.toDailyTransactionWithId(images)
            }.orIfEmpty()
        )
    }

    override suspend fun getAllTransactionByYear(year: Int) {
        _allDailyYearTransaction.tryEmit(
            daoInterface.getAllTransactionByYear(year = year)
                .map { transaction ->
                    val images = daoInterface.getImage(transaction.transactionUniqueId)
                    transaction.toDailyTransactionWithId(images)
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

    override suspend fun getTransactionByDateRange(startDate: Long, endDate: Long) {
        val allMonthTransaction = daoInterface.findItemsInDateRange(
            startDate = startDate, endDate = endDate
        )
        _allDailyMonthTransaction.tryEmit(allMonthTransaction.map { transaction ->
            val images = daoInterface.getImage(transaction.transactionUniqueId)
            transaction.toDailyTransactionWithId(images)
        }.orIfEmpty())
    }

    override suspend fun fetchTransactionFromId(transactionId: Int) {
        daoInterface.fetchTransactionFromId(transactionId)
            .also {
                _transaction.tryEmit(
                    it.toDailyTransactionWithId(daoInterface.getImage(it.transactionUniqueId))
                )
            }

    }

    override suspend fun deleteTransactionImage(
        transactionImage: TransactionImage,
        onSuccess: () -> Unit
    ) {
        val dbImages = daoInterface.getAllImage()
        if (transactionImage in dbImages) {
            daoInterface.deleteImage(transactionImage)
            onSuccess()
        }
    }
}