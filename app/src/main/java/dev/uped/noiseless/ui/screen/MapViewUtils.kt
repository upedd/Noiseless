package dev.uped.noiseless.ui.screen

/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.os.Bundle
import androidx.annotation.FloatRange
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.ktx.addMarker
import com.google.maps.android.ktx.awaitMap
import dev.uped.noiseless.R
import kotlinx.coroutines.launch

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

fun GoogleMap.setZoom(
    @FloatRange(from = MinZoom.toDouble(), to = MaxZoom.toDouble()) zoom: Float
) {
    resetMinMaxZoomPreference()
    setMinZoomPreference(zoom)
    setMaxZoomPreference(zoom)
}

@Composable
fun MapViewContainer(
    map: MapView,
    latitude: String,
    longitude: String
) {
    val cameraPosition = remember(latitude, longitude) {
        LatLng(latitude.toDouble(), longitude.toDouble())
    }

    LaunchedEffect(map) {
        val googleMap = map.awaitMap()
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isZoomGesturesEnabled = true
        googleMap.addMarker { position(cameraPosition) }
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(cameraPosition))
    }

    var zoom by rememberSaveable(map) { mutableStateOf(InitialZoom) }
    ZoomControls(zoom) {
        zoom = it.coerceIn(MinZoom, MaxZoom)
    }

    val coroutineScope = rememberCoroutineScope()
    AndroidView({ map }) { mapView ->
        // Reading zoom so that AndroidView recomposes when it changes. The getMapAsync lambda
        // is stored for later, Compose doesn't recognize state reads
        //val mapZoom = zoom
        coroutineScope.launch {
            val googleMap = mapView.awaitMap()
            //googleMap.setZoom(mapZoom)
            // Move camera to the same place to trigger the zoom update
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(cameraPosition))
        }
    }
}

@Composable
private fun ZoomControls(
    zoom: Float,
    onZoomChanged: (Float) -> Unit
) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        ZoomButton("-", onClick = { onZoomChanged(zoom * 0.8f) })
        ZoomButton("+", onClick = { onZoomChanged(zoom * 1.2f) })
    }
}

@Composable
private fun ZoomButton(text: String, onClick: () -> Unit) {
    Button(
        modifier = Modifier.padding(8.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.onPrimary,
            contentColor = MaterialTheme.colors.primary
        ),
        onClick = onClick
    ) {
        Text(text = text, style = MaterialTheme.typography.h5)
    }
}

private const val InitialZoom = 5f
const val MinZoom = 2f
const val MaxZoom = 20f