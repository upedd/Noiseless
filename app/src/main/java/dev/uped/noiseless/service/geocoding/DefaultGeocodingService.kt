package dev.uped.noiseless.service.geocoding

import android.location.Address
import android.location.Geocoder
import android.location.Location

class DefaultGeocodingService(private val geocoder: Geocoder) : GeocodingService {
    override fun getFromLocation(location: Location): Address? {
        // #FIXME handle if geocoder is missing
        return geocoder.getFromLocation(location.latitude, location.longitude, 1).firstOrNull()
    }
}