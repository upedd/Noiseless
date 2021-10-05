package dev.uped.noiseless.util

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.maps.android.ui.IconGenerator
import dev.uped.noiseless.R

@SuppressLint("InflateParams")
fun createIconGeneratorForColor(color: Color, context: Context): IconGenerator {
    val iconGenerator = IconGenerator(context)
    val view = LayoutInflater.from(context).inflate(R.layout.marker, null)
    val imageView = view.findViewById<ImageView>(R.id.MarkerIcon)
    imageView.setColorFilter(color.toArgb())
    iconGenerator.setContentView(view)
    iconGenerator.setBackground(null)
    return iconGenerator
}

fun getIconForLoudness(
    loudness: Double,
    greenIconGenerator: IconGenerator,
    yellowIconGenerator: IconGenerator,
    redIconGenerator: IconGenerator
): BitmapDescriptor {
    val iconGenerator = when {
        loudness <= 40 -> greenIconGenerator
        loudness <= 70 -> yellowIconGenerator
        else -> redIconGenerator
    }
    val icon = iconGenerator.makeIcon(String.format("%.1f", loudness))
    return BitmapDescriptorFactory.fromBitmap(icon)
}