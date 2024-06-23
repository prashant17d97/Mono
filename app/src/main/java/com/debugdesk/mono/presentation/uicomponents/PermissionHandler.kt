package com.debugdesk.mono.presentation.uicomponents

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import com.debugdesk.mono.R
import com.debugdesk.mono.presentation.uicomponents.media.PermissionHandler
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.showAlertDialog

@Composable
fun PermissionLauncherHandler(
    permissionHandler: PermissionHandler,
    onGranted: () -> Unit,
    onPermissionDenial: () -> Unit = {}
) {
    var requestPermission by remember {
        mutableStateOf(true)
    }
    val context = LocalContext.current
    val activity = context as Activity
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionMap ->
        val isRational = shouldShowRequestPermissionRationaleForList(
            activity,
            permissionHandler.permissionStrings
        )
        checkPermissions(
            permissionMap = permissionMap,
            permissions = permissionHandler.permissionStrings,
            onError = {
                permissionHandler.appStateManager.showAlertDialog(
                    title = R.string.permission_access,
                    message = permissionHandler.message(isRational),
                    iconDrawable = null,
                    onPositiveClick = {
                        if (isRational) {
                            requestPermission = true
                        } else {
                            navigateToAppSettings(activity)
                        }
                    },
                    onNegativeClick = onPermissionDenial
                )
            },
            onGranted = onGranted
        )
    }
    LaunchedEffect(requestPermission) {
        if (requestPermission) {
            permissionLauncher.launch(permissionHandler.permissionStrings)
            requestPermission = false
        }
    }
}

private fun checkPermissions(
    permissionMap: Map<String, Boolean>,
    permissions: Array<String>,
    onGranted: () -> Unit = {},
    onError: (List<String>) -> Unit = {}
) {
    // Check if all permissions in the array are present in the map
    if (permissions.size != permissionMap.size) {
        onError(permissions.toList())
        return
    }

    // Loop through each permission and check if it's granted in the map
    val nonGrantedPermissions = mutableListOf<String>()
    for (permission in permissions) {
        if (!permissionMap.containsKey(permission) || !permissionMap[permission]!!) {
            nonGrantedPermissions.add(permission)
        }
    }

    if (nonGrantedPermissions.isNotEmpty()) {
        onError(nonGrantedPermissions)
    } else {
        onGranted()
    }
}

private fun navigateToAppSettings(activity: Activity) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts("package", activity.packageName, null)
    intent.data = uri
    activity.startActivity(intent)
}

private fun shouldShowRequestPermissionRationaleForList(
    activity: Activity,
    permissions: Array<String>
): Boolean {
    return permissions.any { permission ->
        shouldShowRequestPermissionRationale(
            activity,
            permission
        )
    }
}

fun Context.hasPermission(permission: Array<String>): Boolean {
    return permission.map {
        ActivityCompat.checkSelfPermission(
            this,
            it
        ) == PackageManager.PERMISSION_GRANTED
    }.all { it }
}


