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

import android.app.AlarmManager.INTERVAL_FIFTEEN_MINUTES
import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Build
import androidx.lifecycle.ProcessLifecycleOwner
import dev.liinahamari.low_battery_notifier.di.DaggerMainComponent
import dev.liinahamari.low_battery_notifier.di.MainComponent
import dev.liinahamari.low_battery_notifier.helper.AppLifecycleListener
import dev.liinahamari.low_battery_notifier.helper.Config
import dev.liinahamari.low_battery_notifier.helper.ext.createNotificationChannel
import dev.liinahamari.low_battery_notifier.helper.ext.scheduleLowBatteryChecker
import dev.liinahamari.low_battery_notifier.helper.ext.startActivity
import dev.liinahamari.low_battery_notifier.services.CHANNEL_BATTERY_LOW_ID
import dev.liinahamari.low_battery_notifier.ui.AskPermissionActivity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers

private const val PREFS_KEY = "Prefs"
internal const val BATTERY_CHECKER_ID = 101

internal lateinit var mainComponent: MainComponent

fun init(
    app: Application,
    requestStroboscopeFeature: Boolean = false,
    batteryLevelCheckFrequency: Long = INTERVAL_FIFTEEN_MINUTES
) {
    app.apply {
        applyLibSettings(batteryLevelCheckFrequency)
        initDi()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(CHANNEL_BATTERY_LOW_ID, R.string.title_channel_low_battery)
        }

        scheduleLowBatteryChecker()
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleListener())

        if (requestStroboscopeFeature) {
            startActivity(AskPermissionActivity::class.java)
        }
    }
}

private fun Context.applyLibSettings(
    checkingFrequency: Long
) {
    Completable.fromCallable {
        with(Config) {
            preferences = getSharedPreferences(PREFS_KEY, MODE_PRIVATE)
            this.batteryLevelCheckFrequency = checkingFrequency
        }
    }
        .subscribeOn(Schedulers.io())
        .subscribe()
}

private fun Application.initDi() {
    mainComponent = DaggerMainComponent.builder()
        .application(this)
        .build()
}
