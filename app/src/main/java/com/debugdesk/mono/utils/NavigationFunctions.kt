package com.debugdesk.mono.utils

import androidx.navigation.NavHostController
import com.debugdesk.mono.navigation.Screens

object NavigationFunctions {
    fun NavHostController.navigateTo(screens: Screens) {
        navigate(screens.route)
    }

    fun NavHostController.navigateTo(route: String) {
        navigate(route)
    }
}