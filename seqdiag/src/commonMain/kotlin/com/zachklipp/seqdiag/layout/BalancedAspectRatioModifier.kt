package com.zachklipp.seqdiag.layout

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import kotlin.math.sqrt

internal fun Modifier.balancedAspectRatio(): Modifier = this.then(BalancedAspectRatioModifier)

private object BalancedAspectRatioModifier : LayoutModifier {

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        val width = calculateMaxWidth(measurable)
        val placeable = measurable.measure(constraints.copy(maxWidth = width))
        return layout(placeable.width, placeable.height) {
            placeable.place(IntOffset.Zero)
        }
    }

    override fun IntrinsicMeasureScope.maxIntrinsicWidth(
        measurable: IntrinsicMeasurable,
        height: Int
    ): Int = calculateMaxWidth(measurable, height)

    private fun calculateMaxWidth(
        measurable: IntrinsicMeasurable,
        height: Int = Constraints.Infinity
    ): Int {
        // Assume the measurable contains text, or behaves like it does. So the min width is the
        // width of the longest word, and assume that if we start with the min width, roughly every
        // min-width pixels the measurable's height will decrease and width will increase.
        // We calculate how many of these min-width "columns" fit in the max width, then take the
        // square-root of that count to estimate a width.
        val maxWidth = measurable.maxIntrinsicWidth(height)
        val minWidth = measurable.minIntrinsicWidth(height)
        return if (maxWidth == minWidth) maxWidth else {
            val columns = maxWidth.toFloat() / minWidth.toFloat()
            val squareColumns = sqrt(columns)
            (squareColumns * minWidth).roundToInt()
        }
    }
}

//@Preview(showBackground = true)
@Composable
private fun SmallWordPreview() {
    Column {
        repeat(10) {
            BasicText(
                "a ".repeat(it),
                Modifier
                    .border(0.dp, Color.Black)
                    .balancedAspectRatio()
            )
        }
    }
}

//@Preview(showBackground = true)
@Composable
private fun MediumWordPreview() {
    Column {
        repeat(10) {
            BasicText(
                "bbbbb ".repeat(it),
                Modifier
                    .border(0.dp, Color.Black)
                    .drawIntrinsics()
                    .balancedAspectRatio()
            )
        }
    }
}

//@Preview(showBackground = true)
@Composable
private fun LargeWordPreview() {
    Column {
        repeat(10) {
            BasicText(
                "cccccccccccccccc ".repeat(it),
                Modifier
                    .border(0.dp, Color.Black)
                    .drawIntrinsics()
                    .balancedAspectRatio()
            )
        }
    }
}

//@Preview(showBackground = true)
@Composable
private fun VariedWordPreview() {
    Column {
        BasicText(
            "The quick brown fox jumped over the lazy dogs. Lorem ipsum dolor set amor.",
            Modifier
                .border(0.dp, Color.Black)
                .drawIntrinsics()
                .balancedAspectRatio()
        )
    }
}

private fun Modifier.drawIntrinsics(): Modifier = composed {
    var maxIntrinsicWidth by remember { mutableStateOf(0) }
    Modifier
        .layout { measurable, constraints ->
            maxIntrinsicWidth = measurable.maxIntrinsicWidth(Constraints.Infinity)
            val placeable = measurable.measure(constraints)
            layout(placeable.width, placeable.height) {
                placeable.place(IntOffset.Zero)
            }
        }
        .drawBehind {
            drawLine(
                Color.Red,
                start = Offset(maxIntrinsicWidth.toFloat(), 0f),
                end = Offset(maxIntrinsicWidth.toFloat(), size.height)
            )
        }
}