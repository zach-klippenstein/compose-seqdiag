package com.zachklipp.seqdiag

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

internal fun Modifier.scaleToFitMaxConstraints(): Modifier {
    return layout { measurable, constraints ->
        // Pass through minimum constraints.
        val childConstraints = constraints.copy(
            maxWidth = Constraints.Infinity,
            maxHeight = Constraints.Infinity
        )
        val placeable = measurable.measure(childConstraints)
        val scaleFactor = if (
            placeable.width <= constraints.maxWidth &&
            placeable.height <= constraints.maxHeight
        ) 1f else {
            minOf(
                constraints.maxWidth / placeable.width.toFloat(),
                constraints.maxHeight / placeable.height.toFloat()
            )
        }

        layout(
            (placeable.width * scaleFactor).roundToInt(),
            (placeable.height * scaleFactor).roundToInt()
        ) {
            if (scaleFactor == 1f) {
                placeable.place(IntOffset.Zero)
            } else {
                placeable.placeWithLayer(IntOffset.Zero) {
                    // Default origin is center.
                    transformOrigin = TransformOrigin(0f, 0f)
                    scaleX = scaleFactor
                    scaleY = scaleFactor
                }
            }
        }
    }
}

//@Preview
@Composable
private fun ScaleToFitMaxConstraintsPreviewFits() {
    Box(
        Modifier
            .size(100.dp)
            .background(Color.DarkGray),
    ) {
        Box(
            Modifier
                .border(1.dp, Color.Red)
                .scaleToFitMaxConstraints()
                .size(80.dp, 80.dp)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center,
        ) {
            BasicText("Hello world")
        }
    }
}

//@Preview
@Composable
private fun ScaleToFitMaxConstraintsPreviewFitsWithMinimum() {
    Box(
        Modifier
            .size(100.dp)
            .background(Color.DarkGray),
        propagateMinConstraints = true
    ) {
        Box(
            Modifier
                .border(1.dp, Color.Red)
                .scaleToFitMaxConstraints()
                .size(90.dp, 90.dp)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center,
        ) {
            BasicText("Hello world")
        }
    }
}

//@Preview
@Composable
private fun ScaleToFitMaxConstraintsPreviewTooLarge() {
    Box(
        Modifier
            .size(100.dp, 75.dp)
            .background(Color.DarkGray),
    ) {
        Box(
            Modifier
                .border(1.dp, Color.Red)
                .scaleToFitMaxConstraints()
                .size(100.dp, 150.dp)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center,
        ) {
            BasicText("Hello world")
        }
    }
}