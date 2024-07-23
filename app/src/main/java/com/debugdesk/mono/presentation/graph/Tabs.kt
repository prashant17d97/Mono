package com.debugdesk.mono.presentation.graph

import androidx.annotation.StringRes
import com.debugdesk.mono.R

enum class Tabs(
    @StringRes val string: Int,
) {
    Month(R.string.month),
    Week(R.string.week),
}
