package com.debugdesk.mono.presentation.uicomponents.media

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.debugdesk.mono.R
import com.debugdesk.mono.presentation.edittrans.TransactionIntent
import com.debugdesk.mono.presentation.uicomponents.BottomSheet
import com.debugdesk.mono.presentation.uicomponents.PermissionLauncherHandler
import com.debugdesk.mono.presentation.uicomponents.PreviewTheme
import com.debugdesk.mono.ui.appconfig.AppStateManager
import com.debugdesk.mono.utils.CameraFunction
import com.debugdesk.mono.utils.CameraFunction.clearExternalFilesDirPictures
import com.debugdesk.mono.utils.CameraFunction.createImageFile
import com.debugdesk.mono.utils.CameraFunction.getByteArrayFromUri
import com.debugdesk.mono.utils.CameraFunction.getUriFromFile
import com.debugdesk.mono.utils.CameraFunction.toImageBitmap
import com.debugdesk.mono.utils.enums.ImageSource

@Composable
fun MediaBottomSheet(
    visible: Boolean = false,
    appStateManager: AppStateManager,
    onProcess: (TransactionIntent) -> Unit = {},
) {
    val context = LocalContext.current
    val file = context.createImageFile()
    val uri = getUriFromFile(context, file)

    var permissionHandler: PermissionHandler? by remember {
        mutableStateOf(null)
    }

    DisposableEffect(Unit) {
        onDispose {
            clearExternalFilesDirPictures(context)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
        val bitmap = getByteArrayFromUri(context, uri)
        if ((bitmap.toImageBitmap()?.height ?: 0) > 1 && (bitmap.toImageBitmap()?.width ?: 0) > 1) {
            onProcess(
                TransactionIntent.SaveImage(
                    imagePath = bitmap,
                    imageSource = ImageSource.CAMERA,
                    createdOn = System.currentTimeMillis()
                )
            )
        }
    }

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uriFile ->
            val bitmap = getByteArrayFromUri(context, uriFile)
            if ((bitmap.toImageBitmap()?.height ?: 0) > 1 && (bitmap.toImageBitmap()?.width
                    ?: 0) > 1
            ) {
                onProcess(
                    TransactionIntent.SaveImage(
                        imagePath = bitmap,
                        imageSource = ImageSource.GALLERY,
                        createdOn = System.currentTimeMillis()
                    )
                )
            }
        }

    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            Log.e("CameraAndGallery: ", event.name)
            if (event == Lifecycle.Event.ON_RESUME) {
                permissionHandler = null
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }


    permissionHandler?.let {
        PermissionLauncherHandler(
            permissionHandler = it,
            onGranted = {
                when (it.requestCode) {
                    RequestCode.CAMERA -> cameraLauncher.launch(uri)
                    RequestCode.GALLERY -> galleryLauncher.launch("image/*")
                }
            },
        )
        onProcess(TransactionIntent.DismissCameraAndGalleryWindow)
    }


    MediaBottomSheetContainer(visible = visible,
        appStateManager = appStateManager,
        permissionHandler = {
            permissionHandler = it
        },
        dismiss = {
            onProcess(TransactionIntent.DismissCameraAndGalleryWindow)
        })

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MediaBottomSheetContainer(
    visible: Boolean,
    appStateManager: AppStateManager,
    permissionHandler: (PermissionHandler) -> Unit,
    dismiss: () -> Unit = {},
) {

    BottomSheet(
        show = visible, onDismiss = dismiss
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            Fab(
                text = stringResource(id = R.string.gallery),
                icon = painterResource(id = R.drawable.ic_gallary)
            ) {
                permissionHandler(
                    CameraFunction.getGalleryPermissionHandler(
                        appStateManager = appStateManager
                    )
                )
            }

            Fab(
                text = stringResource(id = R.string.camera),
                icon = painterResource(id = R.drawable.camera)
            ) {
                permissionHandler(
                    CameraFunction.getCameraPermissionHandler(
                        appStateManager = appStateManager
                    )
                )
            }

        }
    }
}

@Preview
@Composable
private fun MediaBottomSheetPrev() {
    PreviewTheme {
        Fab(
            text = stringResource(id = R.string.gallery),
            icon = painterResource(id = R.drawable.camera)
        ) {

        }
    }
}

@Composable
fun Fab(
    modifier: Modifier = Modifier,
    icon: Painter,
    text: String,
    onClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(
            space = 10.dp,
            alignment = Alignment.Top
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FloatingActionButton(onClick = onClick) {
            Icon(
                painter = icon,
                contentDescription = stringResource(
                    id = R.string.camera
                )
            )
        }

        Text(text = text)
    }
}


