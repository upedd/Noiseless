package dev.uped.noiseless.service.geocoding

import android.location.Address
import android.location.Location

interface GeocodingService {
    fun getFromLocation(location: Location): Address?
}