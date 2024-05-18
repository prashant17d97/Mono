package com.debugdesk.mono.presentation.report

import androidx.annotation.StringRes
import com.debugdesk.mono.R

data class Filter(
    @StringRes var title: Int,
    var isSelected: Boolean = false,
    val filterRange: FilterRange
) {

    companion object {
        val values = listOf(
            Filter(title = R.string.this_month, isSelected = true, filterRange = FilterRange.THIS_MONTH),
            Filter(title = R.string.last_month, isSelected = false, filterRange = FilterRange.LAST_MONTH),
            Filter(
                title = R.string.last_three_month,
                isSelected = false,
                filterRange = FilterRange.LAST_THREE_MONTH
            ),
            Filter(
                title = R.string.last_six_month,
                isSelected = false,
                filterRange = FilterRange.LAST_SIX_MONTH
            ),
            Filter(
                title = R.string.custom_range,
                isSelected = false,
                filterRange = FilterRange.CUSTOM_RANGE
            )
        )

    }
}
