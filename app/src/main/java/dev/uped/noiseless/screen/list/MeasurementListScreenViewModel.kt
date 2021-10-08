package dev.uped.noiseless.screen.list

import androidx.lifecycle.ViewModel
import dev.uped.noiseless.data.repository.MeasurementRepository

class MeasurementListScreenViewModel(private val repository: MeasurementRepository) : ViewModel() {
    suspend fun getMeasurements() = repository.getLocalMeasurements()
}