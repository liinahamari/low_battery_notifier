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

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import dev.liinahamari.low_battery_notifier.receivers.LowBatteryReceiver
import dev.liinahamari.low_battery_notifier.ui.LowBatteryNotifierActivity
import dev.liinahamari.low_battery_notifier.ui.NotifierActivity
import javax.inject.Singleton

@Singleton
@Component(modules = [ServiceModule::class])
internal interface MainComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context): Builder
        fun build(): MainComponent
    }

    fun inject(activity: NotifierActivity)
    fun inject(activity: LowBatteryNotifierActivity)
    fun inject(receiver: LowBatteryReceiver)
}
