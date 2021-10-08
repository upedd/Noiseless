package dev.uped.noiseless.screen.result

import androidx.annotation.StringRes
import dev.uped.noiseless.R

enum class LocationState(@StringRes val displayString: Int?) {
    LOADING(R.string.location_state_loading),
    PERMISSION_MISSING(R.string.location_state_permission_disabled),
    LOCATION_DISABLED(R.string.location_state_disabled),
    UNKNOWN(R.string.location_state_unknown),
    READY(null)
}