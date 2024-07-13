package com.debugdesk.mono.utils

import android.Manifest
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.debugdesk.mono.BuildConfig
import com.debugdesk.mono.R
import com.debugdesk.mono.presentation.uicomponents.media.PermissionHandler
import com.debugdesk.mono.presentation.uicomponents.media.RequestCode
import com.debugdesk.mono.ui.appconfig.AppStateManager
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CameraFunction {

    private const val TAG = "CameraFunction"

    fun Context.createImageFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File(
            storageDir,
            "${getString(R.string.app_name)}_${timestamp}.jpg"
        )
    }

    fun getUriFromFile(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${BuildConfig.APPLICATION_ID}.provider",
            file
        )
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
            val children = dir.list() ?: emptyArray()
            for (child in children) {
                val success = deleteDir(File(dir, child))
                if (!success) {
                    return false
                }
            }
        }
        return dir?.delete() ?: false
    }

    fun String.toBitmap(): Bitmap {
        try {
            val decodedString = Base64.decode(this, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        } catch (ex: FileNotFoundException) {
            return Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)
        }
    }

    suspend fun Uri?.toGalleryBase64(context: Context): String? {
        val galleryFile = this?.getRealPathFromURI(context)?.let { File(it) }
        return if (galleryFile != null && galleryFile.exists()) {
            galleryFile.toCompressedBase64(context)
        } else {
            null
        }
    }

    private fun Uri.getRealPathFromURI(context: Context): String? {
        var cursor: Cursor? = null
        return try {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(this, projection, null, null, null)
            val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(columnIndex)
        } finally {
            cursor?.close()
        }
    }

    suspend fun File.toCompressedBase64(context: Context): String {
        return withContext(Dispatchers.IO) {
            val originalFile = File(path)

            // Determine the quality based on the original file size
            var quality = 75 // Initial quality
            var compressedImageFile: File

            // Compress loop to achieve desired size
            do {
                compressedImageFile = Compressor.compress(context, originalFile) {
                    quality(quality = quality)
                    resolution(width = 720, height = 1024)
                    size(maxFileSize = 500 * 1020)
                }
                quality -= 5 // Decrease quality gradually
            } while (compressedImageFile.length() > 500 * 1024 && quality >= 30) // Adjust conditions based on desired size

            // Rotate the image based on its orientation
            val rotatedBitmap = rotateImageIfRequired(compressedImageFile.absolutePath)

            // Convert rotated bitmap to Base64 string
            bitmapToBase64(rotatedBitmap)
        }
    }

    private fun rotateImageIfRequired(imagePath: String): Bitmap {
        val exif = ExifInterface(imagePath)
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )

        val rotationAngle = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }

        val bitmap = BitmapFactory.decodeFile(imagePath)
        val matrix = Matrix()
        matrix.postRotate(rotationAngle.toFloat())

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun getNotificationPermissionHandler(
        appStateManager: AppStateManager
    ): PermissionHandler {
        return PermissionHandler(
            permissionStrings = arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            appStateManager = appStateManager,
            errorMsg = R.string.notification_permission,
            openSettingMsg = R.string.notification_denied,
            requestCode = RequestCode.NOTIFICATION
        )
    }

    fun clearExternalFilesDirPictures(context: Context) {
        val directory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (directory != null && directory.isDirectory) {
            val files = directory.listFiles()
            if (files != null) {
                for (file in files) {
                    if (file.isFile) {
                        file.delete()
                    }
                }
            }
        }
    }
}
