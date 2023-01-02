package com.zachklipp.seqdiag.demo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush.Companion.verticalGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zachklipp.seqdiag.ArrowHeadType
import com.zachklipp.seqdiag.Label
import com.zachklipp.seqdiag.LineStyle
import com.zachklipp.seqdiag.Note
import com.zachklipp.seqdiag.SequenceDiagram
import com.zachklipp.seqdiag.color
import com.zachklipp.seqdiag.createParticipant
import com.zachklipp.seqdiag.noteOver

/**
 * Generates the hero image used in all the docs as a sample of the library.
 */
@Preview
@Composable
fun DiagramHeroDemo() {
    SequenceDiagram(
        modifier = Modifier
            .background(Color.White)
            .padding(8.dp)
    ) {
        val browser = createParticipant { Note("Browser") }
        val frontend = createParticipant { Note("Frontend") }
        val microservice1 = createParticipant { Note("Microservice 1") }
        val microservice2 = createParticipant { Note("Microservice 2") }
        val db = createParticipant { Note("DB") }

        noteOver(browser) { Label("GET") }
        browser.lineTo(frontend)

        noteOver(frontend, db) { Note("1st attempt") }
        frontend.lineTo(microservice1)
        noteToStartOf(microservice1) { Label("400") }
        microservice1.lineTo(frontend)
            .color(Color.Red)

        noteOver(frontend, db) { Note("2nd attempt") }
        frontend.lineTo(microservice2)
        microservice2.lineTo(db)
            .style(
                LineStyle(
                    dashIntervals = 4.dp to 4.dp,
                    arrowHeadType = ArrowHeadType.Outlined
                )
            )
        db.lineTo(microservice2)
            .style(
                LineStyle(
                    dashIntervals = 4.dp to 4.dp,
                    arrowHeadType = ArrowHeadType.Outlined
                )
            )
        noteToStartOf(microservice2) { Label("200") }
        microservice2.lineTo(frontend)
            .color(Color.Green)

        frontend.lineTo(frontend)
            .label { Label("combine responses") }
            .style(
                LineStyle(
                    brush = verticalGradient(0f to Color.Red, 0.7f to Color.Green),
                    width = 6.dp,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                    arrowHeadType = ArrowHeadType.Outlined
                )
            )
        noteToStartOf(frontend) { Label("200") }
        frontend.lineTo(browser)
            .color(Color.Green)
    }
}