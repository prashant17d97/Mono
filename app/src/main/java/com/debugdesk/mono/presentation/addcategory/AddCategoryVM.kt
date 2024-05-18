package com.debugdesk.mono.presentation.addcategory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.debugdesk.mono.domain.repo.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddCategoryVM(
    private val repository: Repository,
) : ViewModel() {
    private val _categoryModel: MutableStateFlow<AddCategoryState> =
        MutableStateFlow(AddCategoryState())
    val categoryModel: StateFlow<AddCategoryState> = _categoryModel

    fun onIntentChange(
        intent: AddCategoryIntent, navHostController: NavHostController, argument: String
    ) {
        when (intent) {
            is AddCategoryIntent.AddCategory -> {
                _categoryModel.tryEmit(intent.categoryModel)
            }

            is AddCategoryIntent.SaveCategory -> {
                viewModelScope.launch(Dispatchers.IO) {
                    repository.saveCategories(categoryModel.value.copy(categoryType = argument))
                }.invokeOnCompletion { throwable ->
                    throwable?.let {
                        _categoryModel.tryEmit(
                            AddCategoryState(
                                error = it.localizedMessage ?: "Error occurred"
                            )
                        )
                        return@invokeOnCompletion
                    }
                    _categoryModel.tryEmit(AddCategoryState(clearKeyboardFocus = true))
                }
            }

            is AddCategoryIntent.NavigateBack -> {
                navHostController.popBackStack()
            }
        }
    }
}