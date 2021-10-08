package dev.uped.noiseless.screen.result

import androidx.lifecycle.ViewModel
import dev.uped.noiseless.data.repository.MeasurementRepository
import dev.uped.noiseless.model.Measurement

class MeasurementResultViewModel(private val measurementRepository: MeasurementRepository) :
    ViewModel() {
    suspend fun save(measurement: Measurement) = measurementRepository.save(measurement)
    suspend fun saveAndShare(measurement: Measurement) =
        measurementRepository.saveAndShare(measurement)
}