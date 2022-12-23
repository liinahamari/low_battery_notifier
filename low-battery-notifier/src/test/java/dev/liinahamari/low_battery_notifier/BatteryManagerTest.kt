package dev.liinahamari.low_battery_notifier

import android.content.Context
import android.os.BatteryManager
import dev.liinahamari.low_battery_notifier.helper.BatteryStateHandlingUseCase
import dev.liinahamari.low_battery_notifier.helper.ext.activityImplicitLaunch
import dev.liinahamari.low_battery_notifier.services.LowBatteryService
import dev.liinahamari.low_battery_notifier.ui.LowBatteryNotifierActivity
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test

class BatteryManagerTest {
    @MockK lateinit var batteryManager: BatteryManager
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
}
