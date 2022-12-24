package com.zachklipp.seqdiag

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection.Rtl
import com.zachklipp.seqdiag.ui.theme.ComposeSeqDiagTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeSeqDiagTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DiagramDemo()
                }
            }
        }
    }
}

@Composable
private fun DiagramDemo(balanceLabelDimensions: Boolean = true) {
    var participantValue by remember { mutableStateOf(TextFieldValue("(type)")) }
    var noteValue by remember { mutableStateOf(TextFieldValue("(type)")) }

    SequenceDiagram(style = object : SequenceDiagramStyle by SequenceDiagramStyle.Default {
        override val balanceLabelDimensions: Boolean = balanceLabelDimensions
    }) {
        val actor1 = createParticipant(topLabel = { Note("Actor 1") },
            bottomLabel = {
                BasicTextField(
                    value = participantValue,
                    onValueChange = { participantValue = it },
                    textStyle = TextStyle(textAlign = TextAlign.Center),
                    decorationBox = { NoteBox { it() } }
                )
            })
        val actor2 = createParticipant { Note("Actor\n2") }
        val actor3 = createParticipant { Note("Actor 3 has a really long name") }

        actor1.lineTo(actor2)
            .label { Label("Start the sequence, vroom vroom!") }
        actor2.lineTo(actor3)
            .color(Color.Red)
        noteToStartOf(actor1) { Note("Hello world what is going on") }
        actor3.lineTo(actor2)
        noteOver(actor2) { Note("…") }
        noteOver(actor2) {
            BasicTextField(
                value = noteValue,
                onValueChange = { noteValue = it },
                textStyle = TextStyle(textAlign = TextAlign.Center),
                decorationBox = { NoteBox { it() } }
            )
        }
        noteOver(actor2) { Note("this is a really really really long note") }
        actor3 lineTo actor1
        noteToEndOf(actor1) { Note("World") }
        noteToEndOf(actor3) { Note("Another really long note") }
        actor1.lineTo(actor1)
            .label { Label("It's a loop!") }
        actor2.lineTo(actor2)
            .label { Label("It's a loop with a really long description wow.") }
        noteToEndOf(actor2) { Note("Foo") }
        noteOver(actor1, actor2, actor3) { Note("All together now") }
    }
}

@Preview(showBackground = true)
@Composable
fun DiagramDemoPreview() {
    ComposeSeqDiagTheme {
        DiagramDemo()
    }
}

@Preview(showBackground = true)
@Composable
fun DiagramDemoPreviewNoBalancing() {
    ComposeSeqDiagTheme {
        DiagramDemo(balanceLabelDimensions = false)
    }
}

@Preview(showBackground = true)
@Composable
fun DiagramDemoPreviewRtl() {
    ComposeSeqDiagTheme {
        CompositionLocalProvider(LocalLayoutDirection provides Rtl) {
            DiagramDemo()
        }
    }
}