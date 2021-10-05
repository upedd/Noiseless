package dev.uped.noiseless.home

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import dev.uped.noiseless.ui.theme.Green500
import dev.uped.noiseless.ui.theme.Red500
import dev.uped.noiseless.ui.theme.Yellow500
import dev.uped.noiseless.util.createIconGeneratorForColor
import dev.uped.noiseless.util.getIconForLoudness

class MeasurementClusterItem(
    val loudness: Double,
    latitude: Double,
    longitude: Double
) : ClusterItem {

    private val position = LatLng(latitude, longitude)
    override fun getPosition(): LatLng = position

    // No need for now..
    override fun getTitle(): Nothing? = null

    override fun getSnippet(): Nothing? = null
}

// Source: https://github.com/googlemaps/android-maps-utils/blob/main/demo/src/gms/java/com/google/maps/android/utils/demo/CustomMarkerClusteringDemoActivity.java
// Under Apache 2.0 License: https://github.com/googlemaps/android-maps-utils/blob/main/LICENSE
class MeasurementClusterRenderer(
    context: Context, map: GoogleMap?,
    clusterManager: ClusterManager<MeasurementClusterItem>?
) : DefaultClusterRenderer<MeasurementClusterItem>(
    context, map, clusterManager
) {
    private val greenIconGenerator = createIconGeneratorForColor(Green500, context)
    private val yellowIconGenerator = createIconGeneratorForColor(Yellow500, context)
    private val redIconGenerator = createIconGeneratorForColor(Red500, context)

    override fun onBeforeClusterItemRendered(
        item: MeasurementClusterItem,
        markerOptions: MarkerOptions
    ) {
        markerOptions.icon(getItemIcon(item))
    }

    override fun onClusterItemUpdated(item: MeasurementClusterItem, marker: Marker) {
        marker.setIcon(getItemIcon(item))
    }

    override fun onBeforeClusterRendered(
        cluster: Cluster<MeasurementClusterItem>,
        markerOptions: MarkerOptions
    ) {
        markerOptions.icon(getClusterIcon(cluster))
    }

    override fun onClusterUpdated(cluster: Cluster<MeasurementClusterItem>, marker: Marker) {
        marker.setIcon(getClusterIcon(cluster))
    }

    private fun getItemIcon(measurementClusterItem: MeasurementClusterItem): BitmapDescriptor {
        return getIconForLoudness(
            measurementClusterItem.loudness,
            redIconGenerator = redIconGenerator,
            greenIconGenerator = greenIconGenerator,
            yellowIconGenerator = yellowIconGenerator
        )
    }

    private fun getClusterIcon(cluster: Cluster<MeasurementClusterItem>): BitmapDescriptor {
        val averageLoudness = cluster.items.map { it.loudness }.average()
        return getIconForLoudness(
            averageLoudness,
            redIconGenerator = redIconGenerator,
            greenIconGenerator = greenIconGenerator,
            yellowIconGenerator = yellowIconGenerator
        )
    }

    override fun shouldRenderAsCluster(cluster: Cluster<MeasurementClusterItem>): Boolean {
        return cluster.size > 1
    }
}