package com.zachklipp.seqdiag.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collection.mutableVectorOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import com.zachklipp.seqdiag.LineBuilder
import com.zachklipp.seqdiag.Participant
import com.zachklipp.seqdiag.ParticipantState
import com.zachklipp.seqdiag.SequenceDiagramScope
import com.zachklipp.seqdiag.SequenceDiagramStyle
import com.zachklipp.seqdiag.layout.SingleParticipantNote.NoteAnchor.End
import com.zachklipp.seqdiag.layout.SingleParticipantNote.NoteAnchor.Over
import com.zachklipp.seqdiag.layout.SingleParticipantNote.NoteAnchor.Start

internal class SequenceDiagramState : SequenceDiagramScope, MeasurePolicy {

    private val participants = mutableStateListOf<ParticipantState>()
    private val rowItems = mutableStateListOf<SequenceRowItem>()
    private var topLabelsHeight by mutableStateOf(0)
    private var bottomLabelsHeight by mutableStateOf(0)
    private var diagramSize by mutableStateOf(IntSize.Zero)

    override var diagramStyle: SequenceDiagramStyle by mutableStateOf(SequenceDiagramStyle.Default)

    // region Phases

    fun buildDiagram(content: SequenceDiagramScope.() -> Unit) {
        participants.clear()
        rowItems.clear()
        content(this)
    }

