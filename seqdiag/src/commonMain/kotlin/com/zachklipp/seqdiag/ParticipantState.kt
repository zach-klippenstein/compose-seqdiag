package com.zachklipp.seqdiag

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import com.zachklipp.seqdiag.Participant

internal class ParticipantState(
    var index: Int,
    val topLabel: @Composable () -> Unit,
    val bottomLabel: @Composable (() -> Unit)?
) : Participant {
    var topLabelMeasurable: Measurable? = null
    var bottomLabelMeasurable: Measurable? = null
    var topLabelPlaceable: Placeable? by mutableStateOf(null)
    var bottomLabelPlaceable: Placeable? by mutableStateOf(null)
    var left: Int by mutableStateOf(0)
    var topLabelTop: Int by mutableStateOf(0)
    var bottomLabelTop: Int by mutableStateOf(0)

    /** The width required to show the participant and all its row items. */
    var columnWidth: Int by mutableStateOf(0)

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