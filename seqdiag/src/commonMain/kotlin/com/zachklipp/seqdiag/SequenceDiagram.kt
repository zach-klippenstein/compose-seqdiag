package com.zachklipp.seqdiag

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.layout.Layout
import com.zachklipp.seqdiag.layout.SequenceDiagramState

/**
 * A composable that draws a [sequence diagram](https://en.wikipedia.org/wiki/Sequence_diagram).
 * The diagram is specified inside the [content] function by a DSL defined by
 * [SequenceDiagramScope].
 *
 * Sequence diagrams consist of vertical participants with lines and arrows between them showing a
 * sequence of interactions. Inside [content], participants are created via [createParticipant].
 * Lines between participants are specified by the [lineTo][SequenceDiagramScope.lineTo] function.
 *
 * Diagrams can also be decorated with notes anchored to participants via the
 * [noteToStartOf][SequenceDiagramScope.noteToStartOf],
 * [noteToEndOf][SequenceDiagramScope.noteToEndOf], and [noteOver] functions. Notes can contain
 * arbitrary composable content, but the [Note] composable is recommended for consistent styling.
 *
 * Since diagrams have a very complex layout, this composable does not try to rearrange the diagram
 * to fit in the incoming constraints if it does not fit. Instead, the entire diagram will be scaled
 * down to fit the max constraints.
 *
 * @param style The default layout and drawing properties for the diagram. See the documentation on
 * [SequenceDiagramStyle] for more information.
 * @param content The function that specifies the contents of the diagram by calling methods on
 * the [SequenceDiagramScope] receiver.
 *
 * @sample com.zachklipp.seqdiag.samples.NotesAroundParticipant
 * @sample com.zachklipp.seqdiag.samples.BasicSequenceDiagram
 */
@Composable
fun SequenceDiagram(
    modifier: Modifier = Modifier,
    style: SequenceDiagramStyle = SequenceDiagramStyle.Default,
    content: SequenceDiagramScope.() -> Unit
) {
    val state = remember { SequenceDiagramState() }
    state.diagramStyle = style
    state.buildDiagram(content)

    Layout(
        modifier = modifier
            // This allows the layout logic to be a lot simpler since it doesn't have to consider
            // max constraints.
            .scaleToFitMaxConstraints()
            .drawWithCache { with(state) { draw() } },
        content = { state.Content() },
        measurePolicy = state
    )
}

/**
 * Identifies a participant in a [SequenceDiagram]. Participants are created by [createParticipant]
 * and used by the other functions on [SequenceDiagramScope] to specify the targets for lines and
 * the anchors for notes.
 */
sealed interface Participant