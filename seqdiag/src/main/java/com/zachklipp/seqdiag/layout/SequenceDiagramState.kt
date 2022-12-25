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
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.LayoutDirection.Ltr
import androidx.compose.ui.unit.LayoutDirection.Rtl
import com.zachklipp.seqdiag.LineBuilder
import com.zachklipp.seqdiag.Participant
import com.zachklipp.seqdiag.ParticipantState
import com.zachklipp.seqdiag.SequenceDiagramScope
import com.zachklipp.seqdiag.SequenceDiagramStyle
import com.zachklipp.seqdiag.layout.SingleParticipantRowItem.ParticipantAlignment.End
import com.zachklipp.seqdiag.layout.SingleParticipantRowItem.ParticipantAlignment.Over
import com.zachklipp.seqdiag.layout.SingleParticipantRowItem.ParticipantAlignment.Start

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
            it.Content(diagramStyle)
        }
    }

    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints
    ): MeasureResult {
        if (participants.isEmpty()) return layout(0, 0) {}

        val horizontalSpacing = diagramStyle.participantSpacing.roundToPx()
        val labelPadding = diagramStyle.labelPadding.roundToPx()
        val verticalSpacing = diagramStyle.verticalSpacing.roundToPx()

        // Max constraints will always be unbounded since using scaleToFit modifier.
        collectMeasurables(measurables)
        measureParticipantLabels()
        val totalWidth = measureColumns(
            participantSpacing = horizontalSpacing,
            itemPadding = labelPadding * 2,
            layoutDirection = layoutDirection
        )
        measureSpanningRows()
        val totalHeight = calculateVerticalOffsets(verticalSpacing)

        diagramSize = IntSize(totalWidth, totalHeight)
        return layout(totalWidth, totalHeight) {
            // Place participant labels
            participants.forEach {
                val topLabelLeft = it.left + Alignment.CenterHorizontally.align(
                    size = it.topLabelPlaceable?.width ?: 0,
                    space = it.labelWidth,
                    layoutDirection
                )
                val bottomLabelLeft = it.left + Alignment.CenterHorizontally.align(
                    size = it.bottomLabelPlaceable?.width ?: 0,
                    space = it.labelWidth,
                    layoutDirection
                )
                it.topLabelPlaceable!!.placeRelative(x = topLabelLeft, y = it.topLabelTop)
                it.bottomLabelPlaceable!!.placeRelative(x = bottomLabelLeft, y = it.bottomLabelTop)
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
        index = participants.size,
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
                participantAlignment = Over,
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
            participantAlignment = Start,
            label = label,
            style = diagramStyle
        )
    }

    override fun noteToEndOf(participant: Participant, label: @Composable () -> Unit) {
        rowItems += SingleParticipantNote(
            participant as ParticipantState,
            participantAlignment = End,
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

    private fun Density.measureSpanningRows() {
        rowItems.forEach { item ->
            if (item is SpanningRowItem) {
                val startParticipant = participants.first { it in item.participants }
                val endParticipant = participants.last { it in item.participants }
                val minStart = startParticipant.left - startParticipant.labelWidth / 2
                val maxStart = startParticipant.centerXOffset
                val minEnd = endParticipant.centerXOffset
                val maxEnd = endParticipant.left + endParticipant.labelWidth / 2
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

    private fun measureColumns(
        participantSpacing: Int,
        itemPadding: Int,
        layoutDirection: LayoutDirection
    ): Int {
        val numColumns = participants.size * 2 + 1
        val columnItems = Array(numColumns) { mutableVectorOf<SingleParticipantRowItem>() }
        val columnIntrinsicWidths = IntArray(numColumns)
        val columnWidths = IntArray(numColumns)

        // Sort row items into columns and calculate each column's max intrinsic width.
        rowItems.forEach { item ->
            if (item is SingleParticipantRowItem) {
                val participant = item.participant as ParticipantState
                val participantColumnIndex = participant.index * 2 + 1
                val columnIndex = when (item.participantAlignment) {
                    Start -> participantColumnIndex - 1
                    Over -> participantColumnIndex
                    End -> participantColumnIndex + 1
                }
                columnItems[columnIndex] += item
                // The padding used to separate the item from the participant it's NOT anchored to.
                val nonParticipantPadding = when (columnIndex) {
                    0, numColumns - 1 -> 0
                    else -> itemPadding
                }
                columnIntrinsicWidths[columnIndex] = maxOf(
                    columnIntrinsicWidths[columnIndex],
                    item.maxIntrinsicWidth + nonParticipantPadding
                )
            }
        }

        // Calculate each column's max width constraint and place participants.
        var participantOffset = 0
        columnIntrinsicWidths.forEachIndexed { columnIndex, intrinsicWidth ->
            val beforeExtent = if (columnIndex == 0) 0 else {
                columnIntrinsicWidths[columnIndex - 1] / 2
            }
            val afterExtent = if (columnIndex == numColumns - 1) 0 else {
                columnIntrinsicWidths[columnIndex + 1] / 2
            }
            val participantLabelExtent = if (columnIndex % 2 == 0) {
                // In between participants.
                val beforeParticipant = participants.getOrNull((columnIndex) / 2 - 1)
                val afterParticipant = participants.getOrNull(columnIndex / 2)
                val spacing = if (beforeParticipant != null && afterParticipant != null) {
                    participantSpacing
                } else {
                    0
                }
                (beforeParticipant?.labelWidth ?: 0) / 2 +
                        spacing +
                        (afterParticipant?.labelWidth ?: 0) / 2
            } else {
                // On a participant.
                val participantIndex = columnIndex / 2
                val beforeParticipant = participants.getOrNull(participantIndex - 1)
                val participant = participants[participantIndex]
                val afterParticipant = participants.getOrNull(participantIndex + 1)
                val beforeSpacing = if (beforeParticipant != null) participantSpacing else 0
                val afterSpacing = if (afterParticipant != null) participantSpacing else 0
                (beforeParticipant?.labelWidth ?: 0) / 2 +
                        beforeSpacing +
                        participant.labelWidth +
                        afterSpacing +
                        (afterParticipant?.labelWidth ?: 0) / 2
            }
            columnWidths[columnIndex] = maxOf(
                intrinsicWidth,
                beforeExtent,
                afterExtent,
                participantLabelExtent
            )

            // Place participants horizontally.
            if (columnIndex % 2 == 1) {
                val participant = participants[columnIndex / 2]
                // Previous width always includes start extent of this column.
                val previousWidth = columnWidths[columnIndex - 1]
                participant.centerXOffset = participantOffset + previousWidth
                participantOffset += previousWidth
            }
        }

        // Measure and place items.
        columnItems.forEachIndexed { columnIndex, items ->
            val columnWidth = columnWidths[columnIndex]
            val columnLeft = participants.getOrNull(columnIndex / 2 - 1)?.centerXOffset ?: 0
            items.forEach { item ->
                item.measure(maxWidth = columnWidth)
                // Start means start of next participant (end of column), and end means start of
                // previous participant (start of column).
                val alignment = when (item.participantAlignment) {
                    Start -> Alignment.End
                    // Can't use CenterHorizontally because we need to center over the
                    // _participant_, not the total column space.
                    Over -> null
                    End -> Alignment.Start
                }
                val left = if (alignment != null) {
                    // Start/End
                    alignment.align(item.width, columnWidth, Ltr)
                } else {
                    // Over
                    val center = (item.participant as ParticipantState).centerXOffset - columnLeft
                    center - item.width / 2
                }
                item.left = columnLeft + left
            }
        }

        return participantOffset + columnWidths.last()
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
        withTransform({
            if (this@drawParticipantLines.layoutDirection == Rtl) {
                scale(scaleX = -1f, scaleY = 1f)
            }
        }) {
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
    }

    // endregion
}