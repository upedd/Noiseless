package dev.uped.noiseless.data.repository

import dev.uped.noiseless.model.Measurement

interface MeasurementRepository {
    suspend fun getSharedMeasurements(): List<Measurement>
    suspend fun getMeasurementById(id: Long): Measurement
    suspend fun getLocalMeasurements(): List<Measurement>
    suspend fun save(measurement: Measurement)
    suspend fun saveAndShare(measurement: Measurement)
}