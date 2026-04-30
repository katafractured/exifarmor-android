package com.katafract.exifarmor

import android.graphics.Bitmap
import android.util.Log
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.services.storage.TestStorage
import androidx.test.uiautomator.UiDevice
import org.junit.After
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

/**
 * Compose UI screenshots for Play Console listing. Captures the HOME screen
 * empty state on each AVD form factor (phone / 7in / 10in tablet).
 *
 * Resilience: UiAutomation.takeScreenshot() intermittently returns null on
 * tablet AVDs even when the phone AVD captures cleanly (same Compose surface,
 * different SurfaceFlinger/HWC behavior). Rather than fail the build, retry a
 * few times and log-skip — the upload script does delete-all-then-upload, so a
 * missing PNG just leaves that slot empty for this run.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)
class ScreenshotTest {

    private lateinit var scenario: ActivityScenario<MainActivity>
    private val device: UiDevice =
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    private val storage = TestStorage()

    @After
    fun teardown() {
        if (::scenario.isInitialized) {
            try { scenario.close() } catch (_: Throwable) {}
        }
    }

    @Test
    fun a_home() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
        device.waitForIdle(2_000)
        Thread.sleep(1_500)
        screenshot("01_home")
    }

    private fun screenshot(name: String) {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        var bitmap: Bitmap? = null
        repeat(3) { attempt ->
            bitmap = instrumentation.uiAutomation.takeScreenshot()
            if (bitmap != null) return@repeat
            Log.w(TAG, "takeScreenshot returned null on attempt ${attempt + 1}/3 for $name; retrying after 1500ms")
            device.waitForIdle(1_000)
            Thread.sleep(1_500)
        }
        val captured = bitmap
        if (captured == null) {
            Log.e(TAG, "takeScreenshot returned null after 3 attempts for $name; skipping (no PNG written)")
            return
        }
        storage.openOutputFile("$name.png").use {
            captured.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    }

    companion object { private const val TAG = "KataScreenshot" }
}
