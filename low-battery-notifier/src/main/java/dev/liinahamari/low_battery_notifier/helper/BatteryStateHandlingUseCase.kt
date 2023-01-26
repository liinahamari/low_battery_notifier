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

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import dev.liinahamari.low_battery_notifier.helper.ext.activityImplicitLaunch
import dev.liinahamari.low_battery_notifier.services.LowBatteryService
import dev.liinahamari.low_battery_notifier.ui.LowBatteryNotifierActivity
import javax.inject.Inject

private const val MAX_BATTERY_CAPACITY_IN_PERCENT = 100

internal class BatteryStateHandlingUseCase @Inject constructor(
    private val context: Context,
    @JvmField val batteryManager: BatteryManager? = null
) {
    fun execute(lowBatteryThresholdLevel: Int) {
        (batteryManager?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
            ?: context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))?.let { intent ->
                val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                level * MAX_BATTERY_CAPACITY_IN_PERCENT / scale.toFloat()
            }?.toInt())?.also {
            if (it <= lowBatteryThresholdLevel) {
                context.activityImplicitLaunch(LowBatteryService::class.java, LowBatteryNotifierActivity::class.java)
            }
        }
    }
}
