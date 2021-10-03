package dev.uped.noiseless.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.CancellationToken
import logcat.logcat

@SuppressLint("MissingPermission")
@Composable
fun HomeScreen(
    onRequestMic: () -> Unit,
    onMeasure: () -> Unit,
    micAllowed: Boolean,
    onGoToClick: () -> Unit,
    locationClient: FusedLocationProviderClient
) {
    var test by remember {
        mutableStateOf("")
    }

    LaunchedEffect(Unit) {
        val task = locationClient.getCurrentLocation(100, null)
        task.addOnSuccessListener { location ->
            logcat { location.toString() }
            test = location.toString()
        }
        task.addOnFailureListener {
            logcat { it.toString() }
        }
    }

    Column {
        Button(onClick = onRequestMic) {
            Text(text = "Request microphone")
        }
        Text(text = if (micAllowed) "Microphone allowed" else "Microphone not allowed")
        Button(onClick = onMeasure) {
            Text(text = "Measure")
        }
        Button(onClick = onGoToClick) {
            Text("Go to list")
        }
        Text(test, style = MaterialTheme.typography.body1)
    }
}