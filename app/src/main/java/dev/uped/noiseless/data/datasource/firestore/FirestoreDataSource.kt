package dev.uped.noiseless.data.datasource.firestore

import com.google.firebase.firestore.FirebaseFirestore
import dev.uped.noiseless.data.datasource.RemoteDataSource
import dev.uped.noiseless.model.Measurement
import kotlinx.coroutines.tasks.await

class FirestoreDataSource(private val firestore: FirebaseFirestore) : RemoteDataSource {
    override suspend fun getMeasurements(): List<Measurement> {
        return firestore
            .collection("measurements")
            .get()
            .await()
            .toObjects(FirestoreMeasurement::class.java)
            .mapNotNull { it.toMeasurement() }
    }

    override suspend fun save(measurement: Measurement) {
        firestore
            .collection("measurements")
            .add(measurement.toFirestoreMeasurement())
            .await()
    }
}