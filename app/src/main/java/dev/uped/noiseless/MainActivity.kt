package dev.uped.noiseless

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dev.uped.noiseless.home.HomeScreen
import dev.uped.noiseless.ui.component.PermissionRationale
import dev.uped.noiseless.ui.screen.*
import dev.uped.noiseless.ui.theme.NoiselessTheme

class PermissionHelper(
    private val permission: String,
    private val activity: ComponentActivity // It's probably a bad idea
) {
    private var _isPermissionGranted = mutableStateOf(false)
    private val activityResultLauncher: ActivityResultLauncher<String> =
        activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            _isPermissionGranted.value = isGranted
        }
    val isPermissionGranted: State<Boolean> = _isPermissionGranted

    fun requestPermission(onShowRationale: () -> Unit) {
        when {
            ContextCompat.checkSelfPermission(
                activity.applicationContext,
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                _isPermissionGranted.value = true
            }
            shouldShowRequestPermissionRationale(activity, permission) -> {
                onShowRationale()
            }
            else -> {
                activityResultLauncher.launch(permission)
            }
        }
    }

    fun requestPermission() {
        if (ContextCompat.checkSelfPermission(
                activity.applicationContext,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            _isPermissionGranted.value = true
        } else {
            activityResultLauncher.launch(permission)
        }
    }

}

class MainActivity : ComponentActivity() {

    private val locationPermissionHelper =
        PermissionHelper(Manifest.permission.ACCESS_FINE_LOCATION, this)
    private val microphonePermissionHelper =
        PermissionHelper(Manifest.permission.RECORD_AUDIO, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            NoiselessTheme {
                NoiselessApp(
                    locationPermissionHelper,
                    microphonePermissionHelper
                )
            }
        }
    }
}

@Composable
fun NoiselessApp(
    locationPermissionHelper: PermissionHelper,
    microphonePermissionHelper: PermissionHelper
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = MaterialTheme.colors.isLight
    var showRationale by remember {
        mutableStateOf(false)
    }

    SideEffect {
        systemUiController.setSystemBarsColor(Color.Transparent, darkIcons = useDarkIcons)
    }
    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val route = navBackStackEntry?.destination?.route
            if (route != null && route == "home" || route == "measurements") {
                BottomNavigation(
                    backgroundColor = MaterialTheme.colors.surface.copy(alpha = 0.95f),
                    modifier = Modifier.padding(
                        rememberInsetsPaddingValues(
                            LocalWindowInsets.current.navigationBars
                        )
                    )
                ) {
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
                            microphonePermissionHelper.requestPermission {
                                showRationale = true
                            }
                            if (microphonePermissionHelper.isPermissionGranted.value) {
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
        Box() {
            NavHost(navController = navController, startDestination = "home") {
                composable("home") {
                    HomeScreen(
                        modifier = Modifier.padding(innerPadding),
                        locationPermissionHelper = locationPermissionHelper
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
                        { navController.navigate("home") },
                        locationPermissionHelper = locationPermissionHelper
                    )
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
            if (showRationale) {
                PermissionRationale(
                    modifier = Modifier.align(Alignment.Center),
                    onRationaleClose = {
                        showRationale = false
                    },
                    onRationaleSuccess = {
                        microphonePermissionHelper.requestPermission()
                        showRationale = false
                    }
                ) {
                    Text(text = "Żeby móc wykonać pomiar głośności wyraź zgodę na dostęp od mikrofonu")
                }
            }
        }

    }
}