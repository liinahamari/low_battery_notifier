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
import android.app.Notification
import android.content.Intent
import android.media.RingtoneManager.TYPE_NOTIFICATION
import android.media.RingtoneManager.getDefaultUri
import android.os.IBinder
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.CATEGORY_ALARM
import androidx.core.app.NotificationCompat.PRIORITY_MAX
import dev.liinahamari.low_battery_notifier.R
import dev.liinahamari.low_battery_notifier.helper.RxSubscriptionDelegateImpl
import dev.liinahamari.low_battery_notifier.helper.RxSubscriptionsDelegate
import dev.liinahamari.low_battery_notifier.helper.ext.stopForeground
import dev.liinahamari.low_battery_notifier.ui.LowBatteryNotifierActivity
import java.security.SecureRandom

internal const val CHANNEL_BATTERY_LOW_ID = "CHANNEL_BATTERY_LOW_ID"
internal const val CANCELLATION_ACTION_REQUEST_CODE = 2002
internal const val TIED_ACTIVITY_REQUEST_CODE = 2003

internal class LowBatteryService : ForegroundService(), RxSubscriptionsDelegate by RxSubscriptionDelegateImpl() {

    @DrawableRes override fun getIcon(): Int = R.drawable.ic_baseline_battery_alert_24
    override fun getActivity(): Class<out Activity> = LowBatteryNotifierActivity::class.java
    override fun getTitle(intent: Intent?): String = getString(R.string.title_battery_low)

    override fun getActionRequestCode(): Int = CANCELLATION_ACTION_REQUEST_CODE
    override fun getTiedActivityRequestCode(): Int = TIED_ACTIVITY_REQUEST_CODE

    override fun onDestroy() {
        disposeSubscriptions()
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        when (intent.action) {
            ACTION_SHOW_NOTIFICATION -> {
                timer.addToDisposable(::stopForeground)
                startForeground(SecureRandom().nextInt(), formNotification(intent))
            }
            ACTION_TERMINATE -> stopForeground()
            ACTION_STOP_FOREGROUND -> stopForeground()
            else -> error("Unknown action invoking ${javaClass.name} (${intent.action})")
        }
        return START_NOT_STICKY
    }

    private fun formNotification(intent: Intent): Notification =
        NotificationCompat.Builder(this@LowBatteryService, CHANNEL_BATTERY_LOW_ID)
            .setSound(getDefaultUri(TYPE_NOTIFICATION))
            .setSmallIcon(getIcon())
            .setContentText(getTitle(intent))
            .addAction(getCancelAction())
            .setPriority(PRIORITY_MAX)
            .setOngoing(true) // to make the notification to be non-dismissable by the user (API >=33)
            .setCategory(CATEGORY_ALARM)
            .setFullScreenIntent(getFullscreenIntent(getActivity()), true)
            .build()

    override fun onBind(intent: Intent?): IBinder? = null
}
