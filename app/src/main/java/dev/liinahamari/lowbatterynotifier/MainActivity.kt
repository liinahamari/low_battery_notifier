package dev.liinahamari.lowbatterynotifier

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import dev.liinahamari.low_battery_notifier.init

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findViewById<Button>(R.id.button).setOnClickListener {
            init(application, requestStroboscopeFeature = true)
        }
    }
}
