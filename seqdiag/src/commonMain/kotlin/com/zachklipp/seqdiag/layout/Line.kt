package com.zachklipp.seqdiag.layout

import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import com.zachklipp.seqdiag.ArrowHeadType
import com.zachklipp.seqdiag.HorizontalArrow
import com.zachklipp.seqdiag.LineBuilder
import com.zachklipp.seqdiag.Participant
import com.zachklipp.seqdiag.ParticipantState
import com.zachklipp.seqdiag.SequenceDiagramStyle
import com.zachklipp.seqdiag.getLineStroke
import com.zachklipp.seqdiag.isAdjacentTo
import com.zachklipp.seqdiag.isBefore

internal class Line(
    val from: ParticipantState,
    val to: ParticipantState,
) : SpanningRowItem(), LineBuilder {
    private var measurable: Measurable? = null
    private var placeable: Placeable? by mutableStateOf(null)

    override val participants: List<Participant> = listOf(from, to)
    private var brush: Brush? by mutableStateOf(null)
    private var stroke by mutableStateOf<Stroke?>(null)
    private var arrowHeadType by mutableStateOf(ArrowHeadType.Filled)
    private var label by mutableStateOf<(@Composable () -> Unit)?>(null)

    /**
     * True if this line only occupies a single column and doesn't span any participants, i.e.
     * [from] and [to] are adjacent.
     */
    val singleColumn: Boolean
        get() = from.isAdjacentTo(to)
    val maxIntrinsicWidth: Int
        get() = measurable?.maxIntrinsicWidth(Constraints.Infinity) ?: 0

    override val height: Int
        get() = placeable?.height ?: 0
    override val width: Int
        get() = placeable?.width ?: 0

    override fun brush(brush: Brush): LineBuilder = apply { this.brush = brush }
    override fun stroke(stroke: Stroke): LineBuilder = apply { this.stroke = stroke }
    override fun arrowHeadType(type: ArrowHeadType): LineBuilder =
        apply { this.arrowHeadType = type }

    override fun label(content: @Composable () -> Unit): LineBuilder =
        apply { this.label = content }

    @Composable
    override fun Content(style: SequenceDiagramStyle) {
        Column(
            verticalArrangement = spacedBy(style.labelPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            label?.let {
                Box(
                    propagateMinConstraints = true,
                    modifier = Modifier.padding(horizontal = style.labelPadding)
                ) {
                    it()
                }
            }
            val forwards = from.isBefore(to)
            HorizontalArrow(
                brush = brush ?: style.lineBrush,
                stroke = stroke ?: style.getLineStroke(LocalDensity.current),
                startHead = if (!forwards) arrowHeadType else null,
                endHead = if (forwards) arrowHeadType else null,
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    override fun consumeMeasurables(nextMeasurable: () -> Measurable) {
        measurable = nextMeasurable()
    }

    override fun Density.measure(minWidth: Int, maxWidth: Int) {
        val constraints = Constraints(minWidth = minWidth, maxWidth = minWidth)
        placeable = measurable!!.measure(constraints)
    }

    override fun Placeable.PlacementScope.place(density: Density) {
        placeable!!.placeRelative(left, top)
    }
}