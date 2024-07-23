package com.debugdesk.mono.presentation.graph

import androidx.annotation.StringRes
import com.debugdesk.mono.R
import com.debugdesk.mono.domain.data.local.localdatabase.model.DailyTransaction
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.distributeTransactionsByDate

data class GraphState(
    val selectedTabs: Tabs = Tabs.Month,
    val transaction: List<DailyTransaction> = emptyList(),
    @StringRes
    val currencyIcon: Int = R.string.inrIcon,
    val promptFilter: Boolean = false,
) {
    val distributedTransaction = transaction.distributeTransactionsByDate()
    val graphState: EffectState
        get() =
            when {
                transaction.isEmpty() -> EffectState.NoDataFound
                else -> EffectState.Loaded
            }
}

enum class EffectState {
    Loaded,
    NoDataFound,
    NONE,
}
