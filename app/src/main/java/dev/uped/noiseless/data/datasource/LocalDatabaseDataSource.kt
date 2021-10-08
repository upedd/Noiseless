package dev.uped.noiseless.data.datasource

import dev.uped.noiseless.data.MeasurementQueries
import dev.uped.noiseless.model.Measurement

class LocalDatabaseDataSource(private val measurementQueries: MeasurementQueries) : LocalDataSource {
    override suspend fun getMeasurement(id: Long): Measurement {
        return measurementQueries.findById(id).executeAsOne().toModel()
    }

    override suspend fun getMeasurements(): List<Measurement> {
        return measurementQueries.selectAllByDate().executeAsList().map { it.toModel() }
    }

    override suspend fun save(measurement: Measurement) {
        measurementQueries.insert(
            timestamp = measurement.timestamp,
            loudness = measurement.loudness,
            longitude = measurement.longitude,
            latitude = measurement.latitude,
            location = measurement.location
        )
    }

}

fun dev.uped.noiseless.data.Measurement.toModel(): Measurement {
    return Measurement(
        id = id,
        timestamp = timestamp,
        latitude = latitude,
        location = location,
        longitude = longitude,
        loudness = loudness
    )
}