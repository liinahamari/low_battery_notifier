package dev.liinahamari.lowbatterynotifier

import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import dev.liinahamari.low_battery_notifier.LowBatteryNotifier
import dev.liinahamari.lowbatterynotifier.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private val ui by viewBinding(ActivityMainBinding::bind)
    private var lowBatteryNotifier: LowBatteryNotifier? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        enableStrictMode()
        super.onCreate(savedInstanceState)
        ui.initLibBtn.setOnClickListener { initLowBatteryNotifierLib() }
    }

    override fun onDestroy() {
        lowBatteryNotifier = null
        super.onDestroy()
    }

    private fun initLowBatteryNotifierLib() {
        lowBatteryNotifier = LowBatteryNotifier.also {
            try {
                it.init(
                    application,
                    requestStroboscopeFeature = true,
                    lowBatteryThresholdLevel = ui.thresholdSlider.value.toInt()
                )
                ui.initLibBtn.isEnabled = false
            } catch (e: Throwable) {
                ui.initLibBtn.isEnabled = true
                Toast.makeText(this, e.stackTraceToString(), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun enableStrictMode() {
        StrictMode.setThreadPolicy(
            ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyDialog()
                .build()
        )
        StrictMode.setVmPolicy(
            VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyDeath()
                .build()
        )
    }
}
