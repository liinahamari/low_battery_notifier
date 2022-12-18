/*
 * Copyright 2022-2023 liinahamari
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files
 * (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.liinahamari.low_battery_notifier.helper

import android.Manifest.permission.CAMERA
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.AudioManager.RINGER_MODE_SILENT
import android.media.MediaPlayer
import android.media.RingtoneManager.TYPE_ALARM
import android.media.RingtoneManager.getDefaultUri
import android.os.Build
import android.os.VibrationEffect.createWaveform
import android.os.Vibrator
import android.util.Log
import androidx.core.content.ContextCompat.checkSelfPermission
import dev.liinahamari.low_battery_notifier.di.APP_CONTEXT
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named

internal class Notifier @Inject constructor(
    private val vibrator: Vibrator,
    private val audioManager: AudioManager,
    private val stroboscope: Stroboscope,
    @Named(APP_CONTEXT) private val context: Context,
) : RxSubscriptionsDelegate by RxSubscriptionDelegateImpl() {
    private var player: MediaPlayer? = null
    private val vibrationPattern = longArrayOf(0, 300, 300, 300)

    fun stop() {
        disposeSubscriptions()
        vibrator.cancel()
        stroboscope.releaseStroboscope()
        player?.release()
        player = null
    }

    fun start() {
        stop()
        ring()
        vibrate()

        if (Config.stroboscopeOn && checkSelfPermission(context, CAMERA) == PackageManager.PERMISSION_GRANTED) {
            stroboscope.enableStroboscope(Config.batteryLevelCheckFrequency, Config.stroboscopeFrequencyTimeUnit)
        }
    }

    private fun vibrate() {
        if (audioManager.ringerMode != RINGER_MODE_SILENT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(createWaveform(vibrationPattern, 0))
            } else {
                @Suppress("DEPRECATION") vibrator.vibrate(vibrationPattern, 0)
            }
        }
    }

    private fun createPlayer(): MediaPlayer? = try {
        MediaPlayer().apply {
            reset()
            setAudioAttributes(audioAttributes)
            setDataSource(context, getDefaultUri(TYPE_ALARM))
            isLooping = true
        }
    } catch (e: IOException) {
        null
    }

    private val audioAttributes: AudioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_ALARM)
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .build()

    private fun ring() {
        if (context.contentResolver.isDndEnabled().not()) {
            player = createPlayer()?.apply {
                try {
                    if (isPlaying.not()) {
                        prepare()
                        start()
                    }
                } catch (e: IllegalStateException) {
                    Log.e("", "Notifier.ring()", e)
                } catch (e: IOException) {
                    Log.e("", "Notifier.ring(), IOE", e)
                }
            }
        }
    }
}
