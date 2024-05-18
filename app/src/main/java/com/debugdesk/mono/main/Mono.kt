package com.debugdesk.mono.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.debugdesk.mono.navigation.NavigationGraph
import com.debugdesk.mono.navigation.Screens
import com.debugdesk.mono.presentation.uicomponents.AlertStateDialog
import com.debugdesk.mono.presentation.uicomponents.PreviewTheme
import com.debugdesk.mono.ui.appconfig.defaultconfig.AppConfigProperties
import com.debugdesk.mono.ui.theme.MonoTheme
import com.debugdesk.mono.utils.Dp
import com.debugdesk.mono.utils.Dp.dp50
import com.debugdesk.mono.utils.Dp.dp60
import com.debugdesk.mono.utils.states.AlertState

@Composable
fun Mono(appConfigProperties: AppConfigProperties, alertState: AlertState) {
    val navHostController = rememberNavController()

    MonoTheme(appConfigProperties = appConfigProperties) {
        val showBnm = showBnm(navHostController)
        Scaffold(
            bottomBar = {
                if (showBnm) {
                    BottomNavigationView(navController = navHostController)
                }
            },
        ) {
            it.calculateTopPadding()
            Column(modifier = Modifier.padding(bottom = if (showBnm) 56.dp else 0.dp)) {
                NavigationGraph(navHostController)
            }
        }

        AlertStateDialog(alertState = alertState)
    }
}

@Composable
private fun showBnm(navHostController: NavHostController): Boolean {
    return when (currentRoute(navHostController)) {
        Screens.Input.route -> true
        Screens.Report.route -> true
        Screens.Graph.route -> true
        Screens.Setting.route -> true
        else -> false
    }
}

@Composable
private fun BottomNavigationView(navController: NavHostController) {
    val items = listOf(
        Screens.Report, Screens.Input, Screens.Graph, Screens.Setting
    )
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .height(dp60)

    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            NavigationBarItem(
                modifier = Modifier.height(dp50).padding(vertical = Dp.dp4),
                selected = currentRoute == item.route,
                colors = NavigationBarItemColors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    selectedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    unselectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = MaterialTheme.colorScheme.primary,
                    disabledIconColor = MaterialTheme.colorScheme.primary,
                    disabledTextColor = MaterialTheme.colorScheme.primary
                ),
                label = {
                    Text(
                        text = item.name, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                },
                onClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                inclusive = true
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        painterResource(id = item.selectedIcon.takeIf {
                            currentRoute == item.route
                        } ?: item.notSelectedIcon),
                        contentDescription = item.name,
                    )
                })
        }
    }
}

@Composable
private fun currentRoute(navHostController: NavHostController): String? {
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

@Preview
@Composable
fun NavBarPrev() {
    PreviewTheme {
        BottomNavigationView(navController = rememberNavController())
    }
}