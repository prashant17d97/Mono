package com.debugdesk.mono.domain.repo

import com.debugdesk.mono.domain.data.local.localdatabase.AppDatabase
import com.debugdesk.mono.domain.data.local.localdatabase.DaoInterface
import com.debugdesk.mono.domain.data.local.localdatabase.model.CategoryModel
import com.debugdesk.mono.domain.data.local.localdatabase.model.DailyTransaction
import com.debugdesk.mono.domain.data.local.localdatabase.model.emptyTransaction
import com.debugdesk.mono.presentation.addcategory.AddCategoryState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RepositoryImpl(
    private val daoInterface: DaoInterface,
    private val appDatabase: AppDatabase,
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
        _transactionAll.tryEmit(daoInterface.getAllTransaction())
        _allItemSize.tryEmit(daoInterface.getAllTransaction().size)
    }

    override suspend fun insert(dailyTransaction: DailyTransaction) {
        daoInterface.insert(dailyTransaction)
        getTransactionAll()
    }

    override suspend fun updateTransaction(dailyTransaction: DailyTransaction) {
        daoInterface.insert(dailyTransaction)
        getTransactionAll()
    }

    override suspend fun deleteTransaction(dailyTransaction: DailyTransaction) {
        daoInterface.deleteTransaction(dailyTransaction)
        getTransactionAll()
    }

    override suspend fun clearDatabase() {
        appDatabase.clearAllTables()
        getTransactionAll()
    }

    override suspend fun getAllTransactionByMonth(month: Int, year: Int) {
        val allMonthTransaction =
            daoInterface.getAllTransactionByMonth(
                month = month,
                year = year
            )
        _allDailyMonthTransaction.tryEmit(
            allMonthTransaction
        )
    }

    override suspend fun getAllTransactionByYear(year: Int) {
        _allDailyYearTransaction.tryEmit(daoInterface.getAllTransactionByYear(year = year))
    }

    override suspend fun saveCategories(addCategoryState: AddCategoryState) {
        fetchCategories()
        delay(100)
        val categoryModel = CategoryModel(
            category = addCategoryState.category,
            categoryIcon = addCategoryState.categoryIcon,
            categoryType = addCategoryState.categoryType,
        )
        if (categoryModelList.value.contains(categoryModel)) {
            return
        }
        daoInterface.insertCategory(categoryModel)
        fetchCategories()
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
        val allMonthTransaction =
            daoInterface.findItemsInDateRange(
                startDate = startDate, endDate = endDate
            )
        _allDailyMonthTransaction.tryEmit(
            allMonthTransaction
        )
    }

    override suspend fun fetchTransactionFromId(transaction: Int) {
        _transaction.tryEmit(daoInterface.fetchTransactionFromId(transaction))
    }
}