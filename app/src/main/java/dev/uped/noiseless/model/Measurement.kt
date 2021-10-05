package dev.uped.noiseless.model

import androidx.compose.runtime.Stable

@Stable
data class Measurement(
    val id: Long = -1,
    val loudness: Double,
    val latitude: String?,
    val longitude: String?,
    val location: String,
    val timestamp: Long
)
