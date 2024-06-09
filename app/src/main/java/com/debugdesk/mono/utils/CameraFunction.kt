package com.debugdesk.mono.utils

import android.Manifest
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.content.Context
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.debugdesk.mono.BuildConfig
import com.debugdesk.mono.R
import com.debugdesk.mono.domain.data.local.localdatabase.model.TransactionImage
import com.debugdesk.mono.domain.data.local.localdatabase.model.emptyTransactionImageDetail
import com.debugdesk.mono.model.ImageDetails
import com.debugdesk.mono.model.emptyImageDetails
import com.debugdesk.mono.presentation.uicomponents.media.PermissionHandler
import com.debugdesk.mono.presentation.uicomponents.media.RequestCode
import com.debugdesk.mono.ui.appconfig.AppStateManager
import com.debugdesk.mono.utils.Dp.dp0
import com.debugdesk.mono.utils.enums.ImageFrom
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects

object CameraFunction {

    private const val TAG = "CameraFunction"
    fun Context.createImageFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "${getString(R.string.app_name)}_${timestamp}_", ".jpg", storageDir
        ).apply {
            // Ensure the file is created
            if (!exists()) {
                createNewFile()
            }
        }
    }

    fun clearPicturesFolder(context: Context) {
        try {
            val picturesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            deleteDir(picturesDir)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun deleteDir(dir: File?): Boolean {
        if (dir != null && dir.isDirectory) {
            val children = dir.list()
            for (child in children) {
                val success = deleteDir(File(dir, child))
                if (!success) {
                    return false
                }
            }
        }
        return dir?.delete() ?: false
    }


    @Composable
    fun rememberAbsolutePathPainter(path: String): AsyncImagePainter {
        val context = LocalContext.current
        return rememberAsyncImagePainter(model = getUriFromFilePath(context, path))
    }

    private fun getUriFromFilePath(context: Context, filePath: String): Uri {
        val file = File(filePath)
        return FileProvider.getUriForFile(
            Objects.requireNonNull(context), BuildConfig.APPLICATION_ID + ".provider", file
        )

    }

    fun getUriFromFile(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(
            Objects.requireNonNull(context), BuildConfig.APPLICATION_ID + ".provider", file
        )

    }

    fun String.getImageDetails(): ImageDetails {
        return try {
            val file = File(this)

            // Check if the file exists
            if (!file.exists() || !file.isFile) {
                return emptyImageDetails
            }

            // Get file name
            val fileName = file.name

            // Get file size in bytes
            val fileSize = file.length()

            // Get last modified date
            val lastModified = SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault()
            ).format(Date(file.lastModified()))

            // Decode the image file to get its dimensions
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(this, options)

            // Retrieve the image width and height
            val width = options.outWidth
            val height = options.outHeight

            // Determine the image orientation
            val orientation = when {
                width > height -> "Landscape"
                width < height -> "Portrait"
                else -> "Square"
            }

            ImageDetails(
                absolutePath = this,
                fileName = fileName,
                fileSize = fileSize,
                lastModified = lastModified,
                width = width,
                height = height,
                orientation = orientation,
                isEmpty = false
            )
        } catch (e: Exception) {
            e.printStackTrace()
            emptyImageDetails // Error occurred while getting image details, return null
        }
    }

    fun String.getTransactionImage(
        transactionId: Int, imageId: Int = 0, from: ImageFrom
    ): TransactionImage {
        return try {
            val file = File(this)

            // Check if the file exists
            if (!file.exists() || !file.isFile) {
                return emptyTransactionImageDetail
            }

            // Get file name
            val fileName = file.name

            // Get file size in bytes
            val fileSize = file.length()

            // Get last modified date
            val lastModified = SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault()
            ).format(Date(file.lastModified()))

            // Decode the image file to get its dimensions
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(this, options)

            // Retrieve the image width and height
            val width = options.outWidth
            val height = options.outHeight

            // Determine the image orientation
            val orientation = when {
                width > height -> "Landscape"
                width < height -> "Portrait"
                else -> "Square"
            }

            TransactionImage(
                imageId = imageId,
                transactionId = transactionId,
                transactionUniqueId = ObjectIdGenerator.generate(),
                absolutePath = this,
                fileName = fileName,
                fileSize = fileSize,
                lastModified = lastModified,
                width = width,
                height = height,
                orientation = orientation,
                isEmpty = width <= 0 && height <= 0,
                from = from.name
            )
        } catch (e: Exception) {
            e.printStackTrace()
            emptyTransactionImageDetail // Error occurred while getting image details, return null
        }
    }


    @Composable
    fun getImageHeightInDp(absolutePath: String): Dp {
        val screenHeight = LocalConfiguration.current.screenHeightDp * 0.8
        val (_, height) = getImageResolution(absolutePath) ?: return dp0

        return if (screenHeight > height) {
            height
        } else {
            screenHeight.toInt()
        }.dp

    }

    private fun getImageResolution(path: String): Pair<Int, Int>? {
        return try {
            // Decode the image file to get its dimensions
            val options = BitmapFactory.Options().apply {
                // Set inJustDecodeBounds to true to decode only the image size,
                // not the entire bitmap
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(path, options)

            val width = options.outWidth
            val height = options.outHeight

            Pair(width, height)
        } catch (e: Exception) {
            e.printStackTrace()
            null // Error occurred while decoding the image, return null
        }
    }


    fun getGalleryPermissionHandler(
        appStateManager: AppStateManager
    ): PermissionHandler {
        return PermissionHandler(
            permissionStrings = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES, READ_MEDIA_VISUAL_USER_SELECTED
                )
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            },
            appStateManager = appStateManager,
            errorMsg = R.string.gallery_permission,
            openSettingMsg = R.string.gallery_permission_denied,
            requestCode = RequestCode.GALLERY
        )
    }

    fun getCameraPermissionHandler(
        appStateManager: AppStateManager
    ): PermissionHandler {
        return PermissionHandler(
            permissionStrings = arrayOf(Manifest.permission.CAMERA),
            appStateManager = appStateManager,
            errorMsg = R.string.camera_permission,
            openSettingMsg = R.string.gallery_camera_denied,
            requestCode = RequestCode.CAMERA
        )
    }


    fun deleteFile(
        absolutePath: String, onResult: (success: Boolean, notFound: Boolean) -> Unit = { _, _ -> }
    ) {
        val file = File(absolutePath)
        val exists = file.exists()
        if (exists) {
            val success = file.delete()
            onResult(success, false)
            Log.e(TAG, "deleteFile: true")
        } else {
            onResult(false, true)
            Log.e(TAG, "deleteFile: false")
        }
    }

    fun deleteNonExistingFiles(context: Context) {
        val picturesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        picturesDir?.takeIf { it.isDirectory }?.list()?.let { children ->
            for (child in children) {
                val file = File(picturesDir, child)
                val fileImage = file.absolutePath.getTransactionImage(
                    transactionId = 0,
                    from = ImageFrom.CAMERA
                )
                if (file.exists() && fileImage.fileSize == 0L) {
                    if (file.delete()) {
                        Log.e(TAG, "Deleted non-existing camera file: $child")
                    } else {
                        Log.e(TAG, "Failed to delete file: $child")
                    }
                }
            }
        } ?: run {
            Log.e(TAG, "Pictures directory is not accessible or not a directory")
        }
    }


    fun TransactionImage.deleteImageFile(
        onResult: (success: Boolean, notFound: Boolean) -> Unit = { _, _ -> },
        deleteFromDB: (TransactionImage) -> Unit = {}
    ) {
        when (from) {
            ImageFrom.CAMERA.name -> {
                if (absolutePath.isNotEmpty()) {
                    deleteFile(this.absolutePath, onResult = onResult)
                }
            }

            ImageFrom.GALLERY.name -> deleteFromDB(this)
        }

    }

    fun List<TransactionImage>.deleteAllFile(onResult: (success: Boolean, notFound: Boolean) -> Unit = { _, _ -> }) {
        forEach {
            if (it.absolutePath.isNotEmpty() && it.from == ImageFrom.CAMERA.name) {
                deleteFile(absolutePath = it.absolutePath, onResult = onResult)
            }
        }
    }


    fun getAbsolutePathFromUri(context: Context, uri: Uri): String? {
        when {
            // DocumentProvider
            DocumentsContract.isDocumentUri(context, uri) -> {
                val documentId = DocumentsContract.getDocumentId(uri)
                val split = documentId.split(":")
                val type = split[0]

                return if ("primary".equals(type, ignoreCase = true)) {
                    "${context.getExternalFilesDir(null)}/${split[1]}"
                } else {
                    null
                }
            }
            // MediaStore (and general)
            "content".equals(uri.scheme, ignoreCase = true) -> {
                return getDataColumn(context, uri, null, null)
            }
            // File
            "file".equals(uri.scheme, ignoreCase = true) -> {
                return uri.path
            }

            else -> {
                return null
            }
        }
    }

    private fun getDataColumn(
        context: Context, uri: Uri, selection: String?, selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)

        try {
            cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

}
