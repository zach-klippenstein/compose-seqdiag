@file:JvmName("SequenceDiagramSamples")

package com.zachklipp.seqdiag.samples

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.zachklipp.seqdiag.ArrowHeadType
import com.zachklipp.seqdiag.BasicSequenceDiagramStyle
import com.zachklipp.seqdiag.Label
import com.zachklipp.seqdiag.LineStyle
import com.zachklipp.seqdiag.Note
import com.zachklipp.seqdiag.SequenceDiagram
import com.zachklipp.seqdiag.arrowHeadType
import com.zachklipp.seqdiag.color
import com.zachklipp.seqdiag.createParticipant
import com.zachklipp.seqdiag.noteOver

// TODO These should be moved to a separate source set or module when that is supported by Kotlin
//  multiplatform. I tried doing this a bunch of different ways and couldn't make anything work.

@Composable
internal fun BasicSequenceDiagram() {
    // Specifies a simple sequence diagram that consists of three participants with some lines
    // between them.
    SequenceDiagram {
        val alice = createParticipant { Note("Alice") }
        val bob = createParticipant { Note("Bob") }
        val carlos = createParticipant { Note("Carlos") }

        // Lines can be specified between any two participants, with their
        alice.lineTo(bob)
            .label { Label("Hello!") }
        bob.lineTo(carlos)
            .label { Label("Alice says hi") }

        // Lines don't need to have labels, and they can be styled.
        carlos.lineTo(bob)
            .color(Color.Blue)
            .arrowHeadType(ArrowHeadType.Outlined)

        // Lines can span multiple participants.
        carlos.lineTo(alice)
            .label { Label("Hello back!") }
    }
}

@Composable
internal fun NotesAroundParticipant() {
    // In addition to lines, notes can also be placed around participants.
    SequenceDiagram {
        val alice = createParticipant { Note("Alice") }
        createParticipant { Note("Bob") }
        val carlos = createParticipant { Note("Carlos") }

        noteToStartOf(alice) { Note("Note to the start of Alice") }
        noteOver(alice) { Note("Note over Alice") }
        noteToEndOf(alice) { Note("Note to the end of Alice") }

        noteOver(alice, carlos) { Note("Note over multiple participants") }
    }
}

@Composable
internal fun Styling() {
    // Most of the internal layout and drawing properties of the sequence diagram can be controlled
    // via a SequenceDiagramStyle. BasicSequenceDiagramStyle is an immutable implementation of that
    // interface.
    SequenceDiagram(
        style = BasicSequenceDiagramStyle(
            participantSpacing = 10.dp,
            verticalSpacing = 2.dp,
            labelPadding = 16.dp,
            labelTextStyle = TextStyle(fontFamily = FontFamily.Cursive),
            notePadding = PaddingValues(8.dp),
            noteShape = RoundedCornerShape(6.dp),
            noteBackgroundBrush = Brush.radialGradient(
                0f to Color.White, 1f to Color.LightGray
            ),
            lineStyle = LineStyle(
                brush = SolidColor(Color.Blue),
                width = 4.dp,
                arrowHeadType = ArrowHeadType.Outlined,
            ),
        )
    ) {
        val alice = createParticipant { Note("Alice") }
        val bob = createParticipant { Note("Bob") }
        val carlos = createParticipant { Note("Carlos") }

        alice.lineTo(bob)
            .label { Label("Hello!") }

        noteOver(alice, carlos) { Note("Wide note") }
    }
}

@Composable
internal fun DimensionBalancing() {
    // By default, the layout algorithm tries to measure notes and labels that don't span multiple
    // participants to make them closer to square by trying to balance their width vs their height.
    SequenceDiagram(style = BasicSequenceDiagramStyle(balanceLabelDimensions = false)) {
        val alice = createParticipant { Note("Alice") }
        noteOver(alice) { Note("A note with long text that would be wrapped by default.") }
    }
}