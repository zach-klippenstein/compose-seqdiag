package com.zachklipp.seqdiag

import androidx.compose.runtime.Composable

// This should not contain default methods that take composable lambdas: there's an intermittent
// ClassCastException because the composable lambda is being cast to a Function0 for some reason.
interface SequenceDiagramScope {
    val diagramStyle: SequenceDiagramStyle

    fun createParticipant(
        topLabel: @Composable (() -> Unit)?,
        bottomLabel: @Composable (() -> Unit)?
    ): Participant

    infix fun Participant.lineTo(other: Participant): LineBuilder

    fun noteOver(
        participants: List<Participant>,
        label: @Composable () -> Unit
    )

    fun noteToStartOf(participant: Participant, label: @Composable () -> Unit)
    fun noteToEndOf(participant: Participant, label: @Composable () -> Unit)
}

// This is an extension because when it's a default interface method, there's an intermittent
// ClassCastException because the composable lambda is being cast to a Function0 for some reason.
fun SequenceDiagramScope.createParticipant(topAndBottomLabel: @Composable () -> Unit): Participant =
    createParticipant(
        topLabel = topAndBottomLabel,
        bottomLabel = topAndBottomLabel
    )

// This is an extension because when it's a default interface method, there's an intermittent
// ClassCastException because the composable lambda is being cast to a Function0 for some reason.
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