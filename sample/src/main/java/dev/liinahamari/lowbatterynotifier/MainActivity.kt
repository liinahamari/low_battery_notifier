package dev.liinahamari.lowbatterynotifier

import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import dev.liinahamari.low_battery_notifier.LowBatteryNotifier

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private var lowBatteryNotifier: LowBatteryNotifier? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        enableStrictMode()
        super.onCreate(savedInstanceState)
        findViewById<Button>(R.id.button).setOnClickListener {
            lowBatteryNotifier = LowBatteryNotifier.also {
                it.init(application, requestStroboscopeFeature = true, lowBatteryThresholdLevel = 100)
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
