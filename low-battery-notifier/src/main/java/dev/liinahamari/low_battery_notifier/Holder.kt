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

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import dev.liinahamari.low_battery_notifier.di.DaggerMainComponent
import dev.liinahamari.low_battery_notifier.di.MainComponent
import dev.liinahamari.low_battery_notifier.helper.AppLifecycleListener
import dev.liinahamari.low_battery_notifier.helper.minutesToMilliseconds
import dev.liinahamari.low_battery_notifier.helper.scheduleLowBatteryChecker
import dev.liinahamari.low_battery_notifier.receivers.LowBatteryReceiver

internal const val BATTERY_CHECKER_ID = 101

internal lateinit var mainComponent: MainComponent

fun init(app: Application) {
    app.apply {
        initDi()
        createLowBatteryNotificationChannel()
        scheduleLowBatteryChecker()
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleListener())
    }
}

private fun Application.initDi() {
    mainComponent = DaggerMainComponent.builder()
        .application(this)
        .build()
}

@SuppressLint("WrongConstant") private fun Application.createLowBatteryNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        (getSystemService(NotificationManager::class.java) as NotificationManager).createNotificationChannel(
            NotificationChannel(
                CHANNEL_BATTERY_LOW_ID,
                getString(R.string.title_channel_low_battery),
                NotificationManager.IMPORTANCE_MAX
            )
        )
    }
}
