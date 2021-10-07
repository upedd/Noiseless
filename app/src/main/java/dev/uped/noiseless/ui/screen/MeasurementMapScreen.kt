package dev.uped.noiseless.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.insets.systemBarsPadding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.ktx.addMarker
import com.google.maps.android.ktx.awaitMap
import dev.uped.noiseless.data.measurement.repository.MeasurementRepository
import dev.uped.noiseless.model.Measurement
import dev.uped.noiseless.ui.theme.Green500
import dev.uped.noiseless.ui.theme.Red500
import dev.uped.noiseless.ui.theme.Yellow500
import dev.uped.noiseless.util.DEFAULT_ZOOM
import dev.uped.noiseless.util.MapViewContainer
import dev.uped.noiseless.util.createIconGeneratorForColor
import dev.uped.noiseless.util.rememberMapViewWithLifecycle
import org.koin.androidx.compose.getViewModel

class MeasurementMapScreenViewModel(private val measurementRepository: MeasurementRepository) :
    ViewModel() {
    suspend fun getMeasurementById(id: Long) = measurementRepository.getMeasurementById(id)
}

@Composable
fun MeasurementMapScreen(
    measurementId: Long,
    onExitClicked: () -> Unit,
    vm: MeasurementMapScreenViewModel = getViewModel()
) {
    val mapView = rememberMapViewWithLifecycle()
    val context = LocalContext.current
    var measurement by remember {
        mutableStateOf<Measurement?>(null)
    }

    LaunchedEffect(Unit) {

        measurement = vm.getMeasurementById(measurementId)

        if (measurement != null && measurement?.latitude != null && measurement?.longitude != null) {
            // Get icon
            val iconGenerator = when {
                measurement!!.loudness <= 40 -> createIconGeneratorForColor(Green500, context)
                measurement!!.loudness <= 70 -> createIconGeneratorForColor(Yellow500, context)
                else -> createIconGeneratorForColor(Red500, context)
            }
            val icon = iconGenerator.makeIcon(String.format("%.1f", measurement!!.loudness))
            val bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(icon)
            // Add marker
            val map = mapView.awaitMap()

            val position =
                LatLng(measurement!!.latitude!!.toDouble(), measurement!!.longitude!!.toDouble())

            map.addMarker {
                position(position)
                icon(bitmapDescriptor)
            }
            // Move camera to marker and animate
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, DEFAULT_ZOOM))
        }

    }

    MeasurementMapScreenContent(mapView = mapView, onExitClicked = onExitClicked)
}

@Composable
fun MeasurementMapScreenContent(mapView: MapView, onExitClicked: () -> Unit) {
    Box(Modifier.fillMaxSize()) {
        MapViewContainer(mapView)
        FloatingActionButton(
            modifier = Modifier
                .padding(16.dp)
                .padding(top = 16.dp)
                .align(Alignment.TopStart),
            onClick = onExitClicked,
        ) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close Map")
        }
    }
}