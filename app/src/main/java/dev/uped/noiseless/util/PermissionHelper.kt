package dev.uped.noiseless.util

import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionHelper(
    private val permission: String,
    private val activity: ComponentActivity // It's probably a bad idea
) {
    private var _isPermissionGranted = mutableStateOf(false)
    private val activityResultLauncher: ActivityResultLauncher<String> =
        activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            _isPermissionGranted.value = isGranted
        }
    val isPermissionGranted: State<Boolean> = _isPermissionGranted

    fun requestPermission(onShowRationale: () -> Unit) {
        when {
            ContextCompat.checkSelfPermission(
                activity.applicationContext,
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                _isPermissionGranted.value = true
            }
            ActivityCompat.shouldShowRequestPermissionRationale(activity, permission) -> {
                onShowRationale()
            }
            else -> {
                activityResultLauncher.launch(permission)
            }
        }
    }

    fun requestPermission() {
        if (ContextCompat.checkSelfPermission(
                activity.applicationContext,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            _isPermissionGranted.value = true
        } else {
            activityResultLauncher.launch(permission)
        }
    }
}