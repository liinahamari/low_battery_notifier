/*
Copyright 2022-2023 liinahamari

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files
(the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge,
publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR
ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package dev.liinahamari.low_battery_notifier

import android.app.AlarmManager
import android.app.AlarmManager.INTERVAL_FIFTEEN_MINUTES
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.*
import androidx.lifecycle.ProcessLifecycleOwner
import dev.liinahamari.low_battery_notifier.di.DaggerMainComponent
import dev.liinahamari.low_battery_notifier.di.MainComponent
import dev.liinahamari.low_battery_notifier.helper.AppLifecycleListener
import dev.liinahamari.low_battery_notifier.helper.Config
import dev.liinahamari.low_battery_notifier.helper.DEFAULT_LOW_BATTERY_THRESHOLD_PERCENTAGE
import dev.liinahamari.low_battery_notifier.helper.ext.*
import dev.liinahamari.low_battery_notifier.services.CHANNEL_BATTERY_LOW_ID
import dev.liinahamari.low_battery_notifier.ui.AskPermissionActivity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers

private const val PREFS_KEY = "Prefs"
internal const val BATTERY_CHECKER_ID = 101

internal lateinit var mainComponent: MainComponent

fun init(
    context: Context,
    alarmManager: AlarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager,
    notificationManager: NotificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager,
    requestStroboscopeFeature: Boolean = false,
    batteryLevelCheckFrequency: Long = INTERVAL_FIFTEEN_MINUTES,
    lowBatteryThresholdLevel: Int = DEFAULT_LOW_BATTERY_THRESHOLD_PERCENTAGE
) {
    context.applicationContext.apply {
        applyLibSettings(batteryLevelCheckFrequency, lowBatteryThresholdLevel)
        initDi()
        createLowBatteryNotificationChannel(notificationManager)

        if (lessThanTiramisu()) {
            alarmManager.scheduleLowBatteryChecker(context = context)
        }

        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleListener())

        if (requestStroboscopeFeature || tiramisuOrMore()) {
            startActivity(AskPermissionActivity::class.java)
        }
    }
}

fun Context.createLowBatteryNotificationChannel(notificationManager: NotificationManager) {
    if (oreoOrMore()) {
        notificationManager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_BATTERY_LOW_ID,
                getString(R.string.title_channel_low_battery),
                NotificationManager.IMPORTANCE_MAX
            )
        )
    }
}

private fun Context.applyLibSettings(
    checkingFrequency: Long, lowBatteryThresholdLevel: Int
) {
    Completable.fromCallable {
        with(Config) {
            preferences = getSharedPreferences(PREFS_KEY, MODE_PRIVATE)
            this.batteryLevelCheckFrequency = checkingFrequency
            this.lowBatteryThresholdLevel = lowBatteryThresholdLevel
        }
    }
        .subscribeOn(Schedulers.io())
        .subscribe()
}

private fun Context.initDi() {
    mainComponent = DaggerMainComponent.builder()
        .context(this)
        .build()
}
