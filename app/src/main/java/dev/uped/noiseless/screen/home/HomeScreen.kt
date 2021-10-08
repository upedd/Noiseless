package dev.uped.noiseless.screen.home

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.MapView
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.ktx.awaitMap
import dev.uped.noiseless.R
import dev.uped.noiseless.service.location.LocationService
import dev.uped.noiseless.ui.component.PermissionRationale
import dev.uped.noiseless.util.*
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
    val isLocationEnabled = isLocationEnabled(context)

    val scaffoldState = rememberScaffoldState()

    fun showLocationDisabledSnackbar() {
        scope.launch {
            val result = scaffoldState.snackbarHostState.showSnackbar(
                context.getString(R.string.location_disabled),
                context.getString(R.string.settings),
                duration = SnackbarDuration.Long
            )
            if (result == SnackbarResult.ActionPerformed) {
                context.startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
        }
    }

    // Change my location status
    LaunchedEffect(locationPermissionHelper.isPermissionGranted.value, isLocationEnabled) {
        val googleMap = mapView.awaitMap()
        if (locationPermissionHelper.isPermissionGranted.value && isLocationEnabled) {
            googleMap.isMyLocationEnabled = true
            // Animate to user location
            val location = locationService.getCached()
            googleMap.animateCameraToLocation(location)
        } else {
            if (!isLocationEnabled) {
                showLocationDisabledSnackbar()
            }
            googleMap.isMyLocationEnabled = false
        }
    }

    LaunchedEffect(Unit) {
        val googleMap = mapView.awaitMap()

        // Disable default my location button as we have our own
        googleMap.uiSettings.isMyLocationButtonEnabled = false
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



    HomeScreenContent(
        modifier = modifier,
        onMyLocationClicked = {
            if (locationPermissionHelper.isPermissionGranted.value) {
                if (isLocationEnabled(context)) {
                    scope.launch {
                        val googleMap = mapView.awaitMap()
                        val location = locationService.getCached()
                        googleMap.animateCameraToLocation(location)
                    }
                } else {
                    showLocationDisabledSnackbar()
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
    scaffoldState: ScaffoldState
) {

    Scaffold(modifier = modifier, floatingActionButton = {
        FloatingActionButton(onClick = onMyLocationClicked) {
            Icon(painterResource(id = R.drawable.my_location), "My location")
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
                        text = stringResource(id = R.string.location_rationale_part1),
                        style = MaterialTheme.typography.body1
                    )
                    Text(
                        modifier = Modifier.padding(top = 16.dp),
                        text = stringResource(id = R.string.location_rationale_part2),
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}