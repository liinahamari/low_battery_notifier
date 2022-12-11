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

package dev.liinahamari.low_battery_notifier

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.core.content.ContextCompat
import dev.liinahamari.low_battery_notifier.di.APP_CONTEXT
import dev.liinahamari.low_battery_notifier.helper.isAppInForeground
import javax.inject.Inject
import javax.inject.Named

@VisibleForTesting const val BATTERY_THRESHOLD_PERCENTAGE = 23

class BatteryStateHandlingUseCase @Inject constructor(
    @Named(APP_CONTEXT) private val context: Context,
    @JvmField val batteryManager: BatteryManager? = null
) {
    @SuppressLint("NewApi")
    fun execute() {
        (batteryManager?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
            ?: IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { intentFilter ->
                context.registerReceiver(null, intentFilter)?.let { intent ->
                    val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                    val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                    level * 100 / scale.toFloat()
                }
            }?.toInt())?.also {
            if (it < BATTERY_THRESHOLD_PERCENTAGE) {
                context.activityImplicitLaunch(LowBatteryService::class.java, LowBatteryNotifierActivity::class.java)
            }
        }
    }
}

/** workaround for Android 10 restrictions to launch activities in background:
 *  https://developer.android.com/guide/components/activities/background-starts
 * */
fun Context.activityImplicitLaunch(service: Class<out Service>, activity: Class<out Activity>, bundle: Bundle? = null) {
    if (Build.VERSION.SDK_INT >= 29 && isAppInForeground.not()) {
        ContextCompat.startForegroundService(this, Intent(this, service).apply {
            action = ACTION_SHOW_NOTIFICATION
            bundle?.let { putExtras(it) }
        })
    } else {
        startService(Intent(this, service).apply {
            bundle?.let { putExtras(it) }
            action = ACTION_STOP_FOREGROUND
        })
        startActivity(Intent(this, activity).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            bundle?.let { putExtras(it) }
        })
    }
}
