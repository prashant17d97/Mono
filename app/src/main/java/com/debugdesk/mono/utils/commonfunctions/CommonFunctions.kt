package com.debugdesk.mono.utils.commonfunctions

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.DialogProperties
import com.debugdesk.mono.R
import com.debugdesk.mono.domain.data.local.localdatabase.model.DailyTransaction
import com.debugdesk.mono.model.FontModel
import com.debugdesk.mono.model.LanguageModel
import com.debugdesk.mono.ui.appconfig.AppStateManager
import com.debugdesk.mono.ui.appconfig.defaultconfig.ThemeMode
import com.debugdesk.mono.utils.CommonColor.inActiveButton
import com.debugdesk.mono.utils.enums.ExpenseType
import com.debugdesk.mono.utils.states.AlertState
import kotlinx.coroutines.delay
import java.text.DateFormat
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Calendar
import java.util.Currency
import java.util.Date
import java.util.Locale

object CommonFunctions {

    @Composable
    fun TimerDelay(
        timeInMilliSecond: Long = 100L,
        onTimerEnd: suspend () -> Unit
    ) {
        LaunchedEffect(
            key1 = Unit,
            block = {
                try {
                    startTimer(
                        time = timeInMilliSecond,
                        onTimerEnd = onTimerEnd
                    )
                } catch (ex: Exception) {
                    Log.e("TAG", "TimerDelayException: ${ex.message} timer cancelled")
                }
            }
        )
    }


    private suspend fun startTimer(time: Long, onTimerEnd: suspend () -> Unit) {
        delay(timeMillis = time)
        onTimerEnd()
    }

    fun Long.toDate(pattern: String = "MMM dd, yyyy"): String {
        val obj: DateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        return obj.format(Date(this))
    }

    fun Long.toDateWeek(pattern: String = "MMM dd, yyyy (EEE)"): String {
        val date = Date(this)
        val formatter = SimpleDateFormat(
            pattern, Locale.getDefault()
        ) // Customize format as needed
        return formatter.format(date)

    }

    fun Context.getLanguages(language: String): List<LanguageModel> =
        resources.getStringArray(R.array.language).map {
            LanguageModel(language = it, isSelected = it == language)
        }

    fun Context.getFontFamilies(language: String): List<FontModel> =
        resources.getStringArray(R.array.fontFamilies).map {
            FontModel(font = it, isSelected = it == language)
        }

    @Composable
    fun ThemeMode.isDarkTheme(): Boolean {
        return when (this) {
            ThemeMode.Default -> isSystemInDarkTheme()
            ThemeMode.Light -> false
            ThemeMode.Dark -> true
        }
    }

    fun String.getCurrencyIcon(): Int {
        return when (this) {
            "INR" -> R.string.inrIcon
            "USD" -> R.string.usdIcon
            "EUR" -> R.string.eurIcon
            else -> R.string.inrIcon
        }
    }

    fun String.getCurrencyCode(): Int {
        return when (this) {
            "INR" -> R.string.inr
            "USD" -> R.string.usd
            "EUR" -> R.string.eur
            else -> R.string.inr
        }
    }

    fun String.getCurrencyDrawableIcon(): Int {
        return when (this) {
            "INR" -> R.drawable.ic_rupee
            "USD" -> R.drawable.ic_dollar
            "EUR" -> R.drawable.ic_euro
            else -> R.drawable.ic_rupee
        }
    }


    fun Long.longToDateString(datePattern: String = "yyyy-MM-dd"): String {
        val date = Date(this)
        val format = SimpleDateFormat(datePattern, Locale.getDefault())
        return format.format(date)
    }

    fun List<DailyTransaction>.getTotalAmount(): Double {
        return getIncomeAmount() - getExpenseAmount()
    }

    fun List<DailyTransaction>.getIncomeAmount(): Double {
        return this.filter { it.type == ExpenseType.Income.name }.sumOf { it.amount }
    }

    fun List<DailyTransaction>.getExpenseAmount(): Double {
        return this.filter { it.type == ExpenseType.Expense.name }.sumOf { it.amount }
    }

    fun List<DailyTransaction>.distributeTransactionsByDate(): Map<Int, List<DailyTransaction>> {
        return groupBy { transaction ->
            transaction.date.toCalendar().get(Calendar.DAY_OF_MONTH)
        }.mapValues { (_, transactionsByDate) ->
            transactionsByDate.toList()
        }
    }

    // Helper extension function for converting Long to Calendar
    private fun Long.toCalendar(): Calendar {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = this
        return calendar
    }


    fun getMonthRange(monthsAgo: Int): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.time = Date()

        // Set the end date as today's date
        val endDate = calendar.timeInMillis

