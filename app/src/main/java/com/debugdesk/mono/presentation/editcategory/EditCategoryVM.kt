package com.debugdesk.mono.presentation.editcategory

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.debugdesk.mono.domain.data.local.localdatabase.model.CategoryModel
import com.debugdesk.mono.domain.repo.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditCategoryVM(
    private val repository: Repository
) : ViewModel() {
    companion object {
        private const val TAG = "EditCategoryVM"
    }

    val categoryModelList = repository.categoryModelList

    fun init() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.fetchCategories()
        }
        Log.e(TAG, "Init: Called")
    }

    fun updateExpenseCategory(
        categories: List<CategoryModel>,
        model: CategoryModel
    ): List<CategoryModel> {
        return categories.map {
            if (it.category == model.category) {
                it.copy(isSelected = true)
            } else {
                it // Return original item if not updating
            }
        }
    }

    fun updateIncomeCategory(
        categories: List<CategoryModel>,
        model: CategoryModel
    ): List<CategoryModel> {
        return categories.map {
            if (it.category == model.category) {
                it.copy(isSelected = true)
            } else {
                it // Return original item if not updating
            }
        }
    }

    fun removeCategory(incomeCategory: List<CategoryModel>, expenseCategory: List<CategoryModel>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.removeCategories(incomeCategory.filter { it.isSelected })
            repository.removeCategories(expenseCategory.filter { it.isSelected })

        }
    }
}
