package dev.liinahamari.low_battery_notifier.helper.ext

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.TIRAMISU)
internal fun tiramisuOrMore(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

internal fun lessThanTiramisu(): Boolean = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
internal fun oreoOrMore(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.P)
internal fun pieOrMore(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
