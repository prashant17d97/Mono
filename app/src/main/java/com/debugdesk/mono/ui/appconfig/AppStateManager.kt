package com.debugdesk.mono.ui.appconfig

import com.debugdesk.mono.utils.states.AlertState
import kotlinx.coroutines.flow.StateFlow

interface AppStateManager {

    val toastState: StateFlow<Int?>

    val toastStateString: StateFlow<String?>

    val alertState: StateFlow<AlertState>

    fun showToastState(toastMsg: Int? = null, toastMsgString: String? = null)


    fun updateAlertState(
        alertState: AlertState,
    )

    fun hideAlertDialog()


}