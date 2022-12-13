package dev.liinahamari.low_battery_notifier.helper

import android.app.Activity
import android.app.AlarmManager
import android.app.AlarmManager.INTERVAL_HALF_HOUR
import android.app.AlarmManager.RTC_WAKEUP
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.Service
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import dev.liinahamari.low_battery_notifier.ACTION_SHOW_NOTIFICATION
import dev.liinahamari.low_battery_notifier.ACTION_STOP_FOREGROUND
import dev.liinahamari.low_battery_notifier.BATTERY_CHECKER_ID
import dev.liinahamari.low_battery_notifier.BuildConfig
import dev.liinahamari.low_battery_notifier.receivers.LowBatteryReceiver
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import java.lang.System.currentTimeMillis
import java.util.concurrent.TimeUnit

fun minutesToMilliseconds(minutes: Long) = minutes * 1000 * 60

/** Only for RxView elements!*/
fun Observable<Unit>.throttleFirst(skipDurationMillis: Long = 750L): Observable<Unit> = compose {
    it.throttleFirst(
        skipDurationMillis,
        TimeUnit.MILLISECONDS,
        AndroidSchedulers.mainThread()
    )
}

fun String.toColorfulString(@ColorInt color: Int) = SpannableString(this).apply {
    setSpan(ForegroundColorSpan(color), 0, length, 0)
}

/** Runs once-an-hour checker of battery state */
fun Context.scheduleLowBatteryChecker(initialDelayInMinutes: Long = 1L) =
    (getSystemService(Context.ALARM_SERVICE) as AlarmManager).setRepeating(
        RTC_WAKEUP,
        currentTimeMillis() + minutesToMilliseconds(initialDelayInMinutes),
        if (BuildConfig.DEBUG) minutesToMilliseconds(1) else INTERVAL_HALF_HOUR,
        PendingIntent.getBroadcast(
            this,
            BATTERY_CHECKER_ID,
            Intent(this, LowBatteryReceiver::class.java),
            FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT
        )
    )

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
            bundle?.let { putExtras(it) }
        })
    } else {
        startService(Intent(this, service).setAction(ACTION_STOP_FOREGROUND))

        startActivity(Intent(this, activity).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            bundle?.let { putExtras(it) }
        })
    }
}
