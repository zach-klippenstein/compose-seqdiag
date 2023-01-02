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
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import com.zachklipp.seqdiag.DefaultArrowHeadType
import com.zachklipp.seqdiag.DefaultLineBrush
import com.zachklipp.seqdiag.HorizontalArrow
import com.zachklipp.seqdiag.Participant
import com.zachklipp.seqdiag.ParticipantState
import com.zachklipp.seqdiag.SequenceDiagramStyle
import com.zachklipp.seqdiag.toLineStroke
import com.zachklipp.seqdiag.isAdjacentTo
import com.zachklipp.seqdiag.isBefore

internal class Line(
    val from: ParticipantState,
    val to: ParticipantState,
    private val builder: LineBuilderImpl
) : SpanningRowItem() {
    private var measurable: Measurable? = null
    private var placeable: Placeable? by mutableStateOf(null)

    override val participants: List<Participant> = listOf(from, to)

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

    @Composable
    override fun Content(style: SequenceDiagramStyle) {
        Column(
            verticalArrangement = spacedBy(style.labelPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            builder.label?.let {
                // Only balance single-column labels, since multi-column ones are likely to have a
                // lot more space.
                val balanceLabel = style.balanceLabelDimensions && singleColumn
                Box(
                    propagateMinConstraints = true,
                    modifier = Modifier
                        .padding(horizontal = style.labelPadding)
                        .then(if (balanceLabel) Modifier.balancedAspectRatio() else Modifier)
                        .clearBackground()
                ) {
                    it()
                }
            }
            val forwards = from.isBefore(to)
            val resolvedStyle = builder.style.fillMissingFrom(style.lineStyle)
            HorizontalArrow(
                brush = resolvedStyle.brush ?: DefaultLineBrush,
                stroke = resolvedStyle.toLineStroke(LocalDensity.current),
                startHead = if (!forwards) {
                    resolvedStyle.arrowHeadType ?: DefaultArrowHeadType
                } else null,
                endHead = if (forwards) {
                    resolvedStyle.arrowHeadType ?: DefaultArrowHeadType
                } else null,
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