package dev.uped.noiseless.service.location

import android.annotation.SuppressLint
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.tasks.await

class GmsLocationService(private val fusedLocationProviderClient: FusedLocationProviderClient) : LocationService {
    @SuppressLint("MissingPermission")
    override suspend fun getCached(): Location {
        return fusedLocationProviderClient.lastLocation.await()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @SuppressLint("MissingPermission")
    override suspend fun getCurrent(): Location {
        val cancellationTokenSource = CancellationTokenSource()
        val currentLocationTask = fusedLocationProviderClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token)
        return currentLocationTask.await(cancellationTokenSource)
    }
}
