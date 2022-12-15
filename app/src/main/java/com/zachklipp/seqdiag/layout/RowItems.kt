package com.zachklipp.seqdiag.layout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Density
import com.zachklipp.seqdiag.Participant

internal sealed class SequenceRowItem {
    var top: Int by mutableStateOf(0)
    var left: Int by mutableStateOf(0)
    abstract val height: Int
    abstract val width: Int

    @Composable
    abstract fun Content()
    abstract fun consumeMeasurables(nextMeasurable: () -> Measurable)
    abstract fun Placeable.PlacementScope.place(density: Density)
}

internal abstract class SingleParticipantRowItem : SequenceRowItem() {
    abstract val participant: Participant
    abstract fun measure()
}

internal abstract class SpanningRowItem : SequenceRowItem() {
    abstract val participants: List<Participant>
    abstract fun Density.measure(minWidth: Int, maxWidth: Int)
}