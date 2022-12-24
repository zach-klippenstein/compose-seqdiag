package com.zachklipp.seqdiag

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal val LocalTextStyle = compositionLocalOf { RootTextStyle }

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
     */
    val labelTextStyle: TextStyle

    /**
     * The padding used internally [Note] composables.
     */
    val notePadding: PaddingValues
    val noteShape: Shape
    val noteBackgroundBrush: Brush

    val lineBrush: Brush
    val lineWeight: Dp

    /**
     * If true, labels will be measured with constraints that attempt to make their dimensions
     * closer to square. This means long labels will be broken into multiple lines.
     */
    val balanceLabelDimensions: Boolean

    companion object {
        val Default: SequenceDiagramStyle = BasicSequenceDiagramStyle()
    }
}

internal fun SequenceDiagramStyle.getLineStroke(density: Density): Stroke = with(density) {
    Stroke(lineWeight.toPx())
}

data class BasicSequenceDiagramStyle(
    override val participantSpacing: Dp = DefaultParticipantSpacing,
    override val verticalSpacing: Dp = DefaultVerticalSpacing,
    override val labelPadding: Dp = DefaultLabelPadding,
    override val labelTextStyle: TextStyle = DefaultLabelTextStyle,
    override val notePadding: PaddingValues = DefaultNotePadding,
    override val noteShape: Shape = DefaultNoteShape,
    override val noteBackgroundBrush: Brush = DefaultNoteBackgroundBrush,
    override val lineBrush: Brush = DefaultLineBrush,
    override val lineWeight: Dp = DefaultLineWeight,
    override val balanceLabelDimensions: Boolean = true
) : SequenceDiagramStyle

private val DefaultParticipantSpacing = 16.dp
private val DefaultVerticalSpacing = 16.dp
private val DefaultLabelPadding = 8.dp
private val DefaultNotePadding = PaddingValues(8.dp)
private val DefaultLabelTextStyle = TextStyle(color = Color.Black)
private val DefaultNoteShape = RectangleShape
private val DefaultNoteBackgroundBrush = SolidColor(Color.White)
private val DefaultLineBrush = SolidColor(Color.Black)
private val DefaultLineWeight = 2.dp

@Suppress("DEPRECATION")
internal val RootTextStyle = TextStyle(
    textAlign = TextAlign.Center,
    platformStyle = PlatformTextStyle(includeFontPadding = false)
)

@Composable
internal fun OverrideTextStyle(style: (TextStyle) -> TextStyle, content: @Composable () -> Unit) {
    val oldStyle = LocalTextStyle.current
    CompositionLocalProvider(
        LocalTextStyle provides style(oldStyle),
        content = content
    )
}