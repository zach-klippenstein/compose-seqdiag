package com.zachklipp.seqdiag

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect.Companion.dashPathEffect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.takeOrElse

internal val LocalTextStyle = compositionLocalOf { RootTextStyle }

/**
 * The default [TextStyle] used for [LocalTextStyle]. May be overridden using [OverrideTextStyle].
 */
@OptIn(ExperimentalTextApi::class)
internal val RootTextStyle
    get() = TextStyle(
        textAlign = TextAlign.Center,
        platformStyle = RootPlatformTextStyle
    )

@OptIn(ExperimentalTextApi::class)
internal expect val RootPlatformTextStyle: PlatformTextStyle

/**
 * Defines the default layout and drawing properties of a [SequenceDiagram].
 * [BasicSequenceDiagramStyle] is an immutable implementation of this interface.
 *
 * @sample com.zachklipp.seqdiag.samples.Styling
 */
interface SequenceDiagramStyle {
    /**
     * The minimum amount of space between [Participant]s in the diagram.
     */
    val participantSpacing: Dp

    /**
     * The spacing between rows in the diagram.
     */
    val verticalSpacing: Dp

    /**
     * The amount of space between labels and their anchors. This is also used for the amount of
     * space that spanning labels overlap their bounding [Participant]s.
     */
    val labelPadding: Dp

    /**
     * The [TextStyle] used for [Label]s.
     *
     * @see Label
     * @see Note
     */
    val labelTextStyle: TextStyle

    /**
     * The padding used internally [Note] composables.
     *
     * @see Note
     */
    val notePadding: PaddingValues

    /**
     * The [Shape] used for all [Note] composables.
     *
     * @see Note
     */
    val noteShape: Shape

    /**
     * The [Brush] used to draw the background of all [Note] composables.
     *
     * @see Note
     */
    val noteBackgroundBrush: Brush

    /**
     * Properties that control how lines are rendered for both [Participant]s and [LineBuilder]s.
     * See [LineStyle] documentation for more information.
     */
    val lineStyle: LineStyle

    /**
     * If true, labels will be measured with constraints that attempt to make their dimensions
     * closer to square. This means long labels will be broken into multiple lines.
     *
     * @sample com.zachklipp.seqdiag.samples.DimensionBalancing
     */
    val balanceLabelDimensions: Boolean

    companion object {
        val Default: SequenceDiagramStyle = BasicSequenceDiagramStyle()
    }
}

/**
 * An immutable [SequenceDiagramStyle].
 */
data class BasicSequenceDiagramStyle(
    override val participantSpacing: Dp = DefaultParticipantSpacing,
    override val verticalSpacing: Dp = DefaultVerticalSpacing,
    override val labelPadding: Dp = DefaultLabelPadding,
    override val labelTextStyle: TextStyle = DefaultLabelTextStyle,
    override val notePadding: PaddingValues = DefaultNotePadding,
    override val noteShape: Shape = DefaultNoteShape,
    override val noteBackgroundBrush: Brush = DefaultNoteBackgroundBrush,
    override val lineStyle: LineStyle = DefaultLineStyle,
    override val balanceLabelDimensions: Boolean = true
) : SequenceDiagramStyle

/**
 * Properties that control how lines and arrows are rendered.
 *
 * @param brush The [Brush] used to draw lines and arrows.
 * @param arrowHeadType The [ArrowHeadType] of arrows, when appropriate.
 * @param width The width of the stroke, in [Dp]. See [Stroke.width].
 * @param miter The miter of the stroke, in [Dp]. See [Stroke.miter].
 * @param cap The cap of the stroke. See [Stroke.cap].
 * @param join The join of the stroke. See [Stroke.join].
 * @param dashIntervals If non-null, lines will be drawn as dashed lines, with the first element of
 * the pair [Dp] on, and the second element off. E.g. `10.dp to 10.dp` is equivalent to
 * `dashedPathEffect(floatArrayOf(10.dp.toPx(), 10.dp.toPx()))`.
 */
data class LineStyle(
    val brush: Brush? = null,
    val arrowHeadType: ArrowHeadType? = null,
    val width: Dp = Dp.Unspecified,
    val miter: Dp = Dp.Unspecified,
    val cap: StrokeCap? = null,
    val join: StrokeJoin? = null,
    val dashIntervals: Pair<Dp, Dp>? = null
) {
    /**
     * Returns a [LineStyle] that is a copy of this instance, with any unspecified properties set
     * from their corresponding properties in [other].
     */
    fun fillMissingFrom(other: LineStyle?): LineStyle = LineStyle(
        brush = brush ?: other?.brush,
        width = width.takeOrElse { other?.width ?: Dp.Unspecified },
        arrowHeadType = arrowHeadType ?: other?.arrowHeadType,
        miter = miter.takeOrElse { other?.miter ?: Dp.Unspecified },
        cap = cap ?: other?.cap,
        join = join ?: other?.join,
        dashIntervals = dashIntervals ?: other?.dashIntervals
    )
}

internal fun LineStyle.toLineStroke(density: Density): Stroke = with(density) {
    Stroke(
        width = width.takeOrElse { DefaultLineWidth }.toPx(),
        miter = if (miter.isSpecified) miter.toPx() else Stroke.DefaultMiter,
        cap = cap ?: Stroke.DefaultCap,
        join = join ?: Stroke.DefaultJoin,
        pathEffect = dashIntervals?.let {
            dashPathEffect(floatArrayOf(it.first.toPx(), it.second.toPx()))
        }
    )
}

private val DefaultParticipantSpacing = 16.dp
private val DefaultVerticalSpacing = 16.dp
private val DefaultLabelPadding = 8.dp
private val DefaultNotePadding = PaddingValues(8.dp)
private val DefaultLabelTextStyle = TextStyle(color = Color.Black)
private val DefaultNoteShape = RectangleShape
private val DefaultNoteBackgroundBrush = SolidColor(Color.White)
internal val DefaultLineBrush = SolidColor(Color.Black)
internal val DefaultLineWidth = 2.dp
internal val DefaultArrowHeadType = ArrowHeadType.Filled
private val DefaultLineStyle = LineStyle()

/**
 * Modifies the [LocalTextStyle] for [content] by calling the [style] transformation function.
 */
@Composable
internal fun OverrideTextStyle(style: (TextStyle) -> TextStyle, content: @Composable () -> Unit) {
    val oldStyle = LocalTextStyle.current
    CompositionLocalProvider(
        LocalTextStyle provides style(oldStyle),
        content = content
    )
}