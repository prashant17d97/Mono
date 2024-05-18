package com.debugdesk.mono.utils.commonfunctions

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.debugdesk.mono.R
import com.debugdesk.mono.domain.data.local.localdatabase.model.DailyTransaction
import com.debugdesk.mono.model.FontModel
import com.debugdesk.mono.model.LanguageModel
import com.debugdesk.mono.ui.appconfig.defaultconfig.ThemeMode
import com.debugdesk.mono.utils.enums.ExpenseType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.text.DateFormat
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Currency
import java.util.Date
import java.util.Locale

object CommonFunctions {


    @Composable
    fun TimerDelay(
        timeInMilliSecond: Long = 1000L,
        onTimerEnd: () -> Unit
    ) {
        LaunchedEffect(key1 = Unit, block = {
            try {
                startTimer(timeInMilliSecond) { // start a timer for 5 secs
                    onTimerEnd()
                }
            } catch (ex: Exception) {
                Log.e("TAG", "TimerDelayException: ${ex.message} timer cancelled")
            }
        })
    }


    private suspend fun startTimer(time: Long, onTimerEnd: () -> Unit) {
        delay(timeMillis = time)
        onTimerEnd()
    }

    fun Long.toDate(pattern: String = "MMM dd, yyyy"): String {
        val obj: DateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        return obj.format(Date(this))
    }

    fun Long.toDateWeek(pattern: String = "MMM dd, yyyy (EEE)"): String {
        val date = Date(this)
        val formatter =
            SimpleDateFormat(
                pattern,
                Locale.getDefault()
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
            "INR" -> R.drawable.ic_rupee
            "USD" -> R.drawable.ic_dollar
            "EUR" -> R.drawable.ic_euro
            else -> R.drawable.ic_rupee
        }
    }


    suspend fun uriToBase64(contentResolver: ContentResolver, uri: Uri): String? {
        return withContext(Dispatchers.IO) {
            try {
                // Decode URI to bitmap
                val inputStream = contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)

                // Convert bitmap to byte array
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                val byteArray = outputStream.toByteArray()

                // Encode byte array to Base64 string
                val base64String = Base64.encodeToString(byteArray, Base64.DEFAULT)

                base64String
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }


    // Convert Base64 String to ImageBitmap
    @Composable
    fun toImageBitmap(base64String: String): ImageBitmap {
        val decodedImage = remember(base64String) {
            val byteArray = Base64.decode(base64String, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            bitmap.asImageBitmap()
        }

        return if (decodedImage.width > 0 && decodedImage.height > 0) {
            // Image loaded successfully
            decodedImage
        } else {
            // Handle error or display placeholder
            ImageBitmap(100, 100) // Placeholder with desired dimensions
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
        val month = calendar.get(Calendar.MONTH)
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
        return Currency.getInstance(this).symbol
    }

}
