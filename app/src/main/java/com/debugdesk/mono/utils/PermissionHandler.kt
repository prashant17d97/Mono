package com.debugdesk.mono.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import com.debugdesk.mono.R
import com.debugdesk.mono.ui.appconfig.AppStateManager
import com.debugdesk.mono.utils.commonfunctions.CommonFunctions.showAlertDialog

@Composable
fun PermissionLauncherHandler(
    permissions: Array<String>,
    appStateManager: AppStateManager,
    @StringRes errorMsg: Int,
    onGranted: () -> Unit,
    onPermissionDenial: () -> Unit,
) {
    var requestPermission by remember {
        mutableStateOf(true)
    }
    val context = LocalContext.current
    val activity = context as Activity
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionMap ->
        checkPermissions(
            permissionMap = permissionMap,
            permissions = permissions,
            onError = {
                appStateManager.showAlertDialog(
                    title = R.string.permission_access,
                    message = errorMsg,
                    iconDrawable = null,
                    onPositiveClick = {
                        if (!shouldShowRequestPermissionRationaleForList(
                                activity,
                                permissions
                            )
                        ) {
                            navigateToAppSettings(activity)
                        } else {
                            requestPermission = true
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
            permissionLauncher.launch(permissions)
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


