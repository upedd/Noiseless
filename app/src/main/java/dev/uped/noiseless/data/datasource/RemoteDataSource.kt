package dev.uped.noiseless.data.datasource

import dev.uped.noiseless.model.Measurement

interface RemoteDataSource {
    suspend fun getMeasurements(): List<Measurement>
    suspend fun save(measurement: Measurement)
}