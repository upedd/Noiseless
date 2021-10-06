package dev.uped.noiseless.util

import android.content.Context
import android.provider.Settings

fun isLocationEnabled(context: Context): Boolean {
    val locationMode = try {
        Settings.Secure.getInt(context.contentResolver, Settings.Secure.LOCATION_MODE)
    } catch (e: Settings.SettingNotFoundException) {
        null
    }
    return locationMode != null && locationMode != Settings.Secure.LOCATION_MODE_OFF
}