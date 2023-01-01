package com.zachklipp.seqdiag.layout

import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.zachklipp.seqdiag.ArrowHeadType
import com.zachklipp.seqdiag.ArrowToSelf
import com.zachklipp.seqdiag.LineBuilder
import com.zachklipp.seqdiag.OverrideTextStyle
import com.zachklipp.seqdiag.ParticipantState
import com.zachklipp.seqdiag.SequenceDiagramStyle
import com.zachklipp.seqdiag.getLineStroke
import com.zachklipp.seqdiag.layout.SingleParticipantRowItem.ParticipantAlignment.End

internal class LineToSelf(
    override val participant: ParticipantState,
) : SingleParticipantRowItem(), LineBuilder {

    private var measurable: Measurable? = null
    private var placeable: Placeable? by mutableStateOf(null)

    private var brush: Brush? by mutableStateOf(null)
    private var stroke by mutableStateOf<Stroke?>(null)
    private var arrowHeadType by mutableStateOf(ArrowHeadType.Filled)
    private var label by mutableStateOf<(@Composable () -> Unit)?>(null)

    override val height: Int
        get() = placeable?.height ?: 0
    override val width: Int
        get() = placeable?.width ?: 0

    override val participantAlignment: ParticipantAlignment
        get() = End
    override val maxIntrinsicWidth: Int
        get() = measurable?.maxIntrinsicWidth(Constraints.Infinity) ?: 0

    override fun brush(brush: Brush): LineBuilder = apply { this.brush = brush }
    override fun stroke(stroke: Stroke): LineBuilder = apply { this.stroke = stroke }
    override fun arrowHeadType(type: ArrowHeadType) = apply { this.arrowHeadType = type }
    override fun label(content: @Composable () -> Unit): LineBuilder =
        apply { this.label = content }

    @Composable
    override fun Content(style: SequenceDiagramStyle) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Max),
            horizontalArrangement = spacedBy(style.labelPadding)
        ) {
            ArrowToSelf(
                brush = brush ?: style.lineBrush,
                stroke = stroke ?: style.getLineStroke(LocalDensity.current),
                head = arrowHeadType,
                modifier = Modifier
                    .fillMaxHeight()
                    .heightIn(min = 20.dp)
            )
            label?.let {
                OverrideTextStyle({ it.copy(textAlign = TextAlign.Start) }) {
                    Box(
                        propagateMinConstraints = true,
                        modifier = if (style.balanceLabelDimensions) {
                            Modifier.balancedAspectRatio()
                        } else Modifier
                    ) {
                        it()
                    }
                }
            }
        }
    }

    override fun consumeMeasurables(nextMeasurable: () -> Measurable) {
        measurable = nextMeasurable()
    }

    override fun measure(maxWidth: Int) {
        placeable = measurable!!.measure(Constraints(maxWidth = maxWidth))
    }

    override fun Placeable.PlacementScope.place(density: Density) {
        placeable!!.placeRelative(left, top)
    }
}