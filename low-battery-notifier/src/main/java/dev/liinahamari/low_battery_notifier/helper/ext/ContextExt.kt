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

import android.annotation.SuppressLint
import android.app.*
import android.app.NotificationManager.*
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import dev.liinahamari.low_battery_notifier.BATTERY_CHECKER_ID
import dev.liinahamari.low_battery_notifier.BuildConfig
import dev.liinahamari.low_battery_notifier.helper.isAppInForeground
import dev.liinahamari.low_battery_notifier.receivers.LowBatteryReceiver
import dev.liinahamari.low_battery_notifier.services.ACTION_SHOW_NOTIFICATION
import dev.liinahamari.low_battery_notifier.services.ACTION_STOP_FOREGROUND

/** Runs once-an-hour checker of battery state */
fun Context.scheduleLowBatteryChecker(initialDelayInMinutes: Long = 1L) {
    (getSystemService(Context.ALARM_SERVICE) as AlarmManager).setRepeating(
        AlarmManager.RTC_WAKEUP,
        System.currentTimeMillis() + minutesToMilliseconds(initialDelayInMinutes),
        if (BuildConfig.DEBUG) minutesToMilliseconds(1) else AlarmManager.INTERVAL_HALF_HOUR,
        PendingIntent.getBroadcast(
            this,
            BATTERY_CHECKER_ID,
            Intent(this, LowBatteryReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    )
}

fun ContentResolver.isDndEnabled(): Boolean = kotlin.runCatching {
    Settings.Global.getInt(this, "zen_mode")
}.getOrNull()?.equals(1) ?: true

/** workaround for Android 10 restrictions to launch activities in background:
 *  https://developer.android.com/guide/components/activities/background-starts
 * */
internal fun Context.activityImplicitLaunch(
    service: Class<out Service>,
    activity: Class<out Activity>,
    bundle: Bundle? = null
) {
    if (Build.VERSION.SDK_INT >= 29 && isAppInForeground.not()) {
        ContextCompat.startForegroundService(this, Intent(this, service).apply {
            action = ACTION_SHOW_NOTIFICATION
            bundle?.let(::putExtras)
        })
    } else {
        startService(Intent(this, service).setAction(ACTION_STOP_FOREGROUND))

        startActivity(Intent(this, activity).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            bundle?.let(::putExtras)
        })
    }
}

@SuppressLint("WrongConstant")
@RequiresApi(Build.VERSION_CODES.O)
internal fun Context.createNotificationChannel(
    name: String,
    @StringRes description: Int,
    @androidx.annotation.IntRange(
        from = IMPORTANCE_NONE.toLong(),
        to = IMPORTANCE_MAX.toLong()
    ) importance: Int = IMPORTANCE_MAX
) {
    (getSystemService(NotificationManager::class.java) as NotificationManager).createNotificationChannel(
        NotificationChannel(name, getString(description), importance)
    )
}

fun Context.startActivity(clazz: Class<out AppCompatActivity>) {
    startActivity(Intent(this, clazz).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    })
}
