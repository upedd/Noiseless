package dev.uped.noiseless.ui.screen

import android.annotation.SuppressLint
import android.location.Geocoder
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.FusedLocationProviderClient
import dev.uped.noiseless.data.Measurement
import dev.uped.noiseless.ui.component.DBCountCircle
import dev.uped.noiseless.ui.theme.NoiselessTheme
import logcat.logcat
import java.util.*

@SuppressLint("MissingPermission")
@Composable
fun MeasurementResultScreen(dB: Double, onSave: (measurement: Measurement) -> Unit, locationClient: FusedLocationProviderClient?) {
    var locationa by remember {
        mutableStateOf("Loading location...")
    }
    var measurement by remember {
        mutableStateOf(Measurement(System.currentTimeMillis() / 1000, dB, null, null, null))
    }
    val context = LocalContext.current
    val geocoder = remember {
        Geocoder(context, Locale.getDefault())
    }

    LaunchedEffect(Unit) {
        val task = locationClient?.getCurrentLocation(100, null)
        task?.addOnSuccessListener { location ->
            // #FIXME handle if geocoder missing
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            locationa = addresses[0].getAddressLine(0)
            measurement = measurement.copy(latitude = location.latitude, longitude = location.longitude, location = addresses[0].getAddressLine(0))
        }
        task?.addOnFailureListener {
            // #FIXME handle failure and
            logcat { it.toString() }
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
        Text(text = locationa, style = MaterialTheme.typography.h6, color = MaterialTheme.colors.onBackground)
        Column {
            Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceAround) {
                Button(onClick = { /*TODO*/ }) {
                    Text(text = "Zapisz i udostępnij")
                }
                OutlinedButton(onClick = { onSave(measurement) }) {
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

        MeasurementResultScreen(dB = 70.0, onSave = {}, null)
    }
}