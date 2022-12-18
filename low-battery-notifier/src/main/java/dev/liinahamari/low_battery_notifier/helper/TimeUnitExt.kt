package dev.liinahamari.low_battery_notifier.helper

import java.util.concurrent.TimeUnit

internal fun Int.toTimeUnit(): TimeUnit = TimeUnit.values()[this]
internal fun TimeUnit.getIndex(): Int = TimeUnit.values().indexOf(this)
