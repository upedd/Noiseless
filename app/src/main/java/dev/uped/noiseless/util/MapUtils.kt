package dev.uped.noiseless.util

import android.location.Location
import android.os.Bundle
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.ktx.awaitMap
import dev.uped.noiseless.R
import kotlinx.coroutines.launch


// Source: https://github.com/android/compose-samples/blob/main/Crane/app/src/main/java/androidx/compose/samples/crane/details/MapViewUtils.kt
// Under Apache 2.0 License: https://github.com/android/compose-samples/blob/main/LICENSE
/**
 * Remembers a MapView and gives it the lifecycle of the current LifecycleOwner
 */
@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            id = R.id.map
        }
    }

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle, mapView) {
        // Make MapView follow the current lifecycle
        val lifecycleObserver = getMapLifecycleObserver(mapView)
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }

    return mapView
}

private fun getMapLifecycleObserver(mapView: MapView): LifecycleEventObserver =
    LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
            Lifecycle.Event.ON_START -> mapView.onStart()
            Lifecycle.Event.ON_RESUME -> mapView.onResume()
            Lifecycle.Event.ON_PAUSE -> mapView.onPause()
            Lifecycle.Event.ON_STOP -> mapView.onStop()
            Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
            else -> throw IllegalStateException()
        }
    }

/**
 * MapView container with automatic night mode styling
 */
@Composable
fun MapViewContainer(
    map: MapView,
    isDarkMode: Boolean = isSystemInDarkTheme()
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    AndroidView({ map }) {
        if (isDarkMode) {
            scope.launch {
                val googleMap = map.awaitMap()
                googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        context,
                        R.raw.maps_night_style_json
                    )
                )
            }
        }
    }
}

const val DEFAULT_ZOOM = 18f

fun GoogleMap.animateCameraToLocation(location: Location) {
    animateCamera(
        CameraUpdateFactory.newLatLngZoom(
            LatLng(
                location.latitude,
                location.longitude
            ), DEFAULT_ZOOM
        )
    )
}