    @Composable
    fun Content() {
        participants.forEach {
            Box(propagateMinConstraints = true) {
                it.topLabel()
            }
            it.bottomLabel?.let { bottomLabel ->
                Box(propagateMinConstraints = true) {
                    bottomLabel()
                }
            }
        }

        rowItems.forEach {
            it.Content()
        }
    }

    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints
    ): MeasureResult {
        if (participants.isEmpty()) return layout(0, 0) {}

        val horizontalSpacing = diagramStyle.participantSpacing.roundToPx()
        val verticalSpacing = diagramStyle.verticalSpacing.roundToPx()

        // Max constraints will always be unbounded since using scaleToFit modifier.
        collectMeasurables(measurables)
        measureParticipantLabels()
        measureIndependentRows()
        val totalWidth = calculateHorizontalOffsets(horizontalSpacing)
        measureSpanningRows()
        val totalHeight = calculateVerticalOffsets(verticalSpacing)

        diagramSize = IntSize(totalWidth, totalHeight)
        return layout(totalWidth, totalHeight) {
            // Place participant labels
            participants.forEach {
                it.topLabelPlaceable!!.placeRelative(x = it.left, y = it.topLabelTop)
                it.bottomLabelPlaceable!!.placeRelative(x = it.left, y = it.bottomLabelTop)
            }

            // Place rows
            rowItems.forEach {
                with(it) { place(this@measure) }
            }
        }
    }

    fun ContentDrawScope.draw() {
        drawParticipantLines()
        drawContent()
    }

    // endregion

    // region SequenceDiagramScope

    override fun createParticipant(
        topLabel: @Composable () -> Unit,
        bottomLabel: (@Composable () -> Unit)?
    ): Participant = ParticipantState(
        topLabel = topLabel,
        bottomLabel = bottomLabel
    ).also {
        participants += it
    }

    override fun Participant.lineTo(other: Participant): LineBuilder {
        return if (this == other) {
            LineToSelf(this as ParticipantState, diagramStyle)
        } else {
            Line(
                from = this as ParticipantState,
                to = other as ParticipantState,
                forwards = this.isBefore(other),
                style = diagramStyle,
            )
        }.also {
            rowItems += it
        }
    }

    override fun noteOver(participants: List<Participant>, label: @Composable () -> Unit) {
        require(participants.isNotEmpty()) { "Participants list must not be empty" }
        rowItems += if (participants.size == 1) {
            SingleParticipantNote(
                participants[0] as ParticipantState,
                anchor = Over,
                label = label,
                style = diagramStyle,
            )
        } else {
            SpanningNote(participants, label, diagramStyle)
        }
    }

    override fun noteToStartOf(participant: Participant, label: @Composable () -> Unit) {
        rowItems += SingleParticipantNote(
            participant as ParticipantState,
            anchor = Start,
            label = label,
            style = diagramStyle
        )
    }

    override fun noteToEndOf(participant: Participant, label: @Composable () -> Unit) {
        rowItems += SingleParticipantNote(
            participant as ParticipantState,
            anchor = End,
            label = label,
            style = diagramStyle
        )
    }

    // endregion

    // region Helpers

    private fun Participant.isBefore(other: Participant): Boolean {
        participants.forEach {
            if (it == this) return true
            if (it == other) return false
        }
        return false
    }

    private fun collectMeasurables(measurables: List<Measurable>) {
        var i = 0
        participants.forEach {
            it.topLabelMeasurable = measurables[i++]
            if (it.bottomLabel != null) {
                it.bottomLabelMeasurable = measurables[i++]
            }
        }
        rowItems.forEach {
            it.consumeMeasurables { measurables[i++] }
        }
        check(i == measurables.size) { "Expected $i measurables, got ${measurables.size}" }
    }

    private fun measureParticipantLabels() {
        var topHeight = 0
        var bottomHeight = 0

        val labelConstraints = Constraints(
            minWidth = 0,
            maxWidth = Constraints.Infinity,
            minHeight = 0,
            maxHeight = Constraints.Infinity
        )
        participants.forEach {
            it.topLabelPlaceable = it.topLabelMeasurable!!.measure(labelConstraints)
            it.bottomLabelPlaceable = it.bottomLabelMeasurable?.measure(labelConstraints)

            topHeight = maxOf(topHeight, it.topLabelPlaceable!!.height)
            bottomHeight = maxOf(bottomHeight, it.bottomLabelPlaceable?.height ?: 0)
        }

        topLabelsHeight = topHeight
        bottomLabelsHeight = bottomHeight
    }

    private fun measureIndependentRows() {
        rowItems.forEach {
            if (it is SingleParticipantRowItem) {
                it.measure()
            }
        }
    }

    private fun Density.measureSpanningRows() {
        rowItems.forEach { item ->
            if (item is SpanningRowItem) {
                val startParticipant = participants.first { it in item.participants }
                val endParticipant = participants.last { it in item.participants }
                val minStart = startParticipant.left - startParticipant.columnWidth / 2
                val maxStart = startParticipant.centerXOffset
                val minEnd = endParticipant.centerXOffset
                val maxEnd = endParticipant.left + endParticipant.columnWidth / 2
                with(item) {
                    measure(
                        minWidth = minEnd - maxStart,
                        maxWidth = maxEnd - minStart
                    )
                }
                item.left = startParticipant.centerXOffset
            }
        }
    }

    /**
     * @return total width
     */
    private fun calculateHorizontalOffsets(spacing: Int): Int {
        val participantItems = mutableMapOf<Participant, MutableList<SingleParticipantRowItem>>()
        rowItems.forEach {
            if (it is SingleParticipantRowItem) {
                val items = participantItems.getOrPut(it.participant) { ArrayList() }
                items += it
            }
        }

        val startItems = mutableVectorOf<SingleParticipantRowItem>()
        val overItems = mutableVectorOf<SingleParticipantRowItem>()
        val lineItems = mutableVectorOf<SingleParticipantRowItem>()
        val endItems = mutableVectorOf<SingleParticipantRowItem>()
        var startWidth: Int
        var overWidth: Int
        var lineWidth: Int
        var endWidth: Int
        var runningLeft = 0
        participants.forEachIndexed { index, participant ->
            startItems.clear()
            overItems.clear()
            lineItems.clear()
            endItems.clear()
            startWidth = 0
            overWidth = 0
            lineWidth = 0
            endWidth = 0
            val items = participantItems[participant] ?: mutableListOf()
            items.forEach { item ->
                when (item) {
                    is SingleParticipantNote -> when (item.anchor) {
                        Start -> {
                            startItems += item
                            startWidth = maxOf(item.width, startWidth)
                        }

                        Over -> {
                            overItems += item
                            overWidth = maxOf(item.width, overWidth)
                        }

                        End -> {
                            endItems += item
                            endWidth = maxOf(item.width, endWidth)
                        }
                    }

                    is LineToSelf -> {
                        lineItems += item
                        lineWidth = maxOf(item.width, lineWidth)
                    }

                    else -> {
                        // Noop
                    }
                }
            }

            val halfLabelWidth = participant.labelWidth / 2
            participant.columnWidth = maxOf(
                participant.labelWidth,
                overWidth,
                maxOf(startWidth, halfLabelWidth) + maxOf(lineWidth, endWidth, halfLabelWidth)
            )
            participant.centerXOffset = runningLeft + maxOf(
                halfLabelWidth,
                overWidth / 2,
                startWidth
            )
            val rowStart = runningLeft + (halfLabelWidth - startWidth).coerceAtLeast(0)

            startItems.forEach { item ->
                item.left = rowStart + Alignment.End.align(
                    size = item.width,
                    space = startWidth,
                    LayoutDirection.Ltr
                )
            }
            overItems.forEach { item ->
                item.left =
                    participant.centerXOffset - overWidth / 2 + Alignment.CenterHorizontally.align(
                        size = item.width,
                        space = overWidth,
                        LayoutDirection.Ltr
                    )
            }
            lineItems.forEach { item ->
                item.left = participant.centerXOffset
            }
            endItems.forEach { item ->
                item.left = participant.centerXOffset
            }
            runningLeft += participant.columnWidth
            if (index < participants.size - 1) {
                // Don't add spacing after the last item.
                runningLeft += spacing
            }
        }

        return runningLeft
    }

    /** @return total height */
    private fun calculateVerticalOffsets(spacing: Int): Int {
        // Place top labels.
        participants.forEach {
            it.topLabelTop = Alignment.Bottom.align(
                size = it.topLabelHeight,
                space = topLabelsHeight
            )
        }

        // Place rows.
        var y = topLabelsHeight + spacing
        rowItems.forEach { item ->
            item.top = y
            y += item.height + spacing
        }

        // Place bottom labels.
        participants.forEach {
            it.bottomLabelTop = y
        }

        return y + bottomLabelsHeight
    }

    private fun DrawScope.drawParticipantLines() {
        val brush = diagramStyle.lineBrush
        val width = diagramStyle.lineWeight.toPx()
        participants.forEach {
            val x = it.centerXOffset.toFloat()
            val top = it.topLabelPlaceable?.height?.toFloat() ?: 0f
            val bottom = size.height - (it.bottomLabelPlaceable?.height?.toFloat() ?: 0f)
            drawLine(
                brush = brush,
                strokeWidth = width,
                start = Offset(x, top),
                end = Offset(x, bottom)
            )
        }
    }

    // endregion
}