        // Set the start date by subtracting the specified number of months
        calendar.add(Calendar.MONTH, -monthsAgo)
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH))

        val startDate = calendar.timeInMillis

        return Pair(startDate, endDate)
    }

    fun getCurrentMonthYear(): Pair<Int, Int> {
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)  // 0-based (January = 0, December = 11)
        val currentYear = calendar.get(Calendar.YEAR)
        return Pair(currentMonth, currentYear)
    }

    fun getMonthAndYearFromLong(timestamp: Long): Pair<Int, Int> {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        val month = calendar.get(Calendar.MONTH) // 0-based (January = 0, December = 11)
        val year = calendar.get(Calendar.YEAR)
        return Pair(month, year)
    }


    fun getPreviousMonth(currentMonth: Int, currentYear: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, currentMonth)
        calendar.set(Calendar.YEAR, currentYear)
        calendar.add(Calendar.MONTH, -1)
        return calendar.timeInMillis
    }


    fun getNextMonth(currentMonth: Int, currentYear: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, currentMonth)
        calendar.set(Calendar.YEAR, currentYear)
        calendar.add(Calendar.MONTH, 1)
        return calendar.timeInMillis
    }


    fun getMonthOnClick(isLeft: Boolean): Pair<Long, String> {
        val calendar = Calendar.getInstance()
        if (isLeft) {
            calendar.add(Calendar.MONTH, -1)
        } else {
            calendar.add(Calendar.MONTH, 1)
        }
        val year = calendar.get(Calendar.YEAR)
        val monthString =
            calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
        return Pair(calendar.timeInMillis, "$monthString, $year")
    }

    fun getMonthName(month: Int, languageTag: String = "en"): String {
        //val monthList = stringArrayResource(id = R.array.months)
        return DateFormatSymbols(Locale.forLanguageTag(languageTag)).months[month]
    }

    val String.filterIncomeType: Boolean get() = this == ExpenseType.Income.name
    val String.filterExpenseType: Boolean get() = this == ExpenseType.Expense.name

    fun String.double(): Double {
        return try {
            this.toDouble()
        } catch (e: NumberFormatException) {
            0.0
        }
    }


    fun String.takeWord(n: Int): String {
        if (isBlank()) {
            return ""
        }
        val words = trim().split("\\s+".toRegex())

        return if (words.size <= n) {
            this
        } else {
            words.take(n).joinToString(" ")
        }
    }

    fun String.toCurrencyIcon(): String {
        return Currency.getInstance(this).getSymbol(Locale.getDefault())
    }

    fun AppStateManager.showAlertDialog(
        @StringRes title: Int = R.string.alert,
        @StringRes message: Int = R.string.delete_all_transactions,
        @StringRes positiveButtonText: Int = R.string.okay,
        @StringRes negativeButtonText: Int = R.string.cancel,
        @DrawableRes iconDrawable: Int? = R.drawable.ic_warning,
        iconColor: Color = inActiveButton,
        dismissOnBackPress: Boolean = true,
        dismissOnClickOutside: Boolean = true,
        onNegativeClick: () -> Unit = {},
        onPositiveClick: () -> Unit = {}
    ) {
        updateAlertState(AlertState(title = title,
            message = message,
            positiveButtonText = positiveButtonText,
            negativeButtonText = negativeButtonText,
            iconDrawable = iconDrawable,
            show = true,
            iconColor = iconColor,
            properties = DialogProperties(
                dismissOnBackPress = dismissOnBackPress,
                dismissOnClickOutside = dismissOnClickOutside
            ),
            onNegativeClick = {
                hideAlertDialog()
                onNegativeClick()
            },
            onPositiveClick = {
                hideAlertDialog()
                onPositiveClick()
            }

        )
        )
    }

    fun Long.formatFileSize(): String {
        val sizeInBytes: Long = this
        val kb = 1024.0
        val mb = kb * 1024
        val gb = mb * 1024
        val tb = gb * 1024

        val (format, value) = when {
            sizeInBytes < kb -> "%.0f Bytes" to sizeInBytes.toDouble()
            sizeInBytes < mb -> "%.2f KB" to sizeInBytes / kb
            sizeInBytes < gb -> "%.2f MB" to sizeInBytes / mb
            sizeInBytes < tb -> "%.2f GB" to sizeInBytes / gb
            else -> "%.2f TB" to sizeInBytes / tb
        }

        return String.format(Locale.getDefault(), format, value)
    }

    fun getDateOfMonthFromTimestamp(timestamp: Long): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val instant = Instant.ofEpochMilli(timestamp)
            val zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())
            zonedDateTime.dayOfMonth
        } else {
            val calendar = Calendar.getInstance().apply {
                timeInMillis = timestamp
            }
            calendar.get(Calendar.DAY_OF_MONTH)
        }
    }

    val getCurrentDate: Int get() = getDateComponentsFromTimestamp().first
    val getCurrentMonth: Int get() = getDateComponentsFromTimestamp().second
    val getCurrentYear: Int get() = getDateComponentsFromTimestamp().third

    private fun getDateComponentsFromTimestamp(timestamp: Long = System.currentTimeMillis()): Triple<Int, Int, Int> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val instant = Instant.ofEpochMilli(timestamp)
            val zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())
            Triple(zonedDateTime.dayOfMonth, zonedDateTime.monthValue - 1, zonedDateTime.year)
        } else {
            val calendar = Calendar.getInstance().apply {
                timeInMillis = timestamp
            }
            Triple(
                first = calendar.get(Calendar.DAY_OF_MONTH),
                second = calendar.get(Calendar.MONTH),
                third = calendar.get(Calendar.YEAR)
            )
        }
    }


    fun String.toIntIfEmpty(): Int {
        return if (this.isNotEmpty() && isNotBlank()) {
            toInt()
        } else {
            0
        }
    }

    fun getDayOfWeekName(pattern: String = "EEE"): String {
        val calendar = Calendar.getInstance()
        return SimpleDateFormat(pattern, Locale.getDefault()).format(calendar.time)
    }
}
