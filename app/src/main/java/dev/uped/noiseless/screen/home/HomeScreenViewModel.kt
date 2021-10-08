package dev.uped.noiseless.screen.home

import androidx.lifecycle.ViewModel
import dev.uped.noiseless.data.repository.MeasurementRepository

class HomeScreenViewModel(private val repository: MeasurementRepository) : ViewModel() {
    suspend fun getMeasurements() = repository.getSharedMeasurements()
}