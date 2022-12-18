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

package dev.liinahamari.low_battery_notifier.di

import android.app.Application
import android.app.KeyguardManager
import android.content.Context
import android.content.Context.*
import android.media.AudioManager
import android.os.*
import android.os.PowerManager.WakeLock
import android.view.WindowManager
import dagger.Module
import dagger.Provides
import dev.liinahamari.low_battery_notifier.helper.Stroboscope
import dev.liinahamari.low_battery_notifier.helper.StroboscopeImpl
import javax.inject.Named
import javax.inject.Singleton

internal const val APP_CONTEXT = "application_context"

@Module
internal open class ServiceModule {
    @Provides
    @Singleton
    @Named(APP_CONTEXT)
    fun provideContext(app: Application): Context = app.applicationContext

    @Provides
    @Singleton
    fun provideBatteryManager(@Named(APP_CONTEXT) context: Context): BatteryManager? =
        context.getSystemService(BATTERY_SERVICE) as BatteryManager?

    @Provides
    @Singleton
    open fun provideKeyGuardManager(@Named(APP_CONTEXT) context: Context): KeyguardManager =
        context.getSystemService(KEYGUARD_SERVICE) as KeyguardManager

    @Provides
    @Singleton
    open fun provideWakeLock(@Named(APP_CONTEXT) context: Context): WakeLock =
        (context.getSystemService(POWER_SERVICE) as PowerManager)
            .newWakeLock(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, javaClass.simpleName)


    @Provides
    @Singleton
    fun provideVibrator(@Named(APP_CONTEXT) context: Context): Vibrator =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (context.getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        } else {
            @Suppress("DEPRECATION") context.getSystemService(VIBRATOR_SERVICE) as Vibrator
        }

    @Provides
    @Singleton
    fun provideAudioManager(@Named(APP_CONTEXT) context: Context): AudioManager =
        context.getSystemService(AUDIO_SERVICE) as AudioManager

    @Provides
    @Singleton
    fun provideStroboscope(): Stroboscope = StroboscopeImpl()
}
