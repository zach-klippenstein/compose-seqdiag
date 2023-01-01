package com.zachklipp.seqdiag

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SequenceDiagramScope.Note(text: String, modifier: Modifier = Modifier) {
    NoteBox(modifier) {
        Label(text)
    }
}

@Composable
fun SequenceDiagramScope.Label(text: String, modifier: Modifier = Modifier) {
    val style = LocalTextStyle.current?.merge(diagramStyle.labelTextStyle)
        ?: diagramStyle.labelTextStyle
    BasicText(text, modifier = modifier, style = style)
}

@Composable
inline fun SequenceDiagramScope.NoteBox(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    with(diagramStyle) {
        Box(
            modifier = modifier
                .border(
                    width = lineWeight,
                    brush = lineBrush,
                    shape = noteShape
                )
                .background(
                    brush = noteBackgroundBrush,
                    shape = noteShape
                )
                .padding(notePadding),
            propagateMinConstraints = true,
            content = content
        )
    }
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