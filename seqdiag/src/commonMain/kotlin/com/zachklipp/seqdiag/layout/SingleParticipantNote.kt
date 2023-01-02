package com.zachklipp.seqdiag.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import com.zachklipp.seqdiag.ParticipantState
import com.zachklipp.seqdiag.SequenceDiagramStyle
import com.zachklipp.seqdiag.layout.SingleParticipantRowItem.ParticipantAlignment.End
import com.zachklipp.seqdiag.layout.SingleParticipantRowItem.ParticipantAlignment.Over
import com.zachklipp.seqdiag.layout.SingleParticipantRowItem.ParticipantAlignment.Start

/**
 * A note that is anchored to a single participant. Its size is unbounded.
 */
internal class SingleParticipantNote(
    override val participant: ParticipantState,
    override val participantAlignment: ParticipantAlignment,
    private val label: @Composable () -> Unit,
    private val style: SequenceDiagramStyle,
) : SingleParticipantRowItem() {

    private var labelMeasurable: Measurable? = null
    private var labelPlaceable: Placeable? by mutableStateOf(null)

    override val width: Int
        get() = labelPlaceable?.width ?: 0

    override val height: Int
        get() = labelPlaceable?.height ?: 0

    override val maxIntrinsicWidth: Int
        get() = labelMeasurable?.maxIntrinsicWidth(Int.MAX_VALUE) ?: 0

    @Composable
    override fun Content(style: SequenceDiagramStyle) {
        Box(
            propagateMinConstraints = true,
            modifier = Modifier
                .then(
                    when (participantAlignment) {
                        Start -> Modifier.padding(end = style.labelPadding)
                        Over -> Modifier
                        End -> Modifier.padding(start = style.labelPadding)
                    }
                )
                .then(
                    if (style.balanceLabelDimensions) Modifier.balancedAspectRatio() else Modifier
                )
                .clearBackground()
        ) {
            label()
        }
    }

    override fun consumeMeasurables(nextMeasurable: () -> Measurable) {
        labelMeasurable = nextMeasurable()
    }

    override fun measure(maxWidth: Int) {
        labelPlaceable = labelMeasurable!!.measure(Constraints(maxWidth = maxWidth))
    }

    override fun Placeable.PlacementScope.place(density: Density) {
        labelPlaceable!!.placeRelative(left, top)
    }
}