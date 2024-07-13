package com.debugdesk.mono.presentation.edittrans

import com.debugdesk.mono.domain.data.local.localdatabase.model.CategoryModel
import com.debugdesk.mono.domain.data.local.localdatabase.model.DailyTransaction
import com.debugdesk.mono.domain.data.local.localdatabase.model.emptyTransaction
import com.debugdesk.mono.presentation.uicomponents.amounttf.AmountTfState
import com.debugdesk.mono.ui.appconfig.AppStateManager
import com.debugdesk.mono.ui.appconfig.AppStateManagerImpl
import com.debugdesk.mono.utils.enums.ImageSource
import java.util.Date

data class TransactionState(
    val transaction: DailyTransaction = emptyTransaction,
    val date: Long = Date().time,
    val showCalendarDialog: Boolean = false,
    val amountTfState: AmountTfState = AmountTfState(),
    val note: String = "",
    val image: String = "",
    val imageSource: ImageSource = ImageSource.NONE,
    val createdOn: Long = 0L,
    val categoryList: List<CategoryModel> = emptyList(),
    val showCameraAndGallery: Boolean = false,
    val changesFound: Boolean = false,
    val appStateManager: AppStateManager = AppStateManagerImpl()
)