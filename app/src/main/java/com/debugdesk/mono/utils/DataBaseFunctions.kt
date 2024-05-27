package com.debugdesk.mono.utils

import android.os.Build
import com.debugdesk.mono.domain.data.local.localdatabase.model.DailyTransaction
import java.time.YearMonth
import java.util.Calendar

object DataBaseFunctions {

    fun filterTransactionsByYear(
        transactions: List<DailyTransaction>,
        year: Int
    ): List<DailyTransaction> {
        val filteredTransactions = mutableListOf<DailyTransaction>()
        val processedMonths = mutableSetOf<Int>() // Track processed months for a given year

        transactions.sortedByDescending { it.date.toYearMonth() } // Sort by year-month descending
            .forEach { transaction ->
                val currentMonth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    transaction.date.toYearMonth().monthValue
                } else {
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = transaction.date
                    calendar.get(Calendar.MONTH) + 1 // Calendar month starts from 0 (January)
                }

                if (currentMonth !in processedMonths && transaction.date.toYear() == year) {
                    filteredTransactions.add(transaction)
                    processedMonths.add(currentMonth)
                }
            }

        return filteredTransactions
    }

    // Helper extension function for converting Long to YearMonth
    private fun Long.toYearMonth(): YearMonth =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            YearMonth.of(this.toYear(), this.toMonth() + 1)
        } else {
            TODO("VERSION.SDK_INT < O")
        }

    // Helper extension functions for converting Long to year and month (optional)
    private fun Long.toYear(): Int = this.toInt() / 10000
    private fun Long.toMonth(): Int = (this.toInt() % 10000) / 100


    fun findTransactionsForMonth(
        transactions: List<DailyTransaction>,
        month: Int,
        year: Int
    ): List<DailyTransaction> {
        return transactions.filter { transaction ->
            val transactionCalendar = transaction.date.toCalendar()
            transactionCalendar.get(Calendar.MONTH) == month - 1 && // Months are 0-indexed
                    transactionCalendar.get(Calendar.YEAR) == year
        }
    }

    // Helper extension function for converting Long to Calendar
    private fun Long.toCalendar(): Calendar {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = this
        return calendar
    }


}