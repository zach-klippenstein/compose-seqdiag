package com.zachklipp.seqdiag.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import com.zachklipp.seqdiag.Participant
import com.zachklipp.seqdiag.SequenceDiagramStyle

/**
 * A note that is anchored to two participants. Its size is determined by the distance between
 * the participants and the max distance is determined by that distance plus the participants'
 * sizes.
 */
internal class SpanningNote(
    override val participants: Collection<Participant>,
    val label: @Composable () -> Unit,
    private val style: SequenceDiagramStyle,
) : SpanningRowItem() {

    private var measurable: Measurable? = null
    private var placeable: Placeable? by mutableStateOf(null)

    override val height: Int
        get() = placeable?.height ?: 0
    override val width: Int
        get() = placeable?.width ?: 0

    @Composable
    override fun Content(style: SequenceDiagramStyle) {
        Box(
            propagateMinConstraints = true,
            modifier = Modifier.clearBackground()
        ) {
            label()
        }
    }

    override fun consumeMeasurables(nextMeasurable: () -> Measurable) {
        measurable = nextMeasurable()
    }

    override fun Density.measure(minWidth: Int, maxWidth: Int) {
        placeable = measurable!!.measure(
            Constraints(
                minWidth = minWidth + style.labelPadding.roundToPx() * 2,
                maxWidth = maxWidth
            )
        )
    }

    override fun Placeable.PlacementScope.place(density: Density) {
        with(density) {
            placeable!!.placeRelative(left - style.labelPadding.roundToPx(), top)
        }
    }
}