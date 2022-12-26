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

package dev.liinahamari.low_battery_notifier

import android.content.Context
import android.os.BatteryManager
import dev.liinahamari.low_battery_notifier.helper.BatteryStateHandlingUseCase
import dev.liinahamari.low_battery_notifier.helper.ext.activityImplicitLaunch
import dev.liinahamari.low_battery_notifier.services.LowBatteryService
import dev.liinahamari.low_battery_notifier.ui.LowBatteryNotifierActivity
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class BatteryManagerTest {
    @MockK lateinit var batteryManager: BatteryManager
    @MockK lateinit var context: Context

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)
        mockkStatic("dev.liinahamari.low_battery_notifier.helper.ext.ContextExtKt")
    }

    @Test
    fun `when BatteryManager!=null && battery capacity lesser than threshold then activityImplicitLaunch func invoked`() {
        every { batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) } returns 24
        BatteryStateHandlingUseCase(context, batteryManager).execute(25)
        verify(exactly = 1) {
            context.activityImplicitLaunch(LowBatteryService::class.java, LowBatteryNotifierActivity::class.java)
        }
    }

    @Test
    fun `when BatteryManager!=null && battery capacity greater than threshold then activityImplicitLaunch func invoked`() {
        every { batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) } returns 100
        BatteryStateHandlingUseCase(context, batteryManager).execute(25)
        verify(exactly = 0) {
            context.activityImplicitLaunch(LowBatteryService::class.java, LowBatteryNotifierActivity::class.java)
        }
    }

    @Test
    fun `when BatteryManager!=null && battery capacity equal to threshold then activityImplicitLaunch func invoked`() {
        every { batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) } returns 25
        BatteryStateHandlingUseCase(context, batteryManager).execute(25)
        verify(exactly = 0) {
            context.activityImplicitLaunch(LowBatteryService::class.java, LowBatteryNotifierActivity::class.java)
        }
    }
}
