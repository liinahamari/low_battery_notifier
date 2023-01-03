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

package dev.liinahamari.low_battery_notifier.ui

import android.app.KeyguardManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.view.WindowManager.LayoutParams.*
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import dev.liinahamari.low_battery_notifier.LowBatteryNotifier.mainComponent
import dev.liinahamari.low_battery_notifier.helper.ext.minutesToMilliseconds
import dev.liinahamari.low_battery_notifier.services.DEFAULT_ALARM_PLAYING_TIME
import javax.inject.Inject

internal open class NotifierActivity(@LayoutRes layout: Int) : AppCompatActivity(layout) {
    @Suppress("DEPRECATION")
    private val lockWindowFlags = FLAG_DISMISS_KEYGUARD or FLAG_SHOW_WHEN_LOCKED or FLAG_TURN_SCREEN_ON

    @Inject lateinit var wakeLock: PowerManager.WakeLock
    @Inject lateinit var keyguardManager: KeyguardManager

    @CallSuper override fun onDestroy() = super.onDestroy().also { dimScreen() }

    @CallSuper override fun onCreate(savedInstanceState: Bundle?) {
        mainComponent.inject(this)
        super.onCreate(savedInstanceState)
        turnOnScreen()
    }

    private fun turnOnScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            window.addFlags(lockWindowFlags)
        }
        wakeLock.acquire(minutesToMilliseconds(DEFAULT_ALARM_PLAYING_TIME))
    }

    private fun dimScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(false)
            setTurnScreenOn(false)
        } else {
            window.clearFlags(lockWindowFlags)
        }
        wakeLock.release()
    }
}
