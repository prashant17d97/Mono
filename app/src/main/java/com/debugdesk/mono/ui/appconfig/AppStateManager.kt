package com.debugdesk.mono.ui.appconfig

import com.debugdesk.mono.utils.states.AlertState
import com.debugdesk.mono.utils.states.SnackBarData
import kotlinx.coroutines.flow.StateFlow

interface AppStateManager {
    val toastState: StateFlow<Int?>

    val toastStateString: StateFlow<String?>

    val alertState: StateFlow<AlertState>
    val snackBar: StateFlow<SnackBarData>

    fun showToastState(
        toastMsg: Int? = null,
        toastMsgString: String? = null,
    )

    fun updateAlertState(alertState: AlertState)

    fun showSnackBar(snackBarData: SnackBarData)

    fun hideAlertDialog()
}
