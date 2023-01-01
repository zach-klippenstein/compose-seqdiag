package com.zachklipp.seqdiag

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor

/**
 * A fluent-style builder interface for configuring optional properties of lines pointing from one
 * [Participant] to another.
 */
interface LineBuilder {
    /**
     * Specifies the [LineStyle] to use for the line and arrow. See the [LineStyle] documentation
     * for more information.
     *
     * If called multiple times, only the properties of [style] that are specified in later calls
     * will override earlier calls. E.g. the following two calls are equivalent:
     * ```
     * .style(LineStyle(brush = SolidColor(Color.Red)))
     * .style(LineStyle(width = 4.dp))
     *
     * // vs
     *
     * .style(LineStyle(brush = SolidColor(Color.Red), width = 4.dp))
     * ```
     */
    fun style(style: LineStyle): LineBuilder

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

/**
 * Specifies the color of the line.
 */
fun LineBuilder.color(color: Color): LineBuilder = style(LineStyle(brush = SolidColor(color)))

/**
 * The style of the arrow head drawn at the end of the line. See [ArrowHeadType] for possible
 * values.
 */
fun LineBuilder.arrowHeadType(type: ArrowHeadType): LineBuilder =
    style(LineStyle(arrowHeadType = type))
