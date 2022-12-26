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

package dev.liinahamari.low_battery_notifier.receivers

import android.app.AlarmManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_BOOT_COMPLETED
import dev.liinahamari.low_battery_notifier.helper.ext.scheduleLowBatteryChecker

class BootReceiver : BroadcastReceiver() {
    /**
     * To test "boot completed" event is handling you'll need a rooted device and run in terminal:
     *  adb root
     *  adb shell am broadcast -a android.intent.action.BOOT_COMPLETED -p dev.liinahamari.follower
     *
     * In case {@link #onReceive(Context, Intent)} succeeds receiving, you'll see {@link #BOOT_RECEIVED_LOG}
     * */
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_BOOT_COMPLETED) {
            (context.getSystemService(Service.ALARM_SERVICE) as AlarmManager)
                .scheduleLowBatteryChecker(context = context)
        }
    }
}
