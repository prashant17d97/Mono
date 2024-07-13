package com.debugdesk.mono.presentation.input.state

import com.debugdesk.mono.domain.data.local.localdatabase.model.CategoryModel
import com.debugdesk.mono.domain.data.local.localdatabase.model.DailyTransaction
import com.debugdesk.mono.domain.data.local.localdatabase.model.emptyTransaction
import com.debugdesk.mono.presentation.uicomponents.amounttf.AmountTfState
import com.debugdesk.mono.ui.appconfig.AppStateManager
import com.debugdesk.mono.ui.appconfig.AppStateManagerImpl
import com.debugdesk.mono.utils.enums.ExpenseType
import com.debugdesk.mono.utils.enums.ImageSource
import java.util.Date

data class InputState(
    val transaction: DailyTransaction = emptyTransaction,
    val date: Long = Date().time,
    val amountTfState: AmountTfState = AmountTfState(),
    val note: String = "",
    val image: String = "",
    val createdOn: Long = System.currentTimeMillis(),
    val imageSource: ImageSource = ImageSource.NONE,
    val categoryList: List<CategoryModel> = emptyList(),
    val showCalendarDialog: Boolean = false,
    val showCameraAndGallery: Boolean = false,
    val changesFound: Boolean = false,
    val clickedIndex: Int = 0,
    val appStateManager: AppStateManager = AppStateManagerImpl(),
    val transactionType: ExpenseType = ExpenseType.Expense
)