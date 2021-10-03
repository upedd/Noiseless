package dev.uped.noiseless

import android.app.Application
import dev.uped.noiseless.data.DB
import logcat.AndroidLogcatLogger
import logcat.LogPriority

class NoiselessApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DB.createDriver(applicationContext)
        // Log all priorities in debug builds, no-op in release builds.
        AndroidLogcatLogger.installOnDebuggableApp(this, minPriority = LogPriority.VERBOSE)
    }
}