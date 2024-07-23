package com.debugdesk.mono.model

import androidx.annotation.StringRes
import com.debugdesk.mono.R

data class Tabs(
    @StringRes
    val text: Int,
    @Transient
    val isSelected: Boolean,
) {
    companion object {
        val values =
            listOf(
                Tabs(R.string.all, isSelected = true),
                Tabs(R.string.expense, isSelected = false),
                Tabs(R.string.income, isSelected = false),
            )
    }
}
