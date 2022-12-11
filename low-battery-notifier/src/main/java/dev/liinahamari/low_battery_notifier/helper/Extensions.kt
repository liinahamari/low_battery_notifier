package dev.liinahamari.low_battery_notifier.helper

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.annotation.ColorInt
import dev.liinahamari.low_battery_notifier.BATTERY_CHECKER_ID
import dev.liinahamari.low_battery_notifier.BuildConfig
import dev.liinahamari.low_battery_notifier.receivers.LowBatteryReceiver
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
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
    setSpan(
        ForegroundColorSpan(color), 0, length, 0
    )
}

/** Runs once-an-hour checker of battery state */
fun Context.scheduleLowBatteryChecker(initialDelayInMinutes: Long = 1L) =
    (getSystemService(Context.ALARM_SERVICE) as AlarmManager).setRepeating(
        AlarmManager.RTC_WAKEUP,
        System.currentTimeMillis() + minutesToMilliseconds(initialDelayInMinutes),
        if (BuildConfig.DEBUG) AlarmManager.INTERVAL_FIFTEEN_MINUTES else AlarmManager.INTERVAL_HOUR,
        PendingIntent.getBroadcast(
            this,
            BATTERY_CHECKER_ID,
            Intent(this, LowBatteryReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
    )

fun ContentResolver.isDndEnabled(): Boolean = kotlin.runCatching {
    Settings.Global.getInt(this, "zen_mode")
}.getOrNull()?.equals(1) ?: true
