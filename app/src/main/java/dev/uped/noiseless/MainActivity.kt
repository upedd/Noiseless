package dev.uped.noiseless

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import dev.uped.noiseless.ui.theme.NoiselessTheme
import dev.uped.noiseless.util.PermissionHelper

class MainActivity : ComponentActivity() {

    private val locationPermissionHelper =
        PermissionHelper(Manifest.permission.ACCESS_FINE_LOCATION, this)
    private val microphonePermissionHelper =
        PermissionHelper(Manifest.permission.RECORD_AUDIO, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            NoiselessTheme {
                NoiselessApp(
                    locationPermissionHelper,
                    microphonePermissionHelper
                )
            }
        }
    }
}

