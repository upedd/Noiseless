package dev.uped.noiseless.home

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.MapView
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.ktx.awaitMap
import dev.uped.noiseless.util.PermissionHelper
import dev.uped.noiseless.service.location.LocationService
import dev.uped.noiseless.ui.component.PermissionRationale
import dev.uped.noiseless.util.MapViewContainer
import dev.uped.noiseless.util.animateCameraToLocation
import dev.uped.noiseless.util.isLocationEnabled
import dev.uped.noiseless.util.rememberMapViewWithLifecycle
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel

@SuppressLint("MissingPermission")
@Composable
fun HomeScreen(
    modifier: Modifier,
    locationPermissionHelper: PermissionHelper,
    locationService: LocationService = get(),
    vm: HomeScreenViewModel = getViewModel(),
) {
    val mapView = rememberMapViewWithLifecycle()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var showRationale by remember {
        mutableStateOf(false)
    }
    val isLocationEnabled = remember {
        isLocationEnabled(context)
    }

    // Change my location status
    LaunchedEffect(locationPermissionHelper.isPermissionGranted.value) {
        val googleMap = mapView.awaitMap()
        if (locationPermissionHelper.isPermissionGranted.value && isLocationEnabled) {
            if (!googleMap.isMyLocationEnabled) {
                googleMap.isMyLocationEnabled = true
                googleMap.uiSettings.isMyLocationButtonEnabled = false
            }
            // Animate to user location
            launch {
                val location = locationService.getCached()
                googleMap.animateCameraToLocation(location)
            }
        } else {
            if (googleMap.isMyLocationEnabled) {
                googleMap.isMyLocationEnabled = false
                googleMap.uiSettings.isMyLocationButtonEnabled = false
            }
        }
    }

    LaunchedEffect(Unit) {
        val googleMap = mapView.awaitMap()
        // Setup marker clustering
        val clusterManager = ClusterManager<MeasurementClusterItem>(context, googleMap)
        clusterManager.renderer = MeasurementClusterRenderer(context, googleMap, clusterManager)
        googleMap.setOnCameraIdleListener(clusterManager)

        // Request location
        locationPermissionHelper.requestPermission {
            showRationale = true
        }

        // Get and add measurements to map
        val measurements = vm.getMeasurements()
        clusterManager.addItems(
            measurements
                .filter { it.latitude != null && it.longitude != null }
                .map {
                    return@map MeasurementClusterItem(
                        it.loudness,
                        it.latitude!!.toDouble(),
                        it.longitude!!.toDouble()
                    )
                }
        )
        clusterManager.cluster()
    }

    val scaffoldState = rememberScaffoldState()

    HomeScreenContent(
        modifier = modifier,
        onMyLocationClicked = {
            if (locationPermissionHelper.isPermissionGranted.value) {
                if (isLocationEnabled) {
                    scope.launch {
                        val googleMap = mapView.awaitMap()
                        val location = locationService.getCached()
                        googleMap.animateCameraToLocation(location)
                    }
                } else {
                    scope.launch {
                        // #TODO extract to function
                        val result = scaffoldState.snackbarHostState.showSnackbar(
                            "Lokalizacja jest wyłączona",
                            "Ustawienia",
                            duration = SnackbarDuration.Indefinite
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            context.startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                        }
                    }
                }

            } else {
                showRationale = true
            }
        },
        mapView = mapView,
        showRationale = showRationale,
        onRationaleSuccess = {
            locationPermissionHelper.requestPermission()
            showRationale = false
        },
        onRationaleClose = {
            showRationale = false
        },
        isLocationEnabled,
        scaffoldState
    )
}

@Composable
fun HomeScreenContent(
    modifier: Modifier,
    onMyLocationClicked: () -> Unit,
    mapView: MapView,
    showRationale: Boolean,
    onRationaleSuccess: () -> Unit,
    onRationaleClose: () -> Unit,
    isLocationEnabled: Boolean,
    scaffoldState: ScaffoldState
) {

    val context = LocalContext.current

    LaunchedEffect(isLocationEnabled) {
        if (!isLocationEnabled) {
            val result = scaffoldState.snackbarHostState.showSnackbar(
                "Lokalizacja jest wyłączona",
                "Ustawienia",
                duration = SnackbarDuration.Indefinite
            )
            if (result == SnackbarResult.ActionPerformed) {
                context.startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
        }
    }

    Scaffold(modifier = modifier, floatingActionButton = {
        FloatingActionButton(onClick = onMyLocationClicked) {

            Icon(Icons.Default.MyLocation, "My location")
        }
    }, scaffoldState = scaffoldState) {
        Box(modifier = Modifier.fillMaxSize()) {
            MapViewContainer(map = mapView)

            if (showRationale) {
                PermissionRationale(
                    modifier = Modifier.align(Alignment.Center),
                    onRationaleClose = onRationaleClose,
                    onRationaleSuccess = onRationaleSuccess
                ) {
                    Text(
                        text = "Żeby móc korzystać ze wszystkich funkcji aplikacji, wyraź zgodę na korzystanie z lokalizacji.",
                        style = MaterialTheme.typography.body1
                    )
                    Text(
                        modifier = Modifier.padding(top = 16.dp),
                        text =
                        "Zgoda ta potrzebna jest do pokazywanie twojej lokalizacji na mapie i przy udostępnianiu wyników.",
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}