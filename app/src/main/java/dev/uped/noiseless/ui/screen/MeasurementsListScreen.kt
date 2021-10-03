package dev.uped.noiseless.ui.screen

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import dev.uped.noiseless.data.DB
import dev.uped.noiseless.data.Measurement
import dev.uped.noiseless.ui.component.getColorForLoudness
import dev.uped.noiseless.ui.theme.NoiselessTheme
import java.time.Instant
import java.util.*

@Composable
fun MeasurementsListScreen(onMeasurementClick: (Measurement) -> Unit) {
    val measurements = remember {
        mutableStateListOf<Measurement>()
    }

    LaunchedEffect(Unit) {
        measurements.addAll(DB.measurementQueries.selectAllByDate().executeAsList())
    }
    MeasurementsListContent(measurements, onMeasurementClick)
}

@SuppressLint("NewApi")
@Composable
fun MeasurementsListContent(
    measurements: List<Measurement>,
    onMeasurementClick: (Measurement) -> Unit
) {
    val dateFormatter = SimpleDateFormat("H:m:s EEEE d MMMM, y", Locale.getDefault())
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(measurements) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .clickable {onMeasurementClick(it)}
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(
                                CircleShape
                            )
                            .background(getColorForLoudness(it.loudness))
                            .zIndex(1.0f),
                        contentAlignment = Alignment.Center

                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                String.format("%.1f", it.loudness),
                                color = MaterialTheme.colors.onPrimary,
                                style = MaterialTheme.typography.h4.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                "dB",
                                color = MaterialTheme.colors.onPrimary.copy(alpha = 0.8f),
                                style = MaterialTheme.typography.body1
                            )
                        }
                    }
                    Column( modifier = Modifier.padding(start = 16.dp)) {
                        // #TODO display coordinates if location is unknown
                        Text(it.location ?: "Unknown", style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold))
                        Text(
                            dateFormatter.format(Date.from(Instant.ofEpochSecond(it.timestamp))),
                            style = MaterialTheme.typography.body1,

                            )
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewMeasurementsListScreen() {
    NoiselessTheme {
        MeasurementsListContent(
            listOf(
                Measurement(1633186358, 70.0, null, null, null),
            )
        ) {}
    }
}

