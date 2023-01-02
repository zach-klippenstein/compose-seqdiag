package com.zachklipp.seqdiag

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.google.testing.junit.testparameterinjector.TestParameter
import org.junit.Test

@Suppress("JUnitMalformedDeclaration")
internal class ArrowTest : BaseSnapshotTest() {

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
        @TestParameter head: ArrowHeadType,
        @TestParameter height: SelfArrowTestHeight,
    ) {
        snapshot {
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
        @TestParameter startHead: ArrowHeadType,
        @TestParameter endHead: ArrowHeadType,
    ) {
        snapshot {
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