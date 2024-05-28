package com.debugdesk.mono.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.debugdesk.mono.domain.data.local.localdatabase.model.TransactionImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ImageUtils {

    fun createEmptyBitmap(): Bitmap {
        return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888).apply {
            Canvas(this).drawRect(0f, 0f, 1f, 1f, Paint().apply { color = Color.TRANSPARENT })
        }
    }

    // Convert URI to Bitmap
    fun uriToBitmap(context: Context, uri: Uri): Bitmap {
        return try {
            context.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it)
            } ?: createEmptyBitmap()
        } catch (e: IOException) {
            e.printStackTrace()
            createEmptyBitmap()
        }
    }


    // Convert Bitmap to Uri
    fun bitmapToUri(context: Context, bitmap: Bitmap, filename: String): Uri? {
        val file = File(context.cacheDir, filename)
        return try {
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }
            Uri.fromFile(file)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun List<Bitmap>.bitmapToUri(context: Context): List<Uri> {
        return mapIndexed { index, bitmap ->
            bitmapToUri(context, bitmap, "Mono_$index.png") ?: Uri.EMPTY
        }
    }

    fun List<Uri>.toBitMaps(context: Context): List<Bitmap> {
        return map { uri ->
            uriToBitmap(context, uri)
        }
    }

    fun encodeImageToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(
            Bitmap.CompressFormat.PNG,
            100,
            byteArrayOutputStream
        ) // Adjust compression ratio as needed
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun Bitmap.toBase64(): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        compress(
            Bitmap.CompressFormat.PNG,
            100,
            byteArrayOutputStream
        ) // Adjust compression ratio as needed
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun base64ToBitmap(base64String: String): Bitmap {
        try {
            val decodedString = Base64.decode(base64String, Base64.DEFAULT)
            val byteArray = ByteArrayOutputStream().apply {
                write(decodedString)
                flush()
            }.toByteArray()
            return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        } catch (e: IllegalArgumentException) {
            // Log or handle invalid Base64 string exception
            Log.e("Base64ToBitmap", "Invalid Base64 string", e)
        }
        return createEmptyBitmap()
    }


    fun List<Bitmap>.toBase64String(): List<String> {
        val base64 = mutableListOf<String>()
        forEach {
            base64.add(encodeImageToBase64(it))
        }
        return base64
    }

    fun List<TransactionImage>.toBitmap(): List<Bitmap> {
        val bitmaps: MutableList<Bitmap> = mutableListOf()
        forEach {
            bitmaps.add(base64ToBitmap(it.filePath))
        }
        return bitmaps
    }
}
