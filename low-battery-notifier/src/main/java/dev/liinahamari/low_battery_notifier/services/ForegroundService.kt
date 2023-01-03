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

package dev.liinahamari.low_battery_notifier.services

import android.app.Activity
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.Service
import android.content.Intent
import android.graphics.Color
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import dev.liinahamari.low_battery_notifier.helper.ext.toColorfulString
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import java.util.concurrent.TimeUnit

internal const val DEFAULT_ALARM_PLAYING_TIME = 3L
internal const val ACTION_TERMINATE = "ForegroundService.ACTION_TERMINATE"
internal const val ACTION_STOP_FOREGROUND = "ForegroundService.STOP_FOREGROUND"
internal const val ACTION_SHOW_NOTIFICATION = "ForegroundService.SHOW_NOTIFICATION"

internal abstract class ForegroundService : Service() {
    protected val timer: Observable<Long> = Observable.timer(
        DEFAULT_ALARM_PLAYING_TIME,
        TimeUnit.MINUTES
    ).observeOn(AndroidSchedulers.mainThread())

    abstract fun getActionRequestCode(): Int
    abstract fun getTiedActivityRequestCode(): Int
    abstract fun getActivity(): Class<out Activity>
    abstract fun getTitle(intent: Intent?): String
    @DrawableRes abstract fun getIcon(): Int

    protected fun getCancelAction(): NotificationCompat.Action = NotificationCompat.Action(
        0,
        getString(android.R.string.cancel).toColorfulString(Color.RED),
        PendingIntent.getService(
            this,
            getActionRequestCode(),
            Intent(this, this::class.java)
                .apply { this.action = ACTION_TERMINATE },
            FLAG_IMMUTABLE
        )
    )

    protected fun getFullscreenIntent(
        activity: Class<out Activity>
    ): PendingIntent = PendingIntent.getActivity(
        this,
        getTiedActivityRequestCode(),
        Intent(this, activity)
            .apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            },
        FLAG_IMMUTABLE
    )
}
