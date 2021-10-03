package dev.uped.noiseless

import android.annotation.SuppressLint
import android.media.*
import android.os.Build
import kotlinx.coroutines.*
import logcat.logcat
import kotlin.math.abs
import kotlin.math.log10

// Based on https://github.com/akofman/cordova-plugin-dbmeter/blob/master/plugin/src/android/DBMeter.java under Apache 2.0 License https://github.com/akofman/cordova-plugin-dbmeter/blob/master/LICENSE
class DBMeter(val receiveDB: (Double) -> Unit) {
    private val scope = CoroutineScope(Job() + Dispatchers.IO)

    private var isListening = false
    private var audioRecord: AudioRecord? = null

    init {
        logcat { "DBMeter init" }
    }

    @SuppressLint("MissingPermission")
    fun start() {
        scope.launch {
            var buffer = ShortArray(0)

            if (audioRecord == null) {
                val rate = AudioTrack.getNativeOutputSampleRate(AudioManager.STREAM_SYSTEM)
                val channelConfig = AudioFormat.CHANNEL_IN_MONO
                val audioFormat = AudioFormat.ENCODING_PCM_16BIT
                val bufferSize = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat)
                val audioSource =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) MediaRecorder.AudioSource.UNPROCESSED else MediaRecorder.AudioSource.MIC
                audioRecord = AudioRecord(audioSource, rate, channelConfig, audioFormat, bufferSize)
                buffer = ShortArray(bufferSize)
            }
            if (!isListening) {
                isListening = true
                audioRecord!!.startRecording()
                while (isListening) {
                    val readSize = audioRecord!!.read(buffer, 0, buffer.size)
                    var maxAmplitude = 0.0
                    for (i in 0 until readSize) {
                        if (abs(buffer[i].toInt()) > maxAmplitude) {
                            maxAmplitude = abs(buffer[i].toInt()).toDouble()
                        }
                    }
                    if (maxAmplitude != 0.0) {
                        receiveDB(20.0 * log10(maxAmplitude / 32767.0) + 100)
                    }
                    delay(100)
                }
            } else {
                logcat { "Unprocessed unavailable" }
            }
        }


    }

    fun destroy() {
        isListening = false
        scope.cancel()
        if (audioRecord != null) {
            audioRecord!!.release()
        }
    }
}