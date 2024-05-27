package com.debugdesk.mono.utils

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.debugdesk.mono.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CameraFunction {

    private const val TAG = "CameraFunction"
    fun Context.createImageFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "MONO_${timestamp}_",
            ".jpg",
            storageDir
        ).apply {
            // Ensure the file is created
            if (!exists()) {
                createNewFile()
            }
        }
    }

    fun deleteFile(context: Context, file: File): Boolean {
        return try {
            // Check if file exists
            if (file.exists()) {
                // Delete the file from the filesystem
                val fileDeleted = file.delete()
                if (fileDeleted) {
                    Log.d("FileDelete", "File deleted successfully: ${file.absolutePath}")
                    removeFileFromMediaStore(context, file)
                    true
                } else {
                    Log.e("FileDelete", "Failed to delete file: ${file.absolutePath}")
                    false
                }
            } else {
                Log.e("FileDelete", "File does not exist: ${file.absolutePath}")
                false
            }
        } catch (e: Exception) {
            Log.e("FileDelete", "Error deleting file: ${file.absolutePath}", e)
            false
        }
    }

    private fun removeFileFromMediaStore(context: Context, file: File) {
        try {
            val resolver: ContentResolver = context.contentResolver
            val uri: Uri = MediaStore.Files.getContentUri("external")
            val cursor = resolver.query(
                uri,
                arrayOf(MediaStore.Files.FileColumns._ID),
                MediaStore.Files.FileColumns.DATA + "=?",
                arrayOf(file.absolutePath),
                null
            )

            cursor?.use {
                if (it.moveToFirst()) {
                    val id = it.getLong(it.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID))
                    val deleteUri = ContentUris.withAppendedId(uri, id)
                    resolver.delete(deleteUri, null, null)
                    Log.d("FileDelete", "Removed file from Media Store: ${file.absolutePath}")
                }
            }
        } catch (e: Exception) {
            Log.e("FileDelete", "Error removing file from Media Store: ${file.absolutePath}", e)
        }
    }

    fun deleteImage(
        context: Context, uri: Uri,
        returnMessage: (message: Int) -> Unit = {},
        onSuccess: (uri: Uri) -> Unit = {}
    ): Boolean {
        return try {
            val contentResolver = context.contentResolver
            val rowsDeleted = contentResolver.delete(uri, null, null)
            if (rowsDeleted > 0) {
                Log.d("FileDelete", "File deleted successfully: $uri")
                returnMessage(R.string.image_deleted)
                uri.path?.let { File(it) }?.let { deleteFile(context, it) }
                onSuccess(uri)
                true
            } else {
                Log.e("FileDelete", "Failed to delete file: $uri")
                returnMessage(R.string.image_deleted_failed)
                false
            }
        } catch (e: Exception) {
            Log.e("FileDelete", "Error deleting file: $uri", e)
            returnMessage(R.string.image_deleted_error)
            false
        }
    }

    fun List<String>.toUris(): List<Uri> {
        return try {
            if (isNotEmpty()) {
                val uriList: MutableList<Uri> = mutableListOf()
                this.forEach { path ->
                    val uri = Uri.parse(path)
                    uriList.add(uri)
                }
                uriList
            } else {
                emptyList()
            }
        } catch (exception: IllegalArgumentException) {
            Log.e("CameraFunction", "toUris: ${exception.message}")
            emptyList()
        }
    }


    fun List<Uri>.toUriPaths(): List<String> {
        return this.map { it.toString() }
    }


    fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: Exception) {
            Log.e("ImageLoader", "Error loading image from URI: $uri", e)
            null
        }
    }

}