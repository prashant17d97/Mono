package com.debugdesk.mono.presentation.uicomponents

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import com.debugdesk.mono.R

@Composable
fun ImagePicker(onImageSelected: (Uri) -> Unit) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageUri = result.data?.data
            if (imageUri != null) {
                onImageSelected(imageUri)
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { result ->

    }
    var hasStoragePermission by remember {
        mutableStateOf(false)
    }

    var hasCameraPermission by remember {
        mutableStateOf(false)
    }

    var showPermissionDialog by remember {
        mutableStateOf(false)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (result.values.all { it }) {
            val intent = chooseImageIntent()
            launcher.launch(intent)
        } else {
            showPermissionDialog = true
        }
    }

    // Check permissions based on Android version
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        val permissionStatusStorage = ContextCompat.checkSelfPermission(
            LocalContext.current,
            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
        )
        hasStoragePermission = permissionStatusStorage == PackageManager.PERMISSION_GRANTED
    } else {
        val permissionStatusStorage = ContextCompat.checkSelfPermission(
            LocalContext.current,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        hasStoragePermission = permissionStatusStorage == PackageManager.PERMISSION_GRANTED
    }

    val permissionStatusCamera = ContextCompat.checkSelfPermission(
        LocalContext.current,
        Manifest.permission.CAMERA
    )
    hasCameraPermission = permissionStatusCamera == PackageManager.PERMISSION_GRANTED

    // Handle permission denial with alert
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text(text = stringResource(R.string.permission_required)) },
            text = { Text(text = stringResource(R.string.permission_explanation)) },
            confirmButton = {
                Button(onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
                                Manifest.permission.CAMERA,
                                Manifest.permission.READ_MEDIA_IMAGES,
                            )
                        )
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.READ_MEDIA_IMAGES,
                                Manifest.permission.CAMERA,
                            )
                        )
                    } else {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA
                            )
                        )

                    }
                    showPermissionDialog = false
                }) {
                    Text(text = stringResource(R.string.grant_permission))
                }
            }
        )
    }

    Row {
        Button(onClick = {
            if (hasCameraPermission) {
                cameraLauncher.launch(Uri.EMPTY)
            } else {
                showPermissionDialog = true
            }
        }) {
            Text(text = stringResource(R.string.capture_image))
        }
        Button(onClick = {
            if (hasStoragePermission && hasCameraPermission) {
                val intent = chooseImageIntent()
                launcher.launch(intent)
            } else {
                showPermissionDialog = true
            }
        }) {
            Text(text = stringResource(R.string.select_image))
        }
    }
}

fun chooseImageIntent(): Intent {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent
    } else {
        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    }
}
