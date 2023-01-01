package com.zachklipp.seqdiag

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.takeOrElse

/**
 * Standard note styled by the [SequenceDiagramStyle]. To just draw text without the border use the
 * [Label] composable. To use your own composables with the standard note background and border,
 * use [NoteBox].
 *
 * This is a convenience function for wrapping a [Label] inside a [NoteBox].
 *
 * @sample com.zachklipp.seqdiag.samples.NotesAroundParticipant
 */
@Composable
fun SequenceDiagramScope.Note(text: String, modifier: Modifier = Modifier) {
    NoteBox(modifier) {
        Label(text)
    }
}

/**
 * Standard text styled by the [SequenceDiagramStyle]. To draw a standard background and border
 * around a label, use the [Note] composable.
 *
 * @sample com.zachklipp.seqdiag.samples.NotesAroundParticipant
 */
@Composable
fun SequenceDiagramScope.Label(text: String, modifier: Modifier = Modifier) {
    val style = LocalTextStyle.current.merge(diagramStyle.labelTextStyle)
    BasicText(text, modifier = modifier, style = style)
}

/**
 * A [Box] that is styled according to the [SequenceDiagramStyle]'s note properties.
 */
@Composable
inline fun SequenceDiagramScope.NoteBox(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier.then(noteBoxModifier()),
        propagateMinConstraints = true,
        content = content
    )
}

// TODO when context receivers are supported, expose this as a Modifier.style factory function
//  on the scope, and deprecate the NoteBox composable.
@PublishedApi
internal fun SequenceDiagramScope.noteBoxModifier(): Modifier = with(diagramStyle) {
    Modifier
        .border(
            width = lineStyle.width.takeOrElse { DefaultLineWidth },
            brush = lineStyle.brush ?: DefaultLineBrush,
            shape = noteShape
        )
        .background(
            brush = noteBackgroundBrush,
            shape = noteShape
        )
        .padding(notePadding)
}

//@Preview
@Composable
private fun LabelPreview() {
    with(PreviewSequenceDiagramScope) {
        Label(text = "Hello world")
    }
}

//@Preview
@Composable
private fun NotePreview() {
    with(PreviewSequenceDiagramScope) {
        Note(text = "Hello world")
    }
}

private val PreviewSequenceDiagramScope = object : SequenceDiagramScope {
    override val diagramStyle: SequenceDiagramStyle = SequenceDiagramStyle.Default

    override fun createParticipant(
        topLabel: (@Composable () -> Unit)?,
        bottomLabel: (@Composable () -> Unit)?
    ): Participant = throw UnsupportedOperationException()

    override fun Participant.lineTo(other: Participant): LineBuilder =
        throw UnsupportedOperationException()

    override fun noteOver(participants: Collection<Participant>, label: @Composable () -> Unit) =
        throw UnsupportedOperationException()

    override fun noteToStartOf(participant: Participant, label: @Composable () -> Unit): Unit =
        throw UnsupportedOperationException()

    override fun noteToEndOf(participant: Participant, label: @Composable () -> Unit): Unit =
        throw UnsupportedOperationException()
}