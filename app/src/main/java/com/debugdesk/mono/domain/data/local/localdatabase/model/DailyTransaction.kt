package com.debugdesk.mono.domain.data.local.localdatabase.model

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import coil.compose.rememberAsyncImagePainter
import com.debugdesk.mono.R
import com.debugdesk.mono.utils.CameraFunction.toBitmap
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.takeWord
import com.debugdesk.mono.utils.enums.ExpenseType
import com.debugdesk.mono.utils.enums.ImageSource
import java.util.Date

data class DailyTransaction(
    val currentMonthId: Int = 0,
    val date: Long = Date().time,
    val transactionId: Int = 0,
    val type: String,
    val note: String,
    val category: String,
    val categoryIcon: Int,
    val categoryId: Int,
    val amount: Double,
    val imagePath: String = "",
    val imageSize: String = "",
    val imageSource: ImageSource = ImageSource.NONE,
    val createdOn: Long = System.currentTimeMillis(),
    val year: Int? = null,
) {
    private val bit: Bitmap by lazy { imagePath.toBitmap() }
    val painter: Painter
        @Composable
        get() {
            return rememberAsyncImagePainter(model = bit)
        }

    val showImage: Boolean get() = imagePath.isNotEmpty()

    val notes: String
        get() = if (note.isNotEmpty()) "$category (${note.takeWord(2)})" else category
}

val emptyTransaction =
    DailyTransaction(
        type = "",
        note = "",
        category = "",
        categoryIcon = 0,
        categoryId = 0,
        amount = 0.0,
        imagePath = "",
        imageSource = ImageSource.NONE,
    )
val previewTransaction =
    DailyTransaction(
        type = ExpenseType.Expense.name,
        note = "Biryani",
        category = "Food",
        categoryIcon = R.drawable.food,
        categoryId = 0,
        amount = 500.0,
        currentMonthId = 5,
        imagePath = "",
        imageSource = ImageSource.NONE,
        year = 2024,
    )

val previewIncomeTransaction =
    DailyTransaction(
        type = ExpenseType.Income.name,
        note = "OpenBet",
        category = "Salary",
        categoryIcon = R.drawable.bank,
        categoryId = 0,
        amount = 500.0,
        currentMonthId = 5,
        imagePath = "",
        imageSource = ImageSource.NONE,
        year = 2024,
    )

val listOfPreviewTransaction =
    listOf(
        previewTransaction,
        previewTransaction,
        previewTransaction,
        previewTransaction,
        previewTransaction,
        previewIncomeTransaction,
        previewIncomeTransaction,
        previewIncomeTransaction,
        previewIncomeTransaction,
    )
