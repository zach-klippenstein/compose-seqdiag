package com.zachklipp.seqdiag

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke

/**
 * A fluent-style builder interface for configuring optional properties of lines pointing from one
 * [Participant] to another.
 */
interface LineBuilder {
    /**
     * Specifies the color of the line. If this is called after [brush], this call will overwrite
     * the brush.
     */
    fun color(color: Color): LineBuilder = brush(SolidColor(color))

    /**
     * Specifies a [Brush] to use to draw the line. If this is called after [color], this call will
     * overwrite the color.
     */
    fun brush(brush: Brush): LineBuilder

    /**
     * Specifies the [Stroke] used to draw the line.
     */
    fun stroke(stroke: Stroke): LineBuilder

    /**
     * The style of the arrow head drawn at the end of the line. See [ArrowHeadType] for possible
     * values. To configure the default type used for all arrows, specify the
     * [SequenceDiagramStyle.arrowHeadType].
     */
    fun arrowHeadType(type: ArrowHeadType): LineBuilder

    /**
     * Specifies a composable to use as the label for the line.
     * In most cases this should be a [Note] or [Label] composable.
     *
     * The label will be placed on top of the line. The width of the label depends on what kind of
     * line this is:
     *
     * - If this is a line to the same participant (i.e. a cycle) **or** a line between two adjacent
     *   [Participant]s, the label is measured to the maximum intrinsic width of everything sharing
     *   its column and a zero minimum width.
     * - Otherwise, the line spans multiple [Participant]s, and the label is measured with the fixed
     *   width equal to the distance between those participants' lines.
     */
    fun label(content: @Composable () -> Unit): LineBuilder
}