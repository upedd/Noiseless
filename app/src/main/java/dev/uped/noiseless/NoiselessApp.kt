package dev.uped.noiseless

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dev.uped.noiseless.navigation.AppNavHost
import dev.uped.noiseless.navigation.BottomNav
import dev.uped.noiseless.ui.component.PermissionRationale
import dev.uped.noiseless.util.PermissionHelper

@Composable
fun NoiselessApp(
    locationPermissionHelper: PermissionHelper,
    microphonePermissionHelper: PermissionHelper
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val context = LocalContext.current
    var showRationale by remember {
        mutableStateOf(false)
    }
    val sharedPreferences = remember {
        context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
    }
    // Change status bar
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = MaterialTheme.colors.isLight
    SideEffect {
        systemUiController.setSystemBarsColor(
            Color.Transparent,
            darkIcons = useDarkIcons,
            transformColorForLightContent = { Color.Black })
    }

    Scaffold(
        bottomBar = {
            BottomNav(
                route = backStackEntry?.destination?.route,
                onHomeClicked = {
                    navController.navigate("home") {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onMeasurementClicked = {
                    microphonePermissionHelper.requestPermission {
                        showRationale = true
                    }
                    if (microphonePermissionHelper.isPermissionGranted.value) {
                        navController.navigate("measure") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                onMeasurementListClicked = {
                    navController.navigate("measurements") {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        Box {
            AppNavHost(
                modifier = Modifier.padding(innerPadding),
                navController = navController,
                startDestination = if (sharedPreferences.getBoolean(
                        SEEN_ON_BOARDING_KEY,
                        false
                    )
                ) "home" else "onBoarding",
                locationPermissionHelper = locationPermissionHelper
            )
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
                    Text(text = stringResource(id = R.string.microphone_rationale))
                }
            }
        }

    }
}