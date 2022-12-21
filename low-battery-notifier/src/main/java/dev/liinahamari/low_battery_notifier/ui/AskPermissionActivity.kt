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

package dev.liinahamari.low_battery_notifier.ui

import android.Manifest.permission.CAMERA
import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import by.kirich1409.viewbindingdelegate.viewBinding
import com.afollestad.materialdialogs.callbacks.onDismiss
import dev.liinahamari.low_battery_notifier.R
import dev.liinahamari.low_battery_notifier.databinding.ActivityAskPermissionBinding
import dev.liinahamari.low_battery_notifier.helper.ext.createNotificationChannel
import dev.liinahamari.low_battery_notifier.helper.stroboscopeSetupDialog
import dev.liinahamari.low_battery_notifier.services.TIRAMISU_BATTERY_CHECKER_INVOKER_CHANNEL_ID
import dev.liinahamari.low_battery_notifier.services.TiramisuBatteryCheckerInvoker

internal class AskPermissionActivity : AppCompatActivity(R.layout.activity_ask_permission) {
    private val ui by viewBinding(ActivityAskPermissionBinding::bind)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val notificationPermissionLauncher =
        registerForActivityResult(RequestPermission()) { isGranted ->
            if (isGranted) {
                createNotificationChannel(
                    TIRAMISU_BATTERY_CHECKER_INVOKER_CHANNEL_ID,
                    R.string.tiramisu_ongoing_service_explanation
                )

                startForegroundService(Intent(this, TiramisuBatteryCheckerInvoker::class.java))

                ui.requestNotificationPermissionBtn.isEnabled = false
                if (ui.requestCamPermissionBtn.isEnabled.not()) {
                    finish()
                }
            }
        }

    private val cameraPermissionLauncher = registerForActivityResult(RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            stroboscopeSetupDialog()
                .onDismiss {
                    ui.requestCamPermissionBtn.isEnabled = false
                    if (ui.requestNotificationPermissionBtn.isEnabled.not()) {
                        finish()
                    }
                }.show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUi()
    }

    private fun setupUi() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && checkSelfPermission(POST_NOTIFICATIONS) != PERMISSION_GRANTED) {
            ui.requestNotificationPermissionBtn.apply {
                isVisible = true
                setOnClickListener { notificationPermissionLauncher.launch(POST_NOTIFICATIONS) }
            }
        }
        ui.requestCamPermissionBtn.setOnClickListener { cameraPermissionLauncher.launch(CAMERA) }

        when {
            checkSelfPermission(CAMERA) == PERMISSION_GRANTED -> ui.requestCamPermissionBtn.isEnabled = false
            checkSelfPermission(POST_NOTIFICATIONS) == PERMISSION_GRANTED -> ui.requestNotificationPermissionBtn.isEnabled =
                false
            checkSelfPermission(CAMERA) == PERMISSION_GRANTED && checkSelfPermission(POST_NOTIFICATIONS) == PERMISSION_GRANTED -> finish()
        }
    }
}
