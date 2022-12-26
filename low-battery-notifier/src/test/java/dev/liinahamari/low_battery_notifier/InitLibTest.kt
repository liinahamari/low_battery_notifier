package dev.liinahamari.low_battery_notifier

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.os.Build.VERSION_CODES.*
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import dev.liinahamari.low_battery_notifier.helper.ext.millisToMinutes
import dev.liinahamari.low_battery_notifier.helper.ext.startActivity
import dev.liinahamari.low_battery_notifier.ui.AskPermissionActivity
import io.mockk.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import java.util.*

@RunWith(RobolectricTestRunner::class)
class InitLibTest {
    private val context: Context = getInstrumentation().context

    @Config(minSdk = N, maxSdk = S_V2)
    @Test
    fun `when Android API less than Tiramisu then next alarm which checks battery state should be scheduled approx 1 minute after library initialization`() {
        val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val now = System.currentTimeMillis()

        init(
            context = context,
            alarmManager = alarmManager,
            notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        )

        val nextScheduledAlarm = shadowOf(alarmManager).nextScheduledAlarm.triggerAtTime

        val differenceInMinutes = millisToMinutes(nextScheduledAlarm - now).apply(::println)
        assert(differenceInMinutes in 1..2)
    }

    @Config(sdk = [TIRAMISU])
    @Test
    fun `when Android API is Tiramisu then next alarm which checks battery state shouldn't be scheduled at all`() {
        val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        init(
            context = context,
            alarmManager = alarmManager,
            notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        )

        assert(shadowOf(alarmManager).nextScheduledAlarm == null)
    }

    @Config(sdk = [TIRAMISU])
    @Test
    fun `when Android API is Tiramisu then AskPermissionActivity should start`() {
        mockkStatic("dev.liinahamari.low_battery_notifier.helper.ext.ContextExtKt")
        val context = mockk<Context>(relaxed = true)
        init(context, alarmManager = mockk(relaxed = true), notificationManager = mockk(relaxed = true))
        verify(exactly = 1) { context.applicationContext.startActivity(AskPermissionActivity::class.java) }
    }
}
