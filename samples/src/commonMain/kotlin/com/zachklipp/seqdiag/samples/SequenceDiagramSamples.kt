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
import com.zachklipp.seqdiag.Note
import com.zachklipp.seqdiag.SequenceDiagram
import com.zachklipp.seqdiag.createParticipant
import com.zachklipp.seqdiag.noteOver

// New demos should only be added to the end of this file, to preserve existing links.

@Composable
fun BasicSequenceDiagram() {
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
fun NotesAroundParticipant() {
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
fun Styling() {
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
            lineBrush = SolidColor(Color.Blue),
            lineWeight = 4.dp,
            arrowHeadType = ArrowHeadType.Outlined,
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
fun DimensionBalancing() {
    SequenceDiagram(style = BasicSequenceDiagramStyle(balanceLabelDimensions = false)) {
        val alice = createParticipant { Note("Alice") }
        noteOver(alice) { Note("A note with long text that would be wrapped by default.") }
    }
}