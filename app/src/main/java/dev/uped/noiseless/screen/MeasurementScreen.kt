package dev.uped.noiseless.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import dev.uped.noiseless.DBMeter
import dev.uped.noiseless.R
import dev.uped.noiseless.ui.component.DBCountCircle
import dev.uped.noiseless.ui.theme.NoiselessTheme
import kotlinx.coroutines.delay


@Composable
fun MeasurementScreen(
    onMeasurementEnd: (averageLoudness: Double) -> Unit,
    onCancelClicked: () -> Unit,
    onStop: () -> Unit
) {
    val measurements = remember {
        mutableListOf<Double>()
    }

    var db by remember { mutableStateOf(0.0) }
    var dbMeter: DBMeter? by remember { mutableStateOf(null) }
    DisposableEffect(Unit) {
        dbMeter = DBMeter {
            db = it
            measurements.add(it)
        }
        dbMeter!!.start()
        onDispose {
            dbMeter?.destroy()
        }
    }

    // Subscribe to lifecycle events to navigate out of measuring screen when users f.e. switches apps
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                onStop()
            }
        }
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }

    LaunchedEffect(Unit) {
        delay(10000)
        onMeasurementEnd(measurements.average())
    }

    MeasurementScreenContent(db, onCancelClicked)
}

@Composable
fun MeasurementScreenContent(dB: Double, onCancelClicked: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {
        Text(
            stringResource(id = R.string.measuring),
            style = MaterialTheme.typography.h6,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.9f)
        )

        DBCountCircle(dB = dB, isActive = true)

        Button(onClick = onCancelClicked) {
            Text(stringResource(id = R.string.cancel))
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewMeasurementScreen() {
    NoiselessTheme {
        MeasurementScreenContent(70.0) {}
    }
}