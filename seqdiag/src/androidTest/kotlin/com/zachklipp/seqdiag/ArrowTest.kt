package com.zachklipp.seqdiag

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import app.cash.paparazzi.detectEnvironment
import com.android.ide.common.rendering.api.SessionParams
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@Suppress("JUnitMalformedDeclaration")
@RunWith(TestParameterInjector::class)
internal class ArrowTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5,
        showSystemUi = false,
        renderingMode = SessionParams.RenderingMode.SHRINK,
    )

    @TestParameter
    lateinit var stroke: ArrowStrokeTestStyle

    @TestParameter
    lateinit var width: ArrowTestWidth

    @Suppress("unused")
    enum class ArrowStrokeTestStyle(val stroke: Stroke) {
        BasicStroke(Stroke()),
        ThickStroke(Stroke(width = 10f)),
    }

    @Suppress("unused")
    enum class ArrowTestWidth(val modifier: Modifier) {
        UnspecifiedSize(Modifier),
        Wide(Modifier.width(50.dp)),
    }

    @Suppress("unused")
    enum class SelfArrowTestHeight(val modifier: Modifier) {
        UnspecifiedSize(Modifier),
        Tall(Modifier.height(50.dp)),
    }

    @Test
    fun arrowToSelf(
        @TestParameter head: ArrowHead,
        @TestParameter height: SelfArrowTestHeight,
    ) {
        paparazzi.snapshot {
            ArrowToSelf(
                brush = SolidColor(Color.Red),
                stroke = stroke.stroke,
                head = head,
                modifier = Modifier
                    // Padding ensures full arrow is rendered for thick strokes.
                    .padding(10.dp)
                    .then(width.modifier)
                    .then(height.modifier)
            )
        }
    }

    @Test
    fun horizontalArrow(
        @TestParameter startHead: ArrowHead,
        @TestParameter endHead: ArrowHead,
    ) {
        paparazzi.snapshot {
            HorizontalArrow(
                brush = SolidColor(Color.Red),
                stroke = stroke.stroke,
                startHead = startHead,
                endHead = endHead,
                modifier = Modifier
                    // Padding ensures full arrow is rendered for thick strokes.
                    .padding(10.dp)
                    .then(width.modifier)
            )
        }
    }
}