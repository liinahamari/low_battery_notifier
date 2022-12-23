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
import android.content.Intent
import android.os.BatteryManager
import dev.liinahamari.low_battery_notifier.helper.BatteryStateHandlingUseCase
import dev.liinahamari.low_battery_notifier.helper.ext.activityImplicitLaunch
import dev.liinahamari.low_battery_notifier.services.LowBatteryService
import dev.liinahamari.low_battery_notifier.ui.LowBatteryNotifierActivity
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test

class IntentFilterReceiverTest {
    @MockK lateinit var context: Context

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        mockkStatic("dev.liinahamari.low_battery_notifier.helper.ext.ContextExtKt")
        every { context.activityImplicitLaunch(any(), any(), any()) } returns Unit
        every { context.startService(any()) } returns mockk()
        every { context.startActivity(any()) } returns mockk()
    }

    @Test
    fun `when Intent$ACTION_BATTERY_CHANGED returns LEVEL greater than threshold then activity shouldn't be started`() {
        val intent = mockk<Intent>()
        every { intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) } returns 25 /*battery is 25%*/
        every { intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1) } returns 100
        every { context.registerReceiver(any(), any()) } returns intent

        BatteryStateHandlingUseCase(context, null).execute(24)
        verify(exactly = 0) { context.activityImplicitLaunch(any(), any()) }
    }

    @Test
    fun `when Intent$ACTION_BATTERY_CHANGED returns LEVEL equal to threshold then activity shouldn't be started`() {
        val intent = mockk<Intent>()
        every { intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) } returns 25 /*battery is 25%*/
        every { intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1) } returns 100
        every { context.registerReceiver(any(), any()) } returns intent

        BatteryStateHandlingUseCase(context, null).execute(25)
        verify(exactly = 0) { context.activityImplicitLaunch(any(), any()) }
    }

    @Test
    fun `when Intent$ACTION_BATTERY_CHANGED returns LEVEL less than threshold then activity should be started`() {
        val intent = mockk<Intent>()
        every { intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) } returns 25 /*battery is 25%*/
        every { intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1) } returns 100
        every { context.registerReceiver(any(), any()) } returns intent

        BatteryStateHandlingUseCase(context, null).execute(26)
        verify(exactly = 1) {
            context.activityImplicitLaunch(LowBatteryService::class.java, LowBatteryNotifierActivity::class.java)
        }
    }

    @Test
    fun `when registerReceiver==null && BatteryManager==null then activityImplicitLaunch shouldn't be invoked`() {
        every { context.registerReceiver(any(), any()) } returns null

        BatteryStateHandlingUseCase(context, null).execute(26)
        verify(exactly = 0) { context.activityImplicitLaunch(any(), any()) }
    }
}
