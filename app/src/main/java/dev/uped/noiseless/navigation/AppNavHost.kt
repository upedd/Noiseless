package dev.uped.noiseless.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import dev.uped.noiseless.util.PermissionHelper
import dev.uped.noiseless.home.HomeScreen
import dev.uped.noiseless.ui.screen.*

@Composable
fun AppNavHost(
    modifier: Modifier,
    navController: NavHostController,
    startDestination: String,
    locationPermissionHelper: PermissionHelper
) {
    val context = LocalContext.current
    NavHost(
        navController = navController, startDestination = startDestination
    ) {
        composable("onBoarding") {
            OnBoardingScreen {
                navController.navigate("home") {
                    popUpTo("home")
                }
            }
        }
        composable("home") {
            HomeScreen(
                modifier = modifier,
                locationPermissionHelper = locationPermissionHelper
            )
        }
        composable("measure") {
            MeasurementScreen(
                onMeasurementEnd = {
                    navController.navigate("measurementResult/${it}") {
                        popUpTo("home")
                    }
                },
                onCancelClicked = { navController.navigate("home") },
                onStop = { navController.navigate("home") }
            )
        }
        composable(
            "measurementResult/{result}",
            arguments = listOf(navArgument("result") { type = NavType.StringType })
        ) { backStackEntry ->
            // #FIXME handle this pls
            MeasurementResultScreen(
                backStackEntry.arguments?.getString("result")?.toDouble() ?: 0.0,
                onAfterSave = {
                    navController.navigate("home") {
                        popUpTo("home")
                    }
                },
                onExit = {
                    navController.navigate("home") {
                        popUpTo("home")
                    }
                },
                locationPermissionHelper = locationPermissionHelper
            )
        }

        composable("measurements") {
            MeasurementsListScreen(
                modifier = modifier,
                onMeasurementClick = {
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
                    onExitClicked = {
                        navController.navigate("measurements") {
                            popUpTo("measurements")
                        }
                    })
            }
        }
    }
}