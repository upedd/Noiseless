package dev.uped.noiseless

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dev.uped.noiseless.data.DB
import dev.uped.noiseless.home.HomeScreen
import dev.uped.noiseless.ui.screen.*
import dev.uped.noiseless.ui.theme.NoiselessTheme

class MainActivity : ComponentActivity() {

    // Requesting permission to RECORD_AUDIO
    //private var permissionToRecordAccepted: Boolean = false


//    private val requestPermission =
//        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
//            permissionToRecordAccepted = it
//        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NoiselessTheme {
                NoiselessApp(
                )
            }
        }
    }
}

@Composable
fun NoiselessApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val route = navBackStackEntry?.destination?.route
            if (route != null && route == "home" || route == "measurements") {
                BottomNavigation {
                    BottomNavigationItem(
                        icon = { Icon(Icons.Filled.Map, contentDescription = null) },
                        label = { Text("Mapa") },
                        selected = route == "home",
                        onClick = {
                            navController.navigate("home") {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                    BottomNavigationItem(
                        icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                        label = { Text("Nowy Pomiar") },
                        selected = false,
                        onClick = {
                            navController.navigate("measure") {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                    BottomNavigationItem(
                        icon = { Icon(Icons.Filled.List, contentDescription = null) },
                        label = { Text("Pomiary") },
                        selected = route == "measurements",
                        onClick = {
                            navController.navigate("measurements") {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = "home") {
            composable("home") {
                HomeScreen(
                    modifier = Modifier.padding(innerPadding)
                )
            }
            composable("measure") {
                MeasurementScreen(
                    onMeasurementEnd = {
                        navController.navigate("measurementResult/${it}")
                    },
                    onCancelClicked = { navController.navigate("home") }
                )
            }
            composable(
                "measurementResult/{result}",
                arguments = listOf(navArgument("result") { type = NavType.StringType })
            ) { backStackEntry ->
                // #FIXME handle this pls
                MeasurementResultScreen(
                    backStackEntry.arguments?.getString("result")?.toDouble() ?: 0.0,
                    { navController.navigate("home") })
            }

            composable("measurements") {
                MeasurementsListScreen(onMeasurementClick = {
                    navController.navigate("measurementMap/${it.id}")
                })
            }

            composable(
                "measurementMap/{id}",
                listOf(
                    navArgument("id") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")
                if (id == null) {
                    SideEffect {
                        navController.navigate("home")
                        Toast.makeText(
                            context,
                            "Measurement id not passed to MeasurementMapScreen",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    MeasurementMapScreen(
                        measurementId = id.toLong(),
                        onExitClicked = { navController.navigate("home") })
                }
            }
        }
    }
}