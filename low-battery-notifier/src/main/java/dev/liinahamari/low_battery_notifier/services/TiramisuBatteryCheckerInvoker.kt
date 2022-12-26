package dev.liinahamari.low_battery_notifier.services

import android.app.AlarmManager
import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import dev.liinahamari.low_battery_notifier.R
import dev.liinahamari.low_battery_notifier.helper.ext.scheduleLowBatteryChecker

internal const val TIRAMISU_BATTERY_CHECKER_INVOKER_CHANNEL_ID = "TiramisuBatteryCheckerInvokerChannelId"
private const val TIRAMISU_BATTERY_CHECKER_INVOKER_ID = 1001

internal class TiramisuBatteryCheckerInvoker : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY.also {
        startForeground(TIRAMISU_BATTERY_CHECKER_INVOKER_ID, formNotification())
        (getSystemService(ALARM_SERVICE) as AlarmManager).scheduleLowBatteryChecker(context = applicationContext)
    }

    private fun formNotification(): Notification =
        NotificationCompat.Builder(this, TIRAMISU_BATTERY_CHECKER_INVOKER_CHANNEL_ID)
            .setSound(null)
            .setSmallIcon(R.drawable.ic_battery_unknown_24)
            .setContentTitle(getString(R.string.tiramisu_ongoing_service_explanation))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setOngoing(true) // to make the notification to be non-dismissable by the user (API >=33)
            .build()

    override fun onBind(intent: Intent?): IBinder? = null
}
