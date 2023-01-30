/*
 * Copyright 2022-2023 liinahamari
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files
 * (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.liinahamari.low_battery_notifier

import android.os.Build.VERSION_CODES.O
import dev.liinahamari.low_battery_notifier.helper.Config
import dev.liinahamari.low_battery_notifier.helper.RestrictedTimeProcessor
import io.mockk.every
import io.mockk.mockkObject
import org.junit.Test
import java.time.LocalTime

@org.robolectric.annotation.Config(sdk = [O])
class RestrictedTimeTest {
    @Test
    fun `if time within one day and time is in range (upper threshold) then return func returns true`() {
        with(Config) {
            mockkObject(this)
            every { restrictedTime } returns RestrictedTime("11:00", "12:00")
            assert(RestrictedTimeProcessor().isAllowedToCheckBatteryState(LocalTime.of(11, 59)))
        }
    }

    @Test
    fun `if time within one day and time is in range (lower threshold) then return func returns true`() {
        with(Config) {
            mockkObject(this)
            every { restrictedTime } returns RestrictedTime("11:00", "12:00")
            assert(RestrictedTimeProcessor().isAllowedToCheckBatteryState(LocalTime.of(11, 1)))
        }
    }

    @Test
    fun `if time within one day and time isnt in range (upper threshold) then return func returns false`() {
        with(Config) {
            mockkObject(this)
            every { restrictedTime } returns RestrictedTime("11:00", "12:00")
            assert(RestrictedTimeProcessor().isAllowedToCheckBatteryState(LocalTime.of(12, 1)).not())
        }
    }

    @Test
    fun `if time within one day and time isnt in range (lower threshold) then return func returns false`() {
        with(Config) {
            mockkObject(this)
            every { restrictedTime } returns RestrictedTime("11:00", "12:00")
            assert(RestrictedTimeProcessor().isAllowedToCheckBatteryState(LocalTime.of(10, 59)).not())
        }
    }

    @Test
    fun `if time isnt within one day and time is in range (upper threshold) then return func returns true`() {
        with(Config) {
            mockkObject(this)
            every { restrictedTime } returns RestrictedTime("23:00", "12:00")
            assert(RestrictedTimeProcessor().isAllowedToCheckBatteryState(LocalTime.of(11, 59)))
        }
    }

    @Test
    fun `if time isnt within one day and time is in range (lower threshold) then return func returns true`() {
        with(Config) {
            mockkObject(this)
            every { restrictedTime } returns RestrictedTime("23:00", "12:00")
            assert(RestrictedTimeProcessor().isAllowedToCheckBatteryState(LocalTime.of(23, 1)))
        }
    }

    @Test
    fun `if time isnt within one day and time isnt in range (upper threshold) then return func returns false`() {
        with(Config) {
            mockkObject(this)
            every { restrictedTime } returns RestrictedTime("23:00", "12:00")
            assert(RestrictedTimeProcessor().isAllowedToCheckBatteryState(LocalTime.of(12, 1)).not())
        }
    }

    @Test
    fun `if time isnt within one day and time isnt in range (lower threshold) then return func returns false`() {
        with(Config) {
            mockkObject(this)
            every { restrictedTime } returns RestrictedTime("23:00", "12:00")
            assert(RestrictedTimeProcessor().isAllowedToCheckBatteryState(LocalTime.of(22, 59)).not())
        }
    }
}
