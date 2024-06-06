package com.debugdesk.mono.presentation.uicomponents.media

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.debugdesk.mono.R
import com.debugdesk.mono.domain.data.local.localdatabase.model.TransactionImage
import com.debugdesk.mono.presentation.edittrans.TransactionIntent
import com.debugdesk.mono.presentation.uicomponents.ImageCard
import com.debugdesk.mono.presentation.uicomponents.NoDataFound
import com.debugdesk.mono.presentation.uicomponents.PermissionLauncherHandler
import com.debugdesk.mono.presentation.uicomponents.ScreenView
import com.debugdesk.mono.ui.appconfig.AppStateManager
import com.debugdesk.mono.utils.CameraFunction.createImageFile
import com.debugdesk.mono.utils.CameraFunction.deleteAllFile
import com.debugdesk.mono.utils.CameraFunction.deleteFile
import com.debugdesk.mono.utils.CameraFunction.getAbsolutePathFromUri
import com.debugdesk.mono.utils.CameraFunction.getCameraPermissionHandler
import com.debugdesk.mono.utils.CameraFunction.getGalleryPermissionHandler
import com.debugdesk.mono.utils.CameraFunction.getTransactionImage
import com.debugdesk.mono.utils.CameraFunction.getUriFromFile
import com.debugdesk.mono.utils.Dp.dp10
import com.debugdesk.mono.utils.enums.ImageFrom
import kotlinx.coroutines.launch

@Composable
fun CameraAndGallery(
    images: List<TransactionImage> = emptyList(),
    appStateManager: AppStateManager,
    onProcess: (TransactionIntent) -> Unit = {},
) {
    val context = LocalContext.current
    var transactionImages: List<TransactionImage> by remember {
        mutableStateOf(images)
    }
    val lazyState = rememberLazyListState()
    val file = context.createImageFile()
    val uri = getUriFromFile(context, file)

    var permissionHandler: PermissionHandler? by remember {
        mutableStateOf(null)
    }

    val scope = rememberCoroutineScope()

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
        val image = file.absolutePath.getTransactionImage(
            transactionId = images.firstOrNull()?.transactionId ?: 0, from = ImageFrom.CAMERA
        )
        Log.e("CameraAndGallery", "Camera: ${file.absolutePath}, imageIsEmpty:${image.isEmpty}")
        if (!image.isEmpty) {
            transactionImages += image
        }
        scope.launch {
            lazyState.animateScrollToItem(transactionImages.lastIndex)
        }
    }

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uriFile ->
            uriFile.forEach {
                getAbsolutePathFromUri(context, it)?.let { absolutePath ->
                    transactionImages += absolutePath.getTransactionImage(
                        transactionId = images.firstOrNull()?.transactionId ?: 0,
                        from = ImageFrom.GALLERY
                    )
                }
            }
            scope.launch {
                lazyState.animateScrollToItem(transactionImages.lastIndex)
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
        PermissionLauncherHandler(permissionHandler = it, onGranted = {
            when (it.requestCode) {
                RequestCode.CAMERA -> cameraLauncher.launch(uri)
                RequestCode.GALLERY -> galleryLauncher.launch("image/*")
            }
        }, onPermissionDenial = {
            onProcess(TransactionIntent.DismissCameraAndGalleryWindow)
        })
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(), floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(
                    space = dp10, alignment = Alignment.Top
                ), horizontalAlignment = Alignment.End
            ) {
                FloatingActionButton(onClick = {
                    permissionHandler = getGalleryPermissionHandler(
                        appStateManager = appStateManager
                    )
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_gallary),
                        contentDescription = stringResource(
                            id = R.string.camera
                        )
                    )
                }
                FloatingActionButton(onClick = {
                    permissionHandler = getCameraPermissionHandler(
                        appStateManager = appStateManager
                    )
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.camera),
                        contentDescription = stringResource(
                            id = R.string.camera
                        )
                    )
                }
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
                transactionImages.filter { path -> path !in images }.deleteAllFile()
                onProcess(
                    TransactionIntent.DismissCameraGallery
                )
            },
            onTrailClick = { onProcess(TransactionIntent.SaveImagesFilePath(transactionImages)) }

        ) {
            NoDataFound(
                text = R.string.no_data_found,
                show = transactionImages.isEmpty()
            )
            LazyColumn(
                state = lazyState,
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(dp10, alignment = Alignment.Top)
            ) {
                items(transactionImages) { transactionImage ->
                    if (transactionImage.absolutePath.isNotEmpty()) {
                        ImageCard(absolutePath = transactionImage.absolutePath,
                            applyFixedSize = false,
                            onDelete = { absPath ->
                                Log.e(
                                    "CameraAndGallery",
                                    "CameraAndGallery: ${transactionImage.from}",
                                )
                                if (transactionImage.from == ImageFrom.CAMERA.name) {
                                    deleteFile(
                                        absolutePath = absPath,
                                        onResult = { success, notFound ->
                                            val message = if (notFound) {
                                                R.string.image_not_found
                                            } else if (success) {
                                                transactionImages =
                                                    transactionImages.filter { path -> path.absolutePath != absPath }
                                                R.string.image_deleted
                                            } else {
                                                R.string.image_deleted_failed
                                            }
                                            appStateManager.showToastState(toastMsg = message)
                                        })
                                } else {
                                    transactionImages =
                                        transactionImages.filter { path -> path != transactionImage }
                                    onProcess(TransactionIntent.DeleteFromDB(transactionImage))
                                    appStateManager.showToastState(toastMsg = R.string.image_deleted)
                                }

                            })
                    }
                }
            }
        }
    }
}


