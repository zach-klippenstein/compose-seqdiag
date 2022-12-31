package com.zachklipp.seqdiag

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.Layout
import com.zachklipp.seqdiag.layout.SequenceDiagramState

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
            .drawWithContent { with(state) { draw() } },
        content = { state.Content() },
        measurePolicy = state
    )
}

sealed interface Participant

interface LineBuilder {
    fun color(color: Color): LineBuilder = brush(SolidColor(color))
    fun brush(brush: Brush): LineBuilder
    fun stroke(stroke: Stroke): LineBuilder
    fun label(content: @Composable () -> Unit): LineBuilder
}