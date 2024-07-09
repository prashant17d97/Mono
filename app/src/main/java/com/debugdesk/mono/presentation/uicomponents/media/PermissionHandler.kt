package com.debugdesk.mono.presentation.uicomponents.media

import androidx.annotation.StringRes
import com.debugdesk.mono.ui.appconfig.AppStateManager

data class PermissionHandler(
    val permissionStrings: Array<String>,
    val appStateManager: AppStateManager,
    @StringRes
    val errorMsg: Int,
    @StringRes
    val openSettingMsg: Int,
    val requestCode: RequestCode,
) {
    val message = { boolean: Boolean -> errorMsg.takeIf { boolean } ?: openSettingMsg }
}

enum class RequestCode {
    CAMERA,
    GALLERY,
    NOTIFICATION
}