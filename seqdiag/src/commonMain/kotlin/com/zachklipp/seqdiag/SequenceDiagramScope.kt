package com.zachklipp.seqdiag

import androidx.compose.runtime.Composable

/**
 * Defines the DSL used to specify sequence diagrams. Receiver of the content parameter of the
 * [SequenceDiagram] composable.
 */
// This should not contain default methods that take composable lambdas: there's an intermittent
// ClassCastException because the composable lambda is being cast to a Function0 for some reason.
interface SequenceDiagramScope {
    /**
     * The [SequenceDiagramStyle] passed to [SequenceDiagram].
     */
    val diagramStyle: SequenceDiagramStyle

    /**
     * Creates a [Participant] in the sequence. Each participant typically is typically labeled and
     * has a vertical line drawn through the height of the diagram. The rest of the diagram is
     * specified as lines between participants and notes anchored to them. Participants are placed
     * horizontally from start to end in the order they're created.
     *
     * @param topLabel A composable that will be used as the label at the top of the participant.
     * In most cases this should be a [Note] composable.
     * @param bottomLabel A composable that will be used as the label at the bottom of the
     * participant. In most cases this should be a [Note] composable.
     */
    fun createParticipant(
        topLabel: @Composable (() -> Unit)?,
        bottomLabel: @Composable (() -> Unit)?
    ): Participant

    /**
     * Specifies a line drawn from this [Participant] to [other]. The optional properties of the
     * line can be configured by calling methods on the returned [LineBuilder].
     */
    infix fun Participant.lineTo(other: Participant): LineBuilder

    /**
     * Specifies a composable that represents a note that is placed "over", or covering, one or more
     * [participants].
     *
     * @param participants The list of [Participant]s that this note covers. Must contain at least
     * one element. If the note only covers a single participant, the width of the label will be
     * used to determine the space around that [Participant]. If it contains more than one element,
     * it will span from the start-most [Participant] to the end-most one. The order of the
     * collection does not matter.
     * @param label The composable that represents the note. In most cases this should be the [Note]
     * composable. If the [SequenceDiagramStyle.balanceLabelDimensions] flag is true, the label will
     * be measured with a max width less than its intrinsic max width to try to make it closer to
     * square.
     */
    fun noteOver(
        participants: Collection<Participant>,
        label: @Composable () -> Unit
    )

    /**
     * Specifies a composable that represents a note that is placed on the start side of a single
     * [Participant]s.
     *
     * @param participant The [Participant]s that this note is anchored to.
     * @param label The composable that represents the note. In most cases this should be the [Note]
     * composable. If the [SequenceDiagramStyle.balanceLabelDimensions] flag is true, the label will
     * be measured with a max width less than its intrinsic max width to try to make it closer to
     * square.
     */
    fun noteToStartOf(participant: Participant, label: @Composable () -> Unit)

    /**
     * Specifies a composable that represents a note that is placed on the end side of a single
     * [Participant].
     *
     * @param participant The [Participant]s that this note is anchored to.
     * @param label The composable that represents the note. In most cases this should be the [Note]
     * composable. If the [SequenceDiagramStyle.balanceLabelDimensions] flag is true, the label will
     * be measured with a max width less than its intrinsic max width to try to make it closer to
     * square.
     */
    fun noteToEndOf(participant: Participant, label: @Composable () -> Unit)
}

/**
 * Creates a [Participant] in the sequence. Each participant typically is typically labeled and
 * has a vertical line drawn through the height of the diagram. The rest of the diagram is
 * specified as lines between participants and notes anchored to them. Participants are placed
 * horizontally from start to end in the order they're created.
 *
 * This is a convenience method for calling [SequenceDiagramScope.createParticipant] with the same
 * label at the top and bottom.
 *
 * @param topAndBottomLabel A composable that will be used as the label at the top of the
 * participant. In most cases this should be a [Note] composable.
 */
// This is an extension because when it's a default interface method, there's an intermittent
// ClassCastException because the composable lambda is being cast to a Function0 for some reason.
fun SequenceDiagramScope.createParticipant(topAndBottomLabel: @Composable () -> Unit): Participant =
    createParticipant(
        topLabel = topAndBottomLabel,
        bottomLabel = topAndBottomLabel
    )

/**
 * Specifies a composable that represents a note that is placed "over", or covering, one or more
 * [Participant]s.
 *
 * @param firstParticipant The first (and possibly only) [Participant] that this note covers. If the
 * note only covers a single participant, the width of the label will be used to determine the space
 * around that [Participant]. If it contains more than one element, it will span from the start-most
 * [Participant] to the end-most one.
 * @param otherParticipants Other [Participant]s besides [firstParticipant] that this note covers.
 * Must be empty. The order of the list does not matter.
 * @param label The composable that represents the note. In most cases this should be the [Note]
 * composable. If the [SequenceDiagramStyle.balanceLabelDimensions] flag is true, the label will
 * be measured with a max width less than its intrinsic max width to try to make it closer to
 * square.
 */
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