package com.zachklipp.seqdiag

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.dp
import com.google.testing.junit.testparameterinjector.TestParameter
import org.junit.Test

@Suppress("JUnitMalformedDeclaration")
class SequenceDiagramTest : BaseSnapshotTest() {

    @Test
    fun participantLabels(
        @TestParameter("null", "L", "Very long label", "multiline\nlabel")
        topStartLabel: String?,
        @TestParameter("null", "L", "Very long label", "multiline\nlabel")
        topEndLabel: String?,
        @TestParameter("null", "L", "Very long label", "multiline\nlabel")
        bottomStartLabel: String?,
        @TestParameter("null", "L", "Very long label", "multiline\nlabel")
        bottomEndLabel: String?,
    ) {
        snapshot {
            SequenceDiagram {
                createParticipant(
                    topLabel = { topStartLabel?.let { Note(it) } },
                    bottomLabel = { bottomStartLabel?.let { Note(it) } }
                )
                createParticipant(
                    topLabel = { topEndLabel?.let { (Note(it)) } },
                    bottomLabel = { bottomEndLabel?.let { Note(it) } }
                )
            }
        }
    }

    @Test
    fun unevenParticipantLabels() {
        snapshot {
            SequenceDiagram {
                createParticipant(
                    topLabel = { Note("Multiline\nlabel") },
                    bottomLabel = { Note("Alice") }
                )
                createParticipant(
                    topLabel = { Note("Bob") },
                    bottomLabel = { Note("Multiline\nlabel") }
                )
            }
        }
    }

    @Test
    fun singleLine(
        @TestParameter forward: Boolean,
        @TestParameter("null", "long label that shouldn't fit") label: String?,
        @TestParameter spanning: Boolean,
    ) {
        snapshot {
            SequenceDiagram(
                style = BasicSequenceDiagramStyle(
                    balanceLabelDimensions = false
                )
            ) {
                val alice = createParticipant { Note("Alice") }
                if (spanning) {
                    createParticipant { Note("Carlos") }
                }
                val bob = createParticipant { Note("Bob") }

                val line = if (forward) {
                    alice.lineTo(bob)
                } else {
                    bob.lineTo(alice)
                }
                label?.let {
                    line.label { Label(it) }
                }
            }
        }
    }

    @Test
    fun noteOver(
        @TestParameter("short", "long label that shouldn't fit") label: String,
        @TestParameter spanning: Boolean,
    ) {
        snapshot {
            SequenceDiagram(
                style = BasicSequenceDiagramStyle(
                    balanceLabelDimensions = false
                )
            ) {
                val alice = createParticipant { Note("Alice") }
                if (spanning) {
                    createParticipant { Note("Carlos") }
                }
                val bob = createParticipant { Note("Bob") }

                noteOver(alice, bob) { Label(label) }
            }
        }
    }

    // Don't use Dp parameters directly to avoid mangling the test name.
    @Test
    fun horizontalSpacing(
        @TestParameter("0", "10") participantSpacing: Float,
        @TestParameter("0", "10") labelPadding: Float,
        @TestParameter longStartNote: Boolean,
        @TestParameter longMiddleNote: Boolean,
        @TestParameter longEndNote: Boolean,
    ) {
        val style = BasicSequenceDiagramStyle(
            // Save space.
            verticalSpacing = 0.dp,
            participantSpacing = participantSpacing.dp,
            labelPadding = labelPadding.dp,
            notePadding = PaddingValues(0.dp),
            balanceLabelDimensions = false
        )
        val shortNote = "L"
        val longNote = "Long label that should push things"

        snapshot {
            SequenceDiagram(style = style) {
                val start = createParticipant { Note("Start") }
                val middle = createParticipant { Note("Middle") }
                val end = createParticipant { Note("End") }

                noteToStartOf(start) {
                    Note(if (longStartNote) longNote else shortNote)
                }
                noteToEndOf(start) {
                    Note(if (longEndNote) longNote else shortNote)
                }

                noteOver(middle) {
                    Note(if (longMiddleNote) longNote else shortNote)
                }

                noteToStartOf(end) {
                    Note(if (longStartNote) longNote else shortNote)
                }
                noteToEndOf(end) {
                    Note(if (longEndNote) longNote else shortNote)
                }
            }
        }
    }

    // Don't use Dp parameters directly to avoid mangling the test name.
    @Test
    fun verticalSpacing(@TestParameter("0", "10") verticalSpacing: Float) {
        val style = BasicSequenceDiagramStyle(
            verticalSpacing = verticalSpacing.dp,
            // Save space.
            participantSpacing = 0.dp,
            labelPadding = 0.dp,
            notePadding = PaddingValues(0.dp),
            balanceLabelDimensions = false
        )

        snapshot {
            SequenceDiagram(style = style) {
                val start = createParticipant { Note("Start") }
                val end = createParticipant { Note("End") }

                noteToStartOf(start) { Note("note") }
                noteToEndOf(start) { Note("note") }
                noteOver(start) { Note("note") }
                noteOver(start, end) { Note("note") }

                start.lineTo(end)
                start.lineTo(end).label { Label("label") }
            }
        }
    }
}