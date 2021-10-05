package dev.uped.noiseless.data.measurement.repository

import dev.uped.noiseless.data.measurement.datasource.LocalDataSource
import dev.uped.noiseless.data.measurement.datasource.RemoteDataSource
import dev.uped.noiseless.model.Measurement

class MeasurementRepositoryImpl(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : MeasurementRepository {
    override suspend fun getSharedMeasurements(): List<Measurement> {
        return remoteDataSource.getMeasurements()
    }

    override suspend fun getMeasurementById(id: Long): Measurement {
        return localDataSource.getMeasurement(id)
    }

    override suspend fun getLocalMeasurements(): List<Measurement> {
        return localDataSource.getMeasurements()
    }

    override suspend fun save(measurement: Measurement) {
        localDataSource.save(measurement)
    }

    override suspend fun saveAndShare(measurement: Measurement) {
        save(measurement)
        remoteDataSource.save(measurement)
    }
}
