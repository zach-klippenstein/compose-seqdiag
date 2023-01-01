package com.zachklipp.seqdiag

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
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