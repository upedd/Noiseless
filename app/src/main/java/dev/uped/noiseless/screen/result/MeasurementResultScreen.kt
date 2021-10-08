package dev.uped.noiseless.screen.result

import android.content.Intent
import android.location.Location
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.uped.noiseless.R
import dev.uped.noiseless.model.Measurement
import dev.uped.noiseless.service.geocoding.GeocodingService
import dev.uped.noiseless.service.location.LocationService
import dev.uped.noiseless.ui.component.DBCountCircle
import dev.uped.noiseless.util.PermissionHelper
import dev.uped.noiseless.util.isLocationEnabled
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel

@Composable
fun MeasurementResultScreen(
    dB: Double,
    onAfterSave: () -> Unit,
    onExit: () -> Unit,
    locationPermissionHelper: PermissionHelper,
    locationService: LocationService = get(),
    geocodingService: GeocodingService = get(),
    vm: MeasurementResultViewModel = getViewModel()
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var displayLocation by remember {
        mutableStateOf(context.getString(R.string.unknown_location))
    }
    var location by remember {
        mutableStateOf<Location?>(null)
    }
    var locationState by remember {
        mutableStateOf(LocationState.LOADING)
    }
    val isLocationEnabled = isLocationEnabled(context)

    suspend fun refreshLocation() {
        locationState = LocationState.LOADING
        when {
            !locationPermissionHelper.isPermissionGranted.value -> {
                locationState = LocationState.PERMISSION_MISSING
            }
            !isLocationEnabled(context) -> {
                locationState = LocationState.LOCATION_DISABLED
            }
            else -> {
                location = try {
                    locationService.getCurrent()
                } catch (e: Exception) {
                    null
                }
                if (location != null) {
                    locationState = LocationState.READY
                    displayLocation =
                        try {
                            val address = geocodingService.getFromLocation(location!!)
                            address?.getAddressLine(0)
                        } catch (e: Exception) {
                            null
                        }
                            ?: "${location!!.latitude} ${location!!.longitude}" // Fallback to displaying coordinates

                } else {
                    locationState = LocationState.UNKNOWN
                }
            }
        }
    }



    LaunchedEffect(locationPermissionHelper.isPermissionGranted.value, isLocationEnabled) {
        refreshLocation()
    }

    val locationText = when {
        locationState == LocationState.READY -> displayLocation
        locationState.displayString != null -> stringResource(id = locationState.displayString!!)
        else -> ""
    }

    MeasurementResultScreenContent(
        locationText = locationText,
        locationAction = {
            when (locationState) {
                LocationState.LOADING -> CircularProgressIndicator()
                LocationState.PERMISSION_MISSING -> Button(onClick = { locationPermissionHelper.requestPermission() }) {
                    Text(text = stringResource(id = R.string.allow))
                }
                LocationState.LOCATION_DISABLED -> IconButton(onClick = {
                    context.startActivity(
                        Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    )
                }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings"
                    )
                }
                else -> IconButton(onClick = { scope.launch { refreshLocation() } }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh"
                    )
                }
            }
        },
        measurementAction = {
            if (locationState != LocationState.READY) {
                Button(onClick = {
                    scope.launch {
                        vm.save(
                            Measurement(
                                loudness = dB,
                                location = null,
                                longitude = null,
                                latitude = null,
                                timestamp = System.currentTimeMillis() / 1000
                            )
                        )
                    }
                    onAfterSave()
                }) {
                    Text(text = stringResource(id = R.string.save_without_location))
                }
            } else {
                MeasurementResultActions(
                    onSaveAndShare = {
                        scope.launch {
                            vm.saveAndShare(
                                Measurement(
                                    loudness = dB,
                                    location = displayLocation,
                                    longitude = location?.longitude?.toString(),
                                    latitude = location?.latitude?.toString(),
                                    timestamp = System.currentTimeMillis() / 1000
                                )
                            )
                        }
                        onAfterSave()
                    },
                    onSave = {
                        scope.launch {
                            vm.save(
                                Measurement(
                                    loudness = dB,
                                    location = displayLocation,
                                    longitude = location?.longitude?.toString(),
                                    latitude = location?.latitude?.toString(),
                                    timestamp = System.currentTimeMillis() / 1000
                                )
                            )
                        }
                        onAfterSave()
                    }
                )
            }
        },
        onExit,
        dB,
    )

}

@Composable
private fun MeasurementResultScreenContent(
    locationText: String,
    locationAction: @Composable () -> Unit,
    measurementAction: @Composable () -> Unit,
    onExit: () -> Unit,
    dB: Double
) {
//    Column {

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FloatingActionButton(
                onClick = onExit,
                backgroundColor = MaterialTheme.colors.surface,
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
            }
            Text(
                stringResource(id = R.string.measurement_result),
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.9f)
            )
        }



        DBCountCircle(dB = dB, isActive = false)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                Modifier.padding(bottom = 32.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    text = locationText,
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.onBackground,
                    modifier = Modifier.fillMaxWidth(0.7f)
                )
                locationAction()
            }

            measurementAction()
        }
    }
    //}
}