package dev.uped.noiseless.home

import androidx.lifecycle.ViewModel
import dev.uped.noiseless.data.measurement.repository.MeasurementRepository

class HomeScreenViewModel(private val repository: MeasurementRepository) : ViewModel() {
    suspend fun getMeasurements() = repository.getSharedMeasurements()
}