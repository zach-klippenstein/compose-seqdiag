package com.zachklipp.seqdiag

import androidx.compose.runtime.Composable

interface SequenceDiagramScope {
    val diagramStyle: SequenceDiagramStyle

    fun createParticipant(
        topLabel: @Composable () -> Unit,
        bottomLabel: (@Composable () -> Unit)?
    ): Participant

    infix fun Participant.lineTo(other: Participant): LineBuilder

    fun noteOver(
        participants: List<Participant>,
        label: @Composable () -> Unit
    )

    fun noteToStartOf(participant: Participant, label: @Composable () -> Unit)
    fun noteToEndOf(participant: Participant, label: @Composable () -> Unit)
}

fun SequenceDiagramScope.createParticipant(
    topAndBottomLabel: @Composable () -> Unit
) = createParticipant(
    topLabel = topAndBottomLabel,
    bottomLabel = topAndBottomLabel
)

fun SequenceDiagramScope.noteOver(
    firstParticipant: Participant,
    vararg otherParticipants: Participant,
    label: @Composable () -> Unit
) = noteOver(
    participants = ArrayList<Participant>(otherParticipants.size + 1).also {
        it += firstParticipant
        it.addAll(otherParticipants)
    },
    label = label
)