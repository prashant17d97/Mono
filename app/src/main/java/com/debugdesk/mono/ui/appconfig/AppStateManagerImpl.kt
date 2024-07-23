package com.debugdesk.mono.ui.appconfig

import com.debugdesk.mono.utils.states.AlertState
import com.debugdesk.mono.utils.states.SnackBarData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AppStateManagerImpl : AppStateManager {
    private val _toastState: MutableStateFlow<Int?> = MutableStateFlow(null)

    override val toastState: StateFlow<Int?> = _toastState

    private val _toastStateString: MutableStateFlow<String?> = MutableStateFlow(null)

    override val toastStateString: StateFlow<String?> = _toastStateString

    private val _alertState: MutableStateFlow<AlertState> = MutableStateFlow(AlertState.NONE)

    override val alertState: StateFlow<AlertState> = _alertState

    private val _snackBar: MutableStateFlow<SnackBarData> =
        MutableStateFlow(SnackBarData.defaultSnackBarData)
    override val snackBar: StateFlow<SnackBarData>
        get() = _snackBar

    override fun showToastState(
        toastMsg: Int?,
        toastMsgString: String?,
    ) {
        _toastState.tryEmit(toastMsg)
        _toastStateString.tryEmit(toastMsgString)
    }

    override fun updateAlertState(alertState: AlertState) {
        _alertState.tryEmit(alertState)
    }

    override fun hideAlertDialog() {
        _alertState.value = AlertState.NONE
    }

    override fun showSnackBar(snackBarData: SnackBarData) {
        _snackBar.tryEmit(snackBarData)
    }
}
