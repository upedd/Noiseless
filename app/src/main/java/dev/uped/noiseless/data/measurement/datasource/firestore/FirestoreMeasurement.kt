package dev.uped.noiseless.data.measurement.datasource.firestore

import dev.uped.noiseless.model.Measurement

data class FirestoreMeasurement(
    val loudness: Double? = null,
    val latitude: String? = null,
    val longitude: String? = null,
    val location: String? = null,
    val timestamp: Long? = null
)

fun FirestoreMeasurement.toMeasurement(): Measurement? {
    if (loudness == null || longitude == null || latitude == null) {
        return null
    }
    return Measurement(-1, loudness, latitude, longitude, location ?: "Unknown", timestamp ?: 0)
}

fun Measurement.toFirestoreMeasurement(): FirestoreMeasurement {
    return FirestoreMeasurement(loudness, latitude, longitude, location, timestamp)
}
