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
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.zachklipp.seqdiag.ArrowToSelf
import com.zachklipp.seqdiag.DefaultArrowHeadType
import com.zachklipp.seqdiag.DefaultLineBrush
import com.zachklipp.seqdiag.OverrideTextStyle
import com.zachklipp.seqdiag.ParticipantState
import com.zachklipp.seqdiag.SequenceDiagramStyle
import com.zachklipp.seqdiag.toLineStroke
import com.zachklipp.seqdiag.layout.SingleParticipantRowItem.ParticipantAlignment.End

internal class LineToSelf(
    override val participant: ParticipantState,
    private val builder: LineBuilderImpl
) : SingleParticipantRowItem() {

    private var measurable: Measurable? = null
    private var placeable: Placeable? by mutableStateOf(null)

    override val height: Int
        get() = placeable?.height ?: 0
    override val width: Int
        get() = placeable?.width ?: 0

    override val participantAlignment: ParticipantAlignment
        get() = End
    override val maxIntrinsicWidth: Int
        get() = measurable?.maxIntrinsicWidth(Constraints.Infinity) ?: 0

    @Composable
    override fun Content(style: SequenceDiagramStyle) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Max),
            horizontalArrangement = spacedBy(style.labelPadding)
        ) {
            val resolvedStyle = builder.style.fillMissingFrom(style.lineStyle)
            ArrowToSelf(
                brush = resolvedStyle.brush ?: DefaultLineBrush,
                stroke = resolvedStyle.toLineStroke(LocalDensity.current),
                head = resolvedStyle.arrowHeadType ?: DefaultArrowHeadType,
                modifier = Modifier
                    .fillMaxHeight()
                    .heightIn(min = 20.dp)
            )
            builder.label?.let {
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