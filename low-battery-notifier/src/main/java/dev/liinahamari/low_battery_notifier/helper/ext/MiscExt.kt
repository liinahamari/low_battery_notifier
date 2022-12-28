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

import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.annotation.ColorInt
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import java.util.concurrent.TimeUnit

private const val MILLIS_IN_SECOND = 1000
private const val SECONDS_IN_MINUTE = 60

fun minutesToMilliseconds(minutes: Long) = minutes * MILLIS_IN_SECOND * SECONDS_IN_MINUTE
fun millisToMinutes(millis: Long) = millis / MILLIS_IN_SECOND / SECONDS_IN_MINUTE

private const val DEFAULT_SKIP_DURATION = 750L

/** Only for RxView elements!*/
fun Observable<Unit>.throttleFirst(skipDurationMillis: Long = DEFAULT_SKIP_DURATION): Observable<Unit> = compose {
    it.throttleFirst(
        skipDurationMillis,
        TimeUnit.MILLISECONDS,
        AndroidSchedulers.mainThread()
    )
}

fun String.toColorfulString(@ColorInt color: Int) = SpannableString(this).apply {
    setSpan(ForegroundColorSpan(color), 0, length, 0)
}
