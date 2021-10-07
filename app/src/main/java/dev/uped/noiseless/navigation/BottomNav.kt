package dev.uped.noiseless.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues

@Composable
fun BottomNav(
    route: String?,
    onHomeClicked: () -> Unit,
    onMeasurementClicked: () -> Unit,
    onMeasurementListClicked: () -> Unit
) {
//    val navBackStackEntry by navController.currentBackStackEntryAsState()
//    val route = navBackStackEntry?.destination?.route
    if (route != null && route == "home" || route == "measurements") {
        BottomNavigation(
            modifier = Modifier.padding(
                rememberInsetsPaddingValues(
                    LocalWindowInsets.current.navigationBars
                )
            ),
        ) {
            BottomNavigationItem(
                icon = { Icon(Icons.Filled.Map, contentDescription = null) },
                label = { Text("Mapa") },
                selected = route == "home",
                onClick = {
                    onHomeClicked()
//                    navController.navigate("home") {
//                        // Pop up to the start destination of the graph to
//                        // avoid building up a large stack of destinations
//                        // on the back stack as users select items
//                        popUpTo(navController.graph.findStartDestination().id) {
//                            saveState = true
//                        }
//                        // Avoid multiple copies of the same destination when
//                        // reselecting the same item
//                        launchSingleTop = true
//                        // Restore state when reselecting a previously selected item
//                        restoreState = true
//                    }
                }
            )
            BottomNavigationItem(
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                label = { Text("Nowy Pomiar") },
                selected = false,
                onClick = {
                    onMeasurementClicked()
//                    microphonePermissionHelper.requestPermission {
//                        showRationale = true
//                    }
//                    if (microphonePermissionHelper.isPermissionGranted.value) {
//                        navController.navigate("measure") {
//                            // Pop up to the start destination of the graph to
//                            // avoid building up a large stack of destinations
//                            // on the back stack as users select items
//                            popUpTo(navController.graph.findStartDestination().id) {
//                                saveState = true
//                            }
//                            // Avoid multiple copies of the same destination when
//                            // reselecting the same item
//                            launchSingleTop = true
//                            // Restore state when reselecting a previously selected item
//                            restoreState = true
//                        }
//                    }
                }
            )
            BottomNavigationItem(
                icon = { Icon(Icons.Filled.List, contentDescription = null) },
                label = { Text("Pomiary") },
                selected = route == "measurements",
                onClick = {
                    onMeasurementListClicked()
//                    navController.navigate("measurements") {
//                        // Pop up to the start destination of the graph to
//                        // avoid building up a large stack of destinations
//                        // on the back stack as users select items
//                        popUpTo(navController.graph.findStartDestination().id) {
//                            saveState = true
//                        }
//                        // Avoid multiple copies of the same destination when
//                        // reselecting the same item
//                        launchSingleTop = true
//                        // Restore state when reselecting a previously selected item
//                        restoreState = true
//                    }
                }
            )
        }
    }
}