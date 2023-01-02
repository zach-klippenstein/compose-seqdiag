package com.zachklipp.seqdiag

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
abstract class BaseSnapshotTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5,
        showSystemUi = false,
        renderingMode = SessionParams.RenderingMode.SHRINK,
    )

    @TestParameter
    private lateinit var layoutDirection: LayoutDirection

    protected fun snapshot(content: @Composable () -> Unit) {
        paparazzi.snapshot {
            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                Box(Modifier.background(Color.Gray)) {
                    content()
                }
            }
        }
    }
}