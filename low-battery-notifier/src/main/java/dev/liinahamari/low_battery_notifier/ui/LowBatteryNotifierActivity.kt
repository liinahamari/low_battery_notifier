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

package dev.liinahamari.low_battery_notifier.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import by.kirich1409.viewbindingdelegate.viewBinding
import com.jakewharton.rxbinding4.view.clicks
import dev.liinahamari.low_battery_notifier.LowBatteryNotifier.mainComponent
import dev.liinahamari.low_battery_notifier.R
import dev.liinahamari.low_battery_notifier.databinding.ActivityLowBatteryNotifierBinding
import dev.liinahamari.low_battery_notifier.helper.Notifier
import dev.liinahamari.low_battery_notifier.helper.RxSubscriptionDelegateImpl
import dev.liinahamari.low_battery_notifier.helper.RxSubscriptionsDelegate
import dev.liinahamari.low_battery_notifier.helper.ext.throttleFirst
import dev.liinahamari.low_battery_notifier.services.ACTION_TERMINATE
import dev.liinahamari.low_battery_notifier.services.DEFAULT_ALARM_PLAYING_TIME
import dev.liinahamari.low_battery_notifier.services.LowBatteryService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

internal class LowBatteryNotifierActivity :
    NotifierActivity(R.layout.activity_low_battery_notifier),
    RxSubscriptionsDelegate by RxSubscriptionDelegateImpl() {
    private val ui by viewBinding(ActivityLowBatteryNotifierBinding::bind)

    @Inject lateinit var notifier: Notifier

    override fun onDestroy() {
        disposeSubscriptions()
        notifier.stop()
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        mainComponent.inject(this)
        super.onCreate(savedInstanceState)

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = shutdownNotifiers()
        })

        Observable.timer(
            DEFAULT_ALARM_PLAYING_TIME,
            TimeUnit.MINUTES,
            AndroidSchedulers.mainThread()
        ).addToDisposable(::shutdownNotifiers)

        ui.buttonGotIt.clicks()
            .throttleFirst()
            .addToDisposable(::shutdownNotifiers)

        notifier.start()
    }

    private fun shutdownNotifiers() {
        startService(Intent(this@LowBatteryNotifierActivity, LowBatteryService::class.java)
            .apply {
                action = ACTION_TERMINATE
            })
        finish()
    }
}
