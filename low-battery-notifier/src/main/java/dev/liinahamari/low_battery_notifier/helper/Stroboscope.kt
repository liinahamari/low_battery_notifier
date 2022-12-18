@file:Suppress("DEPRECATION")

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

import android.graphics.SurfaceTexture
import android.hardware.Camera
import io.reactivex.rxjava3.core.Observable
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

internal interface Stroboscope {
    fun enableStroboscope(frequency: Long, timeUnit: TimeUnit)
    fun releaseStroboscope()
}

internal const val DEFAULT_STROBOSCOPE_FREQUENCY = 300L
internal val DEFAULT_STROBOSCOPE_FREQUENCY_TIME_UNIT = TimeUnit.MILLISECONDS

internal class StroboscopeImpl : Stroboscope, RxSubscriptionsDelegate by RxSubscriptionDelegateImpl() {
    private var camera: Camera? = null
    private var params: Camera.Parameters? = null
    private var lightsOn = AtomicBoolean(true)

    override fun enableStroboscope(frequency: Long, timeUnit: TimeUnit) {
        initializeCamera()
        Observable.interval(frequency, timeUnit).subscribeUi {
            toggleFlashlight(lightsOn.getAndSet(lightsOn.get().not()))
        }
    }

    override fun releaseStroboscope() {
        disposeSubscriptions()
        camera?.release()
        camera = null
    }

    private fun toggleFlashlight(enable: Boolean) {
        with(camera!!) {
            parameters = params!!.apply {
                flashMode = if (enable) Camera.Parameters.FLASH_MODE_TORCH else Camera.Parameters.FLASH_MODE_OFF
            }
            if (enable) {
                setPreviewTexture(SurfaceTexture(1))
                startPreview()
            }
        }
    }

    private fun initializeCamera() {
        camera = Camera.open()
        params = camera!!.parameters
        params!!.flashMode = Camera.Parameters.FLASH_MODE_OFF
        camera!!.parameters = params
    }
}
