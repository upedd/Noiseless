package dev.uped.noiseless.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.MapView
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.ktx.awaitMap
import dev.uped.noiseless.service.location.LocationService
import dev.uped.noiseless.util.MapViewContainer
import dev.uped.noiseless.util.animateCameraToLocation
import dev.uped.noiseless.util.rememberMapViewWithLifecycle
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel

@SuppressLint("MissingPermission")
@Composable
fun HomeScreen(
    modifier: Modifier,
    locationService: LocationService = get(),
    vm: HomeScreenViewModel = getViewModel()
) {
    val mapView = rememberMapViewWithLifecycle()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val googleMap = mapView.awaitMap()

        // Setup marker clustering
        val clusterManager = ClusterManager<MeasurementClusterItem>(context, googleMap)
        clusterManager.renderer = MeasurementClusterRenderer(context, googleMap, clusterManager)
        googleMap.setOnCameraIdleListener(clusterManager)

        // Setup my location
        googleMap.isMyLocationEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = false

        // Animate to user location
        launch {
            val location = locationService.getCached()
            googleMap.animateCameraToLocation(location)
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

    HomeScreenContent(modifier = modifier, onMyLocationClicked = {
        scope.launch {
            val googleMap = mapView.awaitMap()
            val location = locationService.getCached()
            googleMap.animateCameraToLocation(location)
        }
    }, mapView = mapView)
}

@Composable
fun HomeScreenContent(modifier: Modifier, onMyLocationClicked: () -> Unit, mapView: MapView) {
    Scaffold(modifier = modifier, floatingActionButton = {
        FloatingActionButton(onClick = onMyLocationClicked) {
            Icon(Icons.Default.MyLocation, "My location")
        }
    }) {
        MapViewContainer(map = mapView)
    }
}