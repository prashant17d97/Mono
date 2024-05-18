package com.debugdesk.mono.presentation.input.state

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import com.debugdesk.mono.R

data class InputStates(
    val tabs: List<Int> = listOf(R.string.expense, R.string.income),
    val income: String = "",
    val incomeNote: String = "",
    val expense: String = "",
    val expensesNote: String = "",
    val openBottomSheet: Boolean = false,
    val skipPartiallyExpanded: Boolean = false,
) {

    val amount = { index: Int -> expense.takeIf { index == 0 } ?: income }
    val note = { index: Int -> expensesNote.takeIf { index == 0 } ?: incomeNote }
    val amountDouble =
        { index: Int -> (if (amount(index) == "") "0.0" else amount(index)).toDouble() }
}

val InputStatesSaver: Saver<InputStates, *> = mapSaver(
    save = {
        mapOf(
            "tabs" to it.tabs,
            "income" to it.income,
            "incomeNote" to it.incomeNote,
            "expense" to it.expense,
            "expensesNote" to it.expensesNote,
            "openBottomSheet" to it.openBottomSheet,
        )
    },
    restore = {
        InputStates(
            tabs = it["tabs"] as List<Int>? ?: listOf(R.string.expense, R.string.income),
            income = it["income"] as String? ?: "0.0",
            incomeNote = it["incomeNote"] as String? ?: "",
            expense = it["expense"] as String? ?: "0.0",
            expensesNote = it["expensesNote"] as String? ?: "",
            openBottomSheet = it["openBottomSheet"] as Boolean? ?: false,
        )
    }
)

