package dev.uped.noiseless.ui.screen

import android.annotation.SuppressLint
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import dev.uped.noiseless.util.PermissionHelper
import dev.uped.noiseless.data.measurement.repository.MeasurementRepository
import dev.uped.noiseless.model.Measurement
import dev.uped.noiseless.service.geocoding.GeocodingService
import dev.uped.noiseless.service.location.LocationService
import dev.uped.noiseless.ui.component.DBCountCircle
import dev.uped.noiseless.util.isLocationEnabled
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel

class MeasurementResultViewModel(private val measurementRepository: MeasurementRepository) :
    ViewModel() {
    suspend fun save(measurement: Measurement) = measurementRepository.save(measurement)
    suspend fun saveAndShare(measurement: Measurement) =
        measurementRepository.saveAndShare(measurement)
}

enum class LocationState(val displayString: String) {
    LOADING("Ładownie lokalizacji "),
    PERMISSION_MISSING("Brak zgody na korzystanie z lokalizacji"),
    LOCATION_DISABLED("Lokalizacja wyłączona"),
    UNKNOWN("Nie znaleziono lokalizacji"),
    READY("")
}

@SuppressLint("MissingPermission")
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
        mutableStateOf("Nieznana lokalizacja")
    }
    var location by remember {
        mutableStateOf<Location?>(null)
    }
    var locationState by remember {
        mutableStateOf(LocationState.LOADING)
    }

    suspend fun fetchLocation() {
        locationState = LocationState.LOADING
        if (!locationPermissionHelper.isPermissionGranted.value) {
            locationState = LocationState.PERMISSION_MISSING
        } else if (!isLocationEnabled(context)) {
            locationState = LocationState.LOCATION_DISABLED
        } else {
            location = try {
                locationService.getCurrent()
            } catch (e: Exception) {
                null
            }
            if (location != null) {
                locationState = LocationState.READY
                displayLocation = try {
                    val address = geocodingService.getFromLocation(location!!)
                    address?.getAddressLine(0) ?: "${location!!.latitude} ${location!!.longitude}"
                } catch (e: Exception) {
                    "${location!!.latitude} ${location!!.longitude}"
                }
            } else {
                locationState = LocationState.UNKNOWN
            }
        }

    }

    LaunchedEffect(locationPermissionHelper.isPermissionGranted.value) {
        fetchLocation()
    }


    Column {
        FloatingActionButton(onClick = onExit, Modifier.padding(top = 48.dp, start = 16.dp)) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
        }
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 48.dp, top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {

            Text(
                "Twój wynik pomiaru głósności to: ",
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.onBackground.copy(alpha = 0.9f)
            )

            DBCountCircle(dB = dB, isActive = false)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    Modifier.padding(bottom = 32.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text(
                        text = if (locationState == LocationState.READY) displayLocation else locationState.displayString,
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.onBackground,
                        modifier = Modifier.fillMaxWidth(0.7f)
                    )
                    when (locationState) {
                        LocationState.LOADING -> CircularProgressIndicator()
                        LocationState.PERMISSION_MISSING -> Button(onClick = { locationPermissionHelper.requestPermission() }) {
                            Text(text = "Zezwól")
                        }
                        LocationState.LOCATION_DISABLED -> IconButton(onClick = {
                            locationState = LocationState.LOADING
                            context.startActivity(
                                Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                            )
                        }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings"
                            )
                        }
                        else -> IconButton(onClick = { scope.launch { fetchLocation() } }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh"
                            )
                        }
                    }
                }

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
                        Text(text = "Zapisz bez lokalizacji")
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Button(onClick = {
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
                        }) {
                            Text(text = "Zapisz i udostępnij")
                        }
                        OutlinedButton(onClick = {
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
                        }) {
                            Text(text = "Zapisz")
                        }
                    }
                    Text(
                        text = "Udostępniając wynik zgadzasz się na przekazanie twojej lokalizacji. Lokalizacja ta będzie widoczna również dla innych użytkowników aplikacji.",
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onBackground.copy(0.7f),
                        modifier = Modifier.padding(top = 32.dp)
                    )
                }
            }
        }
    }

}