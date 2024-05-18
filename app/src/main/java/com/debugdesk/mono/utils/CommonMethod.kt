package com.debugdesk.mono.utils

object CommonMethod {
    fun Boolean.getConditionedIcon(targetValue: Int, initialValue: Int): Int {
        return targetValue.takeIf { this }
            ?: initialValue
    }
}