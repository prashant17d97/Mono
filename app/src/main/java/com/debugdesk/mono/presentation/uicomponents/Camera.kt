package com.debugdesk.mono.presentation.uicomponents

import android.Manifest
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.debugdesk.mono.R
import com.debugdesk.mono.ui.appconfig.AppStateManager
import com.debugdesk.mono.utils.CameraFunction.createImageFile
import com.debugdesk.mono.utils.CameraFunction.deleteImage
import com.debugdesk.mono.utils.CameraFunction.getUriFromFile
import com.debugdesk.mono.utils.CameraFunction.toUriFromFilePath
import com.debugdesk.mono.utils.Dp.dp10
import com.debugdesk.mono.utils.ImageCard
import com.debugdesk.mono.utils.PermissionLauncherHandler
import org.koin.compose.koinInject

@Composable
fun Camera(
    filePaths: List<String> = emptyList(),
    dismiss: () -> Unit = {},
    onSave: (absolutePaths: List<String>) -> Unit = {},
) {
    val context = LocalContext.current
    var imageUris: List<Uri> by remember {
        mutableStateOf(filePaths.toUriFromFilePath(context))
    }

    var absolutePaths: List<String> by remember {
        mutableStateOf(filePaths)
    }
    val appStateManager: AppStateManager = koinInject()
    val file = context.createImageFile()
    val uri = getUriFromFile(context, file)

    var requestPermission by remember {
        mutableStateOf(false)
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
        Log.e("Camera", "Camera: $uri, ${uri.path}, ${file.absolutePath}")
        absolutePaths += file.absolutePath
        imageUris += uri
        requestPermission = false
    }

    if (requestPermission) {
        PermissionLauncherHandler(permissions = arrayOf(Manifest.permission.CAMERA),
            appStateManager = appStateManager,
            errorMsg = R.string.camera_permission,
            onGranted = {
                cameraLauncher.launch(uri)
            },
            onPermissionDenial = { dismiss() })
    }


    Scaffold(floatingActionButton = {
        FloatingActionButton(onClick = { requestPermission = true }) {
            Icon(
                painter = painterResource(id = R.drawable.camera),
                contentDescription = stringResource(
                    id = R.string.camera
                )
            )
        }
    }) {
        ScreenView(modifier = Modifier
            .fillMaxSize()
            .padding(it),
            isScrollEnabled = false,
            horizontalAlignment = Alignment.CenterHorizontally,
            trailing = stringResource(id = R.string.save),
            heading = stringResource(id = R.string.app_name),
            showBack = true,
            onBackClick = {
                imageUris.forEach { uri ->
                    deleteImage(context, uri = uri, onSuccess = { selectedUri ->
                        imageUris = imageUris.filter { uri -> uri != selectedUri }
                    })

                }
                dismiss()
            },
            onTrailClick = { onSave(absolutePaths) }

        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(dp10, alignment = Alignment.Top)
            ) {
                items(absolutePaths) { absolutePath ->
                    if (absolutePath.isNotEmpty()) {
                        ImageCard(absolutePath = absolutePath, onDelete = { absPath ->
                            deleteImage(context = context,
                                absolutePath = absolutePath,
                                returnMessage = { toast ->
                                    appStateManager.showToastState(toastMsg = toast)
                                }) { selectedUri ->
                                imageUris = imageUris.filter { uri -> uri != selectedUri }
                                absolutePaths =
                                    absolutePaths.filter { path -> path != absPath }
                            }
                        })
                    }
                }

            }
        }
    }
}