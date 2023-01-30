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

package dev.liinahamari.low_battery_notifier.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import dev.liinahamari.low_battery_notifier.LowBatteryNotifier.mainComponent
import dev.liinahamari.low_battery_notifier.helper.BatteryStateHandlingUseCase
import dev.liinahamari.low_battery_notifier.helper.Config
import dev.liinahamari.low_battery_notifier.helper.RestrictedTimeProcessor
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

internal class LowBatteryReceiver : BroadcastReceiver() {
    @Inject internal lateinit var batteryStateHandlingUseCase: BatteryStateHandlingUseCase
    @Inject internal lateinit var restrictedTimeProcessor: RestrictedTimeProcessor

    override fun onReceive(context: Context, intent: Intent) {
        mainComponent.inject(this)

        Single.fromCallable { Config.lowBatteryThresholdLevel }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .filter { SDK_INT < O || (SDK_INT >= O && restrictedTimeProcessor.isAllowedToCheckBatteryState().not()) }
            .subscribe(batteryStateHandlingUseCase::execute)
    }
}
