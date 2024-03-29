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

package dev.liinahamari.low_battery_notifier.helper.ext

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalTime
import java.util.concurrent.TimeUnit

internal fun Int.toTimeUnit(): TimeUnit = TimeUnit.values()[this]
internal fun TimeUnit.getIndex(): Int = TimeUnit.values().indexOf(this)

/**
 * Takes into consideration that the interval may span across midnight
 *
 * @return true if given time is inside the specified interval and false otherwise
 */
@Suppress("NAME_SHADOWING")
@RequiresApi(Build.VERSION_CODES.O)
fun LocalTime.inRangeOf(start: String, end: String): Boolean {
    val start = LocalTime.parse(start)
    val end = LocalTime.parse(end)
    if (end.isBefore(start)) {
        if (isAfter(start) && isAfter(end)) {
            return true
        }
        return isBefore(start) && isBefore(end)
    }

    return if (end.isAfter(start)) {
        isAfter(start) && isBefore(end)
    } else false
}
