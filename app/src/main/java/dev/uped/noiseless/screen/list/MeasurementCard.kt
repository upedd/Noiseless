package dev.uped.noiseless.screen.list

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import dev.uped.noiseless.R
import dev.uped.noiseless.model.Measurement
import dev.uped.noiseless.ui.component.getColorForLoudness
import java.text.SimpleDateFormat

@Composable
fun MeasurementCard(
    onMeasurementClick: (Measurement) -> Unit,
    measurement: Measurement,
    dateFormatter: SimpleDateFormat
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .clickable { onMeasurementClick(measurement) },
        border = if (!isSystemInDarkTheme()) BorderStroke(
            Dp.Hairline,
            MaterialTheme.colors.onSurface.copy(alpha = 0.3f)
        ) else null // Add border in light theme
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
                    .background(getColorForLoudness(measurement.loudness))
                    .zIndex(1.0f),
                contentAlignment = Alignment.Center

            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        String.format("%.1f", measurement.loudness),
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
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(
                    measurement.location ?: stringResource(id = R.string.unknown_location),
                    style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    dateFormatter.format(measurement.timestamp * 1000),
                    style = MaterialTheme.typography.body1,
                )
            }
        }
    }
}