package dev.uped.noiseless.ui.screen

import android.annotation.SuppressLint
import android.location.Location
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import dev.uped.noiseless.model.Measurement
import dev.uped.noiseless.data.measurement.repository.MeasurementRepository
import dev.uped.noiseless.service.geocoding.GeocodingService
import dev.uped.noiseless.service.location.LocationService
import dev.uped.noiseless.ui.component.DBCountCircle
import dev.uped.noiseless.ui.theme.NoiselessTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel

class MeasurementResultViewModel(private val measurementRepository: MeasurementRepository) :
    ViewModel() {
    suspend fun save(measurement: Measurement) = measurementRepository.save(measurement)
    suspend fun saveAndShare(measurement: Measurement) =
        measurementRepository.saveAndShare(measurement)
}

@SuppressLint("MissingPermission")
@Composable
fun MeasurementResultScreen(
    dB: Double,
    onAfterSave: () -> Unit,
    locationService: LocationService = get(),
    geocodingService: GeocodingService = get(),
    vm: MeasurementResultViewModel = getViewModel()
) {
    val scope = rememberCoroutineScope()
    var displayLocation by remember {
        mutableStateOf("Unknown")
    }
    var location by remember {
        mutableStateOf<Location?>(null)
    }
//    var isLocationReady by remember {
//        mutableStateOf(false)
//    }

    LaunchedEffect(Unit) {
        location = try {
            locationService.getCurrent()
        } catch (e: Exception) {
            null
        }
        if (location != null) {
            displayLocation = try {
                val address = geocodingService.getFromLocation(location!!)
                address?.getAddressLine(0) ?: "${location!!.latitude} ${location!!.longitude}"
            } catch (e: Exception) {
                "${location!!.latitude} ${location!!.longitude}"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {
        Text(
            "Twój wynik pomiaru głósności to: ",
            style = MaterialTheme.typography.h6,
            color = MaterialTheme.colors.onBackground.copy(alpha = 0.9f)
        )

        DBCountCircle(dB = dB, isActive = false)
        Text(
            text = displayLocation,
            style = MaterialTheme.typography.h6,
            color = MaterialTheme.colors.onBackground
        )
        Column {
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewMeasurementResultScreen() {
    NoiselessTheme {

        MeasurementResultScreen(dB = 70.0, {})
    }
}