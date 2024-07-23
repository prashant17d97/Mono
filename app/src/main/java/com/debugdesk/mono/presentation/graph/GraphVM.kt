package com.debugdesk.mono.presentation.graph

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.debugdesk.mono.domain.repo.Repository
import com.debugdesk.mono.ui.appconfig.AppConfigManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class GraphVM(
    private val repository: Repository,
    private val appConfigManager: AppConfigManager,
) : ViewModel(), KoinComponent {
    private val _graphState: MutableStateFlow<GraphState> =
        MutableStateFlow(GraphState())
    val graphState: StateFlow<GraphState>
        get() = _graphState
    private var isRequestingForNewFetch: Int? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            observedCategoryTransaction()
            observedAppConfigManager()
        }
    }

    companion object {
        private val TAG = GraphVM::class.java.simpleName
    }

    private suspend fun observedAppConfigManager() {
        appConfigManager.appConfigProperties.collect { appConfigProperties ->
            _graphState.tryEmit(
                graphState.value.copy(
                    currencyIcon = appConfigProperties.selectedCurrencyIconString,
                ),
            )
        }
    }

    private suspend fun observedCategoryTransaction() {
        repository.categoryTransactionAll.collect { transaction ->
            _graphState.tryEmit(
                graphState.value.copy(
                    transaction = transaction,
                ),
            )
        }
    }

    fun fetchTransaction(categoryId: Int) {
        if (isRequestingForNewFetch != categoryId) {
            isRequestingForNewFetch = categoryId
            viewModelScope.launch(Dispatchers.IO) {
                repository.fetchAllTransactionFromCategoryID(categoryId)
            }
        }
    }

    fun handleGraphIntent(
        graphIntent: GraphIntent,
        navHostController: NavHostController,
    ) {
        when (graphIntent) {
            GraphIntent.NavigateBack -> navHostController.popBackStack()
            GraphIntent.PromptFilter -> handleFilterBSM(true)
            GraphIntent.HideFilter -> handleFilterBSM(false)
            is GraphIntent.UpdateTab -> updateTab(graphIntent.tabs)
        }
    }

    private fun updateTab(tab: Tabs) {
        _graphState.tryEmit(graphState.value.copy(selectedTabs = tab))
    }

    private fun handleFilterBSM(show: Boolean) {
        _graphState.tryEmit(graphState.value.copy(promptFilter = show))
    }
}
