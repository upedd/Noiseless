package dev.uped.noiseless.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.uped.noiseless.DBMeter
import dev.uped.noiseless.ui.component.DBCountCircle
import dev.uped.noiseless.ui.theme.NoiselessTheme
import kotlinx.coroutines.delay

class MeasurementScreenViewModel : ViewModel() {
    private val measurements = mutableListOf<Double>()

    fun addMeasurement(measurement: Double) = measurements.add(measurement)
    fun getAverage() = measurements.average()
}

@Composable
fun MeasurementScreen(
    onMeasurementEnd: (averageLoudness: Double) -> Unit,
    onCancelClicked: () -> Unit,
    viewModel: MeasurementScreenViewModel = viewModel()
) {
    var db by remember { mutableStateOf(0.0) }
    var dbMeter: DBMeter? by remember { mutableStateOf(null) }
    DisposableEffect(Unit) {
        dbMeter = DBMeter {
            db = it
            viewModel.addMeasurement(it)
        }
        dbMeter!!.start()
        onDispose {
            dbMeter?.destroy()
        }
    }

    LaunchedEffect(Unit) {
        delay(10000)
        onMeasurementEnd(viewModel.getAverage())
    }

    MeasurementContent(db, onCancelClicked)
}

@Composable
fun MeasurementContent(dB: Double, onCancelClicked: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {
        Text(
            "Mierzenie poziomu głośności...",
            style = MaterialTheme.typography.h6,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.9f)
        )

        DBCountCircle(dB = dB, isActive = true)

        OutlinedButton(onClick = onCancelClicked) {
            Text("Przerwij")
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewMeasurementScreen() {
    NoiselessTheme {
        MeasurementContent(70.0) {}
    }
}