package com.debugdesk.mono.navigation

import com.debugdesk.mono.R

sealed class Screens(
    val name: String,
    val selectedIcon: Int = 0,
    val notSelectedIcon: Int = 0,
    val route: String = name,
) {
    data object WelcomeScreen : Screens(name = WELCOME_SCREEN)

    data object Intro : Screens(name = INTRO)

    data object Input : Screens(
        name = INPUT,
        selectedIcon = R.drawable.input,
        notSelectedIcon = R.drawable.unselected_input,
    )

    data object Report : Screens(
        name = REPORT,
        selectedIcon = R.drawable.report,
        notSelectedIcon = R.drawable.unselected_report,
    )

    data object Graph : Screens(
        name = GRAPH,
        selectedIcon = R.drawable.ic_selected_graph,
        notSelectedIcon = R.drawable.ic_graph,
        route = "$GRAPH/{$GRAPH_ARGS}",
    ) {
        fun passCategoryId(categoryId: Int): String {
            return "$GRAPH/$categoryId"
        }
    }

    data object Setting : Screens(
        name = SETTINGS,
        selectedIcon = R.drawable.settings,
        notSelectedIcon = R.drawable.unselected_settings,
    )

    data object EditCategory : Screens(name = EDIT_CATEGORY)

    data object EditTransaction : Screens(
        name = EDIT_TRANSACTION,
        route = "$EDIT_TRANSACTION/{$EDIT_TRANSACTION_ARGS}",
    ) {
        fun passTransactionId(transactionId: Int): String {
            return "$EDIT_TRANSACTION/$transactionId"
        }
    }

    data object AddCategory : Screens(
        name = ADD_CATEGORY,
        route = "$ADD_CATEGORY/{$ADD_CATEGORY_ARGS}",
    ) {
        fun passAddCategoryArgs(stringArgs: String): String {
            return "$ADD_CATEGORY/$stringArgs"
        }
    }

    data object Currency : Screens(name = CURRENCY)

    data object Appearance : Screens(name = APPEARANCE)

    data object CalendarPage : Screens(name = CALENDAR_PAGE)

    data object Reminder : Screens(name = REMINDER)

    companion object {
        /**
         * Routes
         * */
        private const val WELCOME_SCREEN = "WelcomeScreen"
        private const val INTRO = "Intro"
        private const val INPUT = "Input"
        private const val REPORT = "Report"
        private const val GRAPH = "Graph"
        private const val SETTINGS = "Setting"
        private const val EDIT_CATEGORY = "EditCategory"
        private const val ADD_CATEGORY = "AddCategory"
        private const val CURRENCY = "Currency"
        private const val APPEARANCE = "Appearance"
        private const val REMINDER = "Reminder"
        private const val EDIT_TRANSACTION = "EditTransaction"
        private const val CALENDAR_PAGE = "CALENDAR_PAGE"

        /**
         * ARGUMENTS
         * */
        const val ADD_CATEGORY_ARGS = "AddCategoryArgs"
        const val EDIT_TRANSACTION_ARGS = "EDIT_TRANSACTION_Args"
        const val GRAPH_ARGS = "GRAPH_Args"
    }
}
