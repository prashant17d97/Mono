package com.debugdesk.mono.presentation.input

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.debugdesk.mono.domain.data.local.localdatabase.model.DailyTransaction
import com.debugdesk.mono.domain.repo.Repository
import com.debugdesk.mono.ui.appconfig.AppConfigManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InputVM(
    private val repository: Repository,
    private val appConfigManager: AppConfigManager,
) : ViewModel() {

    val appConfigProperties = appConfigManager.appConfigProperties
    val categoryModels = repository.categoryModelList

    fun init() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.fetchCategories()
        }
    }

    fun insertTransaction(transaction: DailyTransaction) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(transaction)
        }
    }
}