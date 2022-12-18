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

package dev.liinahamari.low_battery_notifier.helper

import android.text.InputType.TYPE_CLASS_NUMBER
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.getActionButton
import com.afollestad.materialdialogs.checkbox.checkBoxPrompt
import com.afollestad.materialdialogs.checkbox.getCheckBoxPrompt
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import dev.liinahamari.low_battery_notifier.R
import dev.liinahamari.low_battery_notifier.helper.ext.getIndex
import dev.liinahamari.low_battery_notifier.helper.ext.toTimeUnit
import java.util.concurrent.TimeUnit

@Suppress("CheckResult")
internal fun AppCompatActivity.stroboscopeSetupDialog() = MaterialDialog(this)
    .checkBoxPrompt(R.string.stroboscope_on) {
        Config.stroboscopeOn = it
    }
    .input(
        hintRes = R.string.time_unit,
        prefill = DEFAULT_STROBOSCOPE_FREQUENCY.toString(),
        inputType = TYPE_CLASS_NUMBER,
        waitForPositiveButton = false,
        allowEmpty = false
    ) { _, text ->
        Config.stroboscopeFrequency = text.toString().toLong()
    }
    .listItemsSingleChoice(
        initialSelection = TimeUnit.MILLISECONDS.getIndex(),
        waitForPositiveButton = true,
        res = R.array.time_units,
        selection = { _, index, _ ->
            Config.stroboscopeFrequencyTimeUnit = index.toTimeUnit()
        }
    )
    .apply {
        cancelable(false)

        getActionButton(WhichButton.POSITIVE).apply {
            isEnabled = false
            setOnClickListener {
                dismiss()
                finish()
            }
        }

        getCheckBoxPrompt().setOnCheckedChangeListener { _, isChecked ->
            getActionButton(WhichButton.POSITIVE).isEnabled = isChecked
        }
    }
