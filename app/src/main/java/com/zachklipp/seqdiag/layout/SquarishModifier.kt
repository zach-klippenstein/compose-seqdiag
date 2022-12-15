package com.zachklipp.seqdiag.layout

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset

internal fun Modifier.squarish(): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(getSquarishConstraints(measurable))
    layout(placeable.width, placeable.height) {
        placeable.place(IntOffset.Zero)
    }
}

private fun getSquarishConstraints(measurable: IntrinsicMeasurable): Constraints {
    val minWidth = measurable.minIntrinsicWidth(Constraints.Infinity)
    val maxWidth = measurable.maxIntrinsicWidth(Constraints.Infinity)

    // Try to get a square-ish size, assuming the label contains mostly text.
    return Constraints(
        minWidth = minWidth,
        maxWidth = minWidth + (maxWidth - minWidth) / 2,
        minHeight = 0,
        maxHeight = Constraints.Infinity
    )
}