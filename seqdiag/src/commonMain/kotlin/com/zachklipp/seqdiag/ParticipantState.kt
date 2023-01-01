package com.zachklipp.seqdiag

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import kotlin.math.absoluteValue

internal class ParticipantState(
    var index: Int,
    val topLabel: (@Composable () -> Unit)?,
    val bottomLabel: (@Composable () -> Unit)?
) : Participant {
    var topLabelMeasurable: Measurable? = null
    var bottomLabelMeasurable: Measurable? = null
    var topLabelPlaceable: Placeable? by mutableStateOf(null)
    var bottomLabelPlaceable: Placeable? by mutableStateOf(null)
    var left: Int by mutableStateOf(0)
    var topLabelTop: Int by mutableStateOf(0)
    var bottomLabelTop: Int by mutableStateOf(0)

    val topLabelHeight: Int get() = topLabelPlaceable?.height ?: 0

    val labelWidth: Int
        get() = maxOf(
            topLabelPlaceable?.width ?: 0,
            bottomLabelPlaceable?.width ?: 0
        )

    var centerXOffset: Int
        get() = left + labelWidth / 2
        set(value) {
            left = value - labelWidth / 2
        }
}

internal fun ParticipantState.isAdjacentTo(other: ParticipantState): Boolean =
    (this.index - other.index).absoluteValue == 1

internal fun Participant.isBefore(other: Participant): Boolean =
    (this as ParticipantState).index < (other as ParticipantState).index