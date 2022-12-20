package dev.liinahamari.lowbatterynotifier

import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import dev.liinahamari.low_battery_notifier.init

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableStrictMode()
        super.onCreate(savedInstanceState)
        findViewById<Button>(R.id.button).setOnClickListener {
            init(application, requestStroboscopeFeature = true)
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
