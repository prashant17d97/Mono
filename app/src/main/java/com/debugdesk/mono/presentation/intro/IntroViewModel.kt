package com.debugdesk.mono.presentation.intro

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.debugdesk.mono.R
import com.debugdesk.mono.domain.data.local.localdatabase.model.CategoryModel
import com.debugdesk.mono.domain.repo.Repository
import com.debugdesk.mono.model.IntroModel
import com.debugdesk.mono.ui.appconfig.AppConfigManager
import com.debugdesk.mono.utils.enums.ExpenseType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class IntroViewModel(
    private val appConfigManager: AppConfigManager,
    private val repository: Repository
) : ViewModel() {

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.fetchCategories()
        }
    }

    fun getIntroModel(context: Context): List<IntroModel> = listOf(
        IntroModel(
            heading = context.getString(R.string.introHeading1),
            description = context.getString(R.string.introDescription1),
            img = R.drawable.intro_img_one
        ),
        IntroModel(
            heading = context.getString(R.string.introHeading2),
            description = context.getString(R.string.introDescription2),
            img = R.drawable.intro_img_two
        ),
        IntroModel(
            heading = context.getString(R.string.introHeading3),
            description = context.getString(R.string.introDescription3),
            img = R.drawable.intro_img_three
        ),
    )


    fun saveSomeCategory(context: Context) {
        // Added default categories for new User
        viewModelScope.launch(Dispatchers.IO) {
            val savedCategory = repository.categoryModelList.value
            listOf(
                CategoryModel(
                    category = context.getString(R.string.salary),
                    categoryIcon = R.drawable.bank,
                    categoryType = ExpenseType.Income.name,
                ),
                CategoryModel(
                    category = context.getString(R.string.groceries),
                    categoryIcon = R.drawable.cook,
                    categoryType = ExpenseType.Expense.name,
                ),
                CategoryModel(
                    category = context.getString(R.string.food),
                    categoryIcon = R.drawable.food,
                    categoryType = ExpenseType.Expense.name,
                )
            ).forEach {
                if (it !in savedCategory) {
                    repository.saveCategories(it)
                }
            }
        }
    }

    val seconds = (0..100)
        .asSequence()
        .asFlow()
        .map {
            if (it in 0..9) "0$it" else it
        }
        .onEach { delay(1000) }

    fun setIntroFinished() {
        appConfigManager.introCompleted(true)
    }
}