package com.debugdesk.mono.utils

import android.Manifest
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.debugdesk.mono.BuildConfig
import com.debugdesk.mono.R
import com.debugdesk.mono.presentation.uicomponents.media.PermissionHandler
import com.debugdesk.mono.presentation.uicomponents.media.RequestCode
import com.debugdesk.mono.ui.appconfig.AppStateManager
import com.debugdesk.mono.utils.Dp.dp0
import java.io.ByteArrayOutputStream
import java.io.File
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

    fun getByteArrayFromUri(context: Context, uri: Uri?): ByteArray {
        var bitmap: Bitmap? = null
        try {
            val inputStream = context.contentResolver.openInputStream(uri!!)
            bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return (bitmap ?: Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)).toByteArray()
    }

    private fun Bitmap.toByteArray(): ByteArray {
        val stream = ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.PNG, 50, stream)
        return stream.toByteArray()
    }

    fun ByteArray.toImageBitmap(): ImageBitmap? {
        try {
            val bitmap = BitmapFactory.decodeByteArray(this, 0, this.size)
            return bitmap.asImageBitmap()
        } catch (exc: NullPointerException) {
            Log.d(TAG, "toImageBitmap: ${exc.localizedMessage}")
            return null
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
