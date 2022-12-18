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

import android.Manifest
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.appcompat.app.AppCompatActivity
import dev.liinahamari.low_battery_notifier.helper.stroboscopeSetupDialog
import dev.liinahamari.low_battery_notifier.helper.toast

internal class AskPermissionActivity : AppCompatActivity(R.layout.activity_ask_permission) {
    private val requestPermissionLauncher = registerForActivityResult(RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            stroboscopeSetupDialog().show()
        } else {
            toast(R.string.cam_permission_rationale)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when {
            checkSelfPermission(Manifest.permission.CAMERA) == PERMISSION_GRANTED -> stroboscopeSetupDialog().show()
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> toast(R.string.cam_permission_rationale)
            else -> requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
}
