package com.debugdesk.mono.navigation

import com.debugdesk.mono.R


sealed class Screens(
    val name: String,
    val selectedIcon: Int = 0,
    val notSelectedIcon: Int = 0,
    val route: String = name
) {
    data object WelcomeScreen : Screens(name = welcomeScreen)

    data object Intro : Screens(name = intro)
    data object Input : Screens(
        name = input,
        selectedIcon = R.drawable.input,
        notSelectedIcon = R.drawable.unselected_input,
    )

    data object Report : Screens(
        name = report,
        selectedIcon = R.drawable.report,
        notSelectedIcon = R.drawable.unselected_report,
    )

    data object Graph : Screens(
        name = graph,
        selectedIcon = R.drawable.ic_selected_graph,
        notSelectedIcon = R.drawable.ic_graph,
    )

    data object Setting : Screens(
        name = setting,
        selectedIcon = R.drawable.settings,
        notSelectedIcon = R.drawable.unselected_settings,
    )

    data object EditCategory : Screens(name = editCategory)

    data object EditTransaction : Screens(
        name = EDIT_TRANSACTION,
        route = "$EDIT_TRANSACTION/{$EDIT_TRANSACTION_ARGS}"
    ) {
        fun passTransactionId(transactionId: Int): String {
            return "$EDIT_TRANSACTION/$transactionId"
        }
    }

    data object AddCategory : Screens(
        name = addCategory,
        route = "$addCategory/{$AddCategoryArgs}"
    ) {
        fun passAddCategoryArgs(stringArgs: String): String {
            return "$addCategory/$stringArgs"
        }
    }

    data object Currency : Screens(name = currency)

    data object Appearance : Screens(name = appearance)

    data object CalendarPage : Screens(name = CALENDAR_PAGE)

    data object Reminder : Screens(name = reminder)


    companion object {
        /**
         * Routes
         * */
        private const val welcomeScreen = "WelcomeScreen"
        private const val intro = "Intro"
        private const val input = "Input"
        private const val report = "Report"
        private const val graph = "Graph"
        private const val setting = "Setting"
        private const val editCategory = "EditCategory"
        private const val addCategory = "AddCategory"
        private const val currency = "Currency"
        private const val appearance = "Appearance"
        private const val reminder = "Reminder"
        private const val EDIT_TRANSACTION = "EditTransaction"
        private const val CAMERA = "Camera"
        private const val CALENDAR_PAGE = "CALENDAR_PAGE"

        /**
         * ARGUMENTS
         * */
        const val AddCategoryArgs = "AddCategoryArgs"
        const val EDIT_TRANSACTION_ARGS = "EDIT_TRANSACTION_Args"

    }

}
