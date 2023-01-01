package com.zachklipp.seqdiag

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.zachklipp.seqdiag.samples.DiagramDevDemo
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@Suppress("JUnitMalformedDeclaration")
@RunWith(TestParameterInjector::class)
internal class DiagramDemoRenderingTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5,
        showSystemUi = false,
        renderingMode = SessionParams.RenderingMode.SHRINK,
    )

    @Test
    fun demoRendering(
        @TestParameter balanceLabels: Boolean,
        @TestParameter layoutDirection: LayoutDirection,
    ) {
        paparazzi.snapshot {
            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                DiagramDevDemo(balanceLabelDimensions = balanceLabels)
            }
        }
    }
}