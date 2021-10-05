package dev.uped.noiseless.data.measurement.datasource

import dev.uped.noiseless.model.Measurement

interface LocalDataSource {
    suspend fun getMeasurement(id: Long): Measurement
    suspend fun getMeasurements(): List<Measurement>
    suspend fun save(measurement: Measurement)
}