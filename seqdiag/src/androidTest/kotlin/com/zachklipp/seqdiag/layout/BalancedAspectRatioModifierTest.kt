package com.zachklipp.seqdiag.layout

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@Suppress("JUnitMalformedDeclaration")
@RunWith(TestParameterInjector::class)
class BalancedAspectRatioModifierTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5,
        showSystemUi = false,
        renderingMode = SessionParams.RenderingMode.SHRINK,
    )

    @Suppress("unused")
    enum class Strings(val text: String) {
        Short("a"),
        LongUnbreakable("aaaaaaaaaaaaaaaaaaaaaaaaaaaaa"),
        LongBreakable("the quick brown fox jumped over the lazy dogs"),
        MultiLine("the\nquick\nbrown\nfox\njumped\nover\nthe\nlazy\ndogs"),
    }

    @Test
    fun wrapping(
        @TestParameter string: Strings,
        @TestParameter layoutDirection: LayoutDirection
    ) {
        paparazzi.snapshot {
            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                BasicText(string.text, modifier = Modifier.balancedAspectRatio())
            }
        }
    }
}