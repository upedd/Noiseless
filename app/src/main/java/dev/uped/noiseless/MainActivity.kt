package dev.uped.noiseless

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dev.uped.noiseless.data.DB
import dev.uped.noiseless.ui.screen.*
import dev.uped.noiseless.ui.theme.NoiselessTheme

class MainActivity : ComponentActivity() {

    // Requesting permission to RECORD_AUDIO
    private var permissionToRecordAccepted: Boolean = false


    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            permissionToRecordAccepted = it
        }


    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            NoiselessTheme {
                NoiselessApp(
                    onRequestMic = { requestPermission.launch(Manifest.permission.RECORD_AUDIO) },
                    micAllowed = permissionToRecordAccepted,
                    locationClient = fusedLocationClient
                )
            }
        }
    }
}

@Composable
fun NoiselessApp(
    onRequestMic: () -> Unit,
    micAllowed: Boolean,
    locationClient: FusedLocationProviderClient
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onRequestMic = onRequestMic,
                onMeasure = { navController.navigate("measure") },
                micAllowed = micAllowed,
                onGoToClick = { navController.navigate("measurements") },
                locationClient = locationClient
            )
        }
        composable("measure") {
            MeasurementScreen(onMeasurementEnd = {
                navController.navigate("measurementResult/${it}")
            })
        }
        composable(
            "measurementResult/{result}",
            arguments = listOf(navArgument("result") { type = NavType.StringType })
        ) { backStackEntry ->
            MeasurementResultScreen(
                backStackEntry.arguments?.getString("result")?.toDouble() ?: 0.0, {
                    DB.measurementQueries.insert(
                        it.timestamp,
                        it.loudness,
                        it.location,
                        it.longitude,
                        it.latitude
                    )
                    navController.navigate("home")
                }, locationClient = locationClient
            )
        }

        composable("measurements") {
            MeasurementsListScreen {
                navController.navigate("measurementMap/${it.latitude}/${it.longitude}")
            }
        }

        composable(
            "measurementMap/{latitude}/{longitude}",
            listOf(
                navArgument("latitude") { type = NavType.StringType },
                navArgument("longitude") { type = NavType.StringType })
        ) { backStackEntry ->
            MeasurementMapScreen(
                longitude = backStackEntry.arguments?.getString("longitude") ?: "0",
                latitude = backStackEntry.arguments?.getString("latitude") ?: "0"
            )
        }
    }
}