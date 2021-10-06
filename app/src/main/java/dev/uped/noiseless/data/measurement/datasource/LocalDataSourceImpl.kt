package dev.uped.noiseless.data.measurement.datasource

import dev.uped.noiseless.data.DB
import dev.uped.noiseless.model.Measurement

class LocalDataSourceImpl : LocalDataSource {
    override suspend fun getMeasurement(id: Long): Measurement {
        return DB.measurementQueries.findById(id).executeAsOne().toModel()
    }

    override suspend fun getMeasurements(): List<Measurement> {
        return DB.measurementQueries.selectAllByDate().executeAsList().map { it.toModel() }
    }

    override suspend fun save(measurement: Measurement) {
        DB.measurementQueries.insert(
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