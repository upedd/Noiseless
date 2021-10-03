package dev.uped.noiseless.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MeasurementMapScreen(longitude: String, latitude: String) {
    val mapView = rememberMapViewWithLifecycle()
    // #TODO store internally as string plsllsldls
    //Box(modifier = Modifier.fillMaxSize()) {
        MapViewContainer(mapView, latitude, longitude)
    //}
}