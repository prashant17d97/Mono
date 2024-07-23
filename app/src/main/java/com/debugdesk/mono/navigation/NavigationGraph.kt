package com.debugdesk.mono.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.debugdesk.mono.presentation.addcategory.AddCategory
import com.debugdesk.mono.presentation.calendar.CalendarPage
import com.debugdesk.mono.presentation.editcategory.EditCategory
import com.debugdesk.mono.presentation.edittrans.EditTransaction
import com.debugdesk.mono.presentation.graph.Graph
import com.debugdesk.mono.presentation.input.Input
import com.debugdesk.mono.presentation.intro.Intro
import com.debugdesk.mono.presentation.report.Report
import com.debugdesk.mono.presentation.setting.Setting
import com.debugdesk.mono.presentation.setting.appearance.Appearance
import com.debugdesk.mono.presentation.setting.currency.Currency
import com.debugdesk.mono.presentation.setting.reminder.Reminder
import com.debugdesk.mono.presentation.splash.WelcomeScreen
import com.debugdesk.mono.utils.enums.ExpenseType

@Composable
fun NavigationGraph(navHostController: NavHostController) {
    NavHost(
        navController = navHostController,
        startDestination = Screens.WelcomeScreen.route,
    ) {
        composable(route = Screens.WelcomeScreen.route) {
            WelcomeScreen(navHostController)
        }
        composable(route = Screens.Intro.route) {
            Intro(navHostController)
        }
        composable(route = Screens.Input.route) {
            Input(navHostController)
        }
        composable(route = Screens.Report.route) {
            Report(navHostController)
        }
        composable(
            route = Screens.Graph.route,
            arguments =
            listOf(
                navArgument(Screens.GRAPH_ARGS) {
                    type = NavType.IntType
                    defaultValue = 0
                },
            ),
        ) {
            Graph(
                navHostController = navHostController,
                categoryId = it.extractIntArgument(Screens.GRAPH_ARGS) ?: 0,
            )
        }
        composable(route = Screens.Setting.route) {
            Setting(navHostController)
        }
        composable(
            route = Screens.EditCategory.route,
        ) {
            EditCategory(navHostController)
        }

        composable(
            route = Screens.EditTransaction.route,
            arguments =
            listOf(
                navArgument(Screens.EDIT_TRANSACTION_ARGS) {
                    type = NavType.IntType
                },
            ),
        ) {
            EditTransaction(
                navHostController = navHostController,
                transactionId = it.extractIntArgument(Screens.EDIT_TRANSACTION_ARGS),
            )
        }
        composable(
            route = Screens.AddCategory.route,
            arguments =
            listOf(
                navArgument(Screens.ADD_CATEGORY_ARGS) {
                    type = NavType.StringType
                },
            ),
        ) {
            AddCategory(
                navHostController,
                argument =
                it.extractStringArgument(Screens.ADD_CATEGORY_ARGS)
                    ?: ExpenseType.Expense.name,
            )
        }
        composable(route = Screens.Currency.route) {
            Currency(navHostController)
        }
        composable(route = Screens.Appearance.route) {
            Appearance(navHostController)
        }
        composable(route = Screens.Appearance.route) {
            Appearance(navHostController)
        }
        composable(route = Screens.Reminder.route) {
            Reminder(navHostController)
        }
        composable(route = Screens.CalendarPage.route) {
            CalendarPage(navHostController = navHostController)
        }
    }
}

private fun NavBackStackEntry.extractStringArgument(key: String): String? {
    return arguments?.getString(key)
}

private fun NavBackStackEntry.extractIntArgument(key: String): Int? {
    return arguments?.getInt(key)
}
