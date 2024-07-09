package com.debugdesk.mono.main

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
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
import com.debugdesk.mono.utils.Dp.dp0
import com.debugdesk.mono.utils.Dp.dp50
import com.debugdesk.mono.utils.Dp.dp56
import com.debugdesk.mono.utils.Dp.dp60
import com.debugdesk.mono.utils.states.AlertState

@Composable
fun Mono(
    appConfigProperties: AppConfigProperties,
    alertState: AlertState,
    targetScreen: String?
) {
    val navHostController = rememberNavController()

    LaunchedEffect(key1 = targetScreen) {
        Log.d("MainActivity", "Mono: $targetScreen")
        targetScreen?.let {
            navHostController.navigate(it)
        }
    }

    MonoTheme(appConfigProperties = appConfigProperties) {
        val showBnm = showBnm(navHostController)
        Scaffold(
            bottomBar = {
                if (showBnm) {
                    BottomNavigationView(navController = navHostController)
                }
            },
            floatingActionButton = {
                AnimatedVisibility(
                    visible = currentRoute(navHostController) == Screens.Report.route,
                    enter = slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessVeryLow
                        )
                    ) + scaleIn(
                        initialScale = 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessVeryLow
                        )
                    ),
                    exit = slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessVeryLow
                        )
                    ) + scaleOut(
                        targetScale = 0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessVeryLow
                        )
                    )
                ) {
                    FloatingActionButton(
                        shape = CircleShape,
                        onClick = { navHostController.navigate(Screens.Input.route) }) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "FAB"
                        )
                    }
                }

            }
        ) {
            it.calculateTopPadding()
            Column(modifier = Modifier.padding(bottom = if (showBnm) dp56 else dp0)) {
                NavigationGraph(navHostController)
            }
        }

        AlertStateDialog(alertState = alertState)
    }
}

@Composable
private fun showBnm(navHostController: NavHostController): Boolean {
    return when (currentRoute(navHostController)) {
        Screens.CalendarPage.route,
        Screens.Input.route,
        Screens.Report.route,
        Screens.Graph.route,
        Screens.Setting.route -> true

        else -> false
    }
}

@Composable
private fun BottomNavigationView(navController: NavHostController) {
    val items = listOf(
        Screens.Report, Screens.Input, Screens.Setting
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
                modifier = Modifier
                    .height(dp50)
                    .padding(vertical = Dp.dp4),
                selected = currentRoute == item.route,
                colors = NavigationBarItemColors(
                    selectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledIconColor = MaterialTheme.colorScheme.primary,
                    disabledTextColor = MaterialTheme.colorScheme.primary
                ),
                label = {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
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