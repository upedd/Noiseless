package dev.uped.noiseless.service.location

import android.location.Location

interface LocationService {
    suspend fun getCached(): Location
    suspend fun getCurrent(): Location
}