package dev.uped.noiseless.screen.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.uped.noiseless.model.Measurement
import org.koin.androidx.compose.getViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MeasurementsListScreen(
    modifier: Modifier,
    onMeasurementClick: (Measurement) -> Unit,
    vm: MeasurementListScreenViewModel = getViewModel()
) {
    val measurements = remember {
        mutableStateListOf<Measurement>()
    }

    LaunchedEffect(Unit) {
        measurements.addAll(vm.getMeasurements())
    }
    MeasurementsListContent(modifier, measurements, onMeasurementClick)
}

@Composable
fun MeasurementsListContent(
    modifier: Modifier,
    measurements: List<Measurement>,
    onMeasurementClick: (Measurement) -> Unit,
    dateFormatter: SimpleDateFormat = SimpleDateFormat("HH:mm, d MMMM yyyy", Locale.getDefault())
) {

    LazyColumn(
        modifier = modifier
            .padding(16.dp)
            .padding(top = 16.dp) // Status bar
    ) {
        items(measurements) {
            MeasurementCard(
                onMeasurementClick = onMeasurementClick,
                measurement = it,
                dateFormatter = dateFormatter
            )
        }
    }
}