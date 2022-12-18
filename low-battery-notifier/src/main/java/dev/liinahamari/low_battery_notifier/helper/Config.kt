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

import android.app.AlarmManager.INTERVAL_FIFTEEN_MINUTES
import android.content.SharedPreferences
import dev.liinahamari.low_battery_notifier.BuildConfig
import dev.liinahamari.low_battery_notifier.helper.ext.getIndex
import dev.liinahamari.low_battery_notifier.helper.ext.minutesToMilliseconds
import dev.liinahamari.low_battery_notifier.helper.ext.toTimeUnit
import java.util.concurrent.TimeUnit

private const val BATTERY_LEVEL_CHECK_FREQUENCY = "battery_level_check_frequency"
private const val STROBOSCOPE_ON = "stroboscope_on"
private const val STROBOSCOPE_FREQUENCY = "stroboscope_frequency"
private const val STROBOSCOPE_FREQUENCY_TIME_UNIT = "stroboscope_frequency_time_unit"

internal object Config {
    lateinit var preferences: SharedPreferences

    var batteryLevelCheckFrequency: Long
        get() = preferences.getLong(
            BATTERY_LEVEL_CHECK_FREQUENCY,
            if (BuildConfig.DEBUG) minutesToMilliseconds(1) else INTERVAL_FIFTEEN_MINUTES
        )
        set(frequency) = preferences.edit().putLong(BATTERY_LEVEL_CHECK_FREQUENCY, frequency).apply()

    var stroboscopeOn: Boolean
        get() = preferences.getBoolean(STROBOSCOPE_ON, false)
        set(isOn) = preferences.edit().putBoolean(STROBOSCOPE_ON, isOn).apply()

    var stroboscopeFrequency: Long
        get() = preferences.getLong(STROBOSCOPE_FREQUENCY, DEFAULT_STROBOSCOPE_FREQUENCY)
        set(frequency) = preferences.edit().putLong(STROBOSCOPE_FREQUENCY, frequency).apply()

    var stroboscopeFrequencyTimeUnit: TimeUnit
        get() = preferences.getInt(STROBOSCOPE_FREQUENCY_TIME_UNIT, DEFAULT_STROBOSCOPE_FREQUENCY_TIME_UNIT.getIndex())
            .toTimeUnit()
        set(timeUnit) = preferences.edit().putInt(STROBOSCOPE_ON, timeUnit.getIndex()).apply()
}
