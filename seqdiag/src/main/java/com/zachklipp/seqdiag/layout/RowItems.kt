package com.zachklipp.seqdiag.layout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Density
import com.zachklipp.seqdiag.Participant
import com.zachklipp.seqdiag.SequenceDiagramStyle

internal sealed class SequenceRowItem {
    var top: Int by mutableStateOf(0)
    var left: Int by mutableStateOf(0)
    abstract val height: Int
    abstract val width: Int

    @Composable
    abstract fun Content(style: SequenceDiagramStyle)
    abstract fun consumeMeasurables(nextMeasurable: () -> Measurable)
    abstract fun Placeable.PlacementScope.place(density: Density)
}

internal abstract class SingleParticipantRowItem : SequenceRowItem() {
    /** The [Participant] this item is anchored to. */
    abstract val participant: Participant

    /** This position of this item relative to its [participant]. */
    abstract val participantAlignment: ParticipantAlignment
    abstract val maxIntrinsicWidth: Int

    /** Measures this item to fit within the given [maxWidth], at any height. */
    abstract fun measure(maxWidth: Int)

    /** The position of an item relative to its [participant]. */
    enum class ParticipantAlignment {
        Start, Over, End
    }
}

internal abstract class SpanningRowItem : SequenceRowItem() {
    abstract val participants: List<Participant>
    abstract fun Density.measure(minWidth: Int, maxWidth: Int)
}