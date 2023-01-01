package com.zachklipp.seqdiag

import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

private val ArrowHeadSize = DpSize(8.dp, 10.dp)

enum class ArrowHeadType {
    Filled, Outlined
}

@Composable
internal fun HorizontalArrow(
    brush: Brush,
    stroke: Stroke,
    modifier: Modifier = Modifier,
    startHead: ArrowHeadType? = null,
    endHead: ArrowHeadType? = null
) {
    Box(
        modifier
            .sizeIn(minWidth = ArrowHeadSize.width, minHeight = ArrowHeadSize.height)
            .drawWithCache {
                val startHeadOutline = startHead?.let {
                    ArrowShape.createOutline(
                        ArrowHeadSize.toSize(),
                        LayoutDirection.Rtl,
                        this
                    )
                }
                val endHeadOutline = endHead?.let {
                    ArrowShape.createOutline(
                        ArrowHeadSize.toSize(),
                        LayoutDirection.Ltr,
                        this
                    )
                }
                val headHeightPx = ArrowHeadSize.height.toPx()

                onDrawBehind {
                    withTransform({
                        translate(top = size.height / 2)
                        if (this@drawWithCache.layoutDirection == LayoutDirection.Rtl) {
                            scale(scaleX = -1f, scaleY = 1f)
                        }
                    }) {
                        drawLine(
                            start = Offset(0f, 0f),
                            end = Offset(size.width, 0f),
                            brush = brush,
                            strokeWidth = stroke.width,
                            pathEffect = stroke.pathEffect
                        )
                        translate(top = headHeightPx / -2) {
                            startHeadOutline?.let {
                                drawOutline(
                                    it,
                                    brush = brush,
                                    style = when (startHead) {
                                        ArrowHeadType.Filled -> Fill
                                        ArrowHeadType.Outlined -> stroke
                                    }
                                )
                            }
                            endHeadOutline?.let {
                                translate(left = size.width - endHeadOutline.bounds.width) {
                                    drawOutline(
                                        it,
                                        brush = brush,
                                        style = when (endHead) {
                                            ArrowHeadType.Filled -> Fill
                                            ArrowHeadType.Outlined -> stroke
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
    )
}

@Composable
internal fun ArrowToSelf(
    brush: Brush,
    stroke: Stroke,
    head: ArrowHeadType,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .sizeIn(minWidth = ArrowHeadSize.width * 2, minHeight = ArrowHeadSize.height)
            .drawWithCache {
                val headOutline = ArrowShape.createOutline(
                    ArrowHeadSize.toSize(),
                    LayoutDirection.Rtl,
                    this
                )
                val pathBottom = size.height - headOutline.bounds.height / 2
                val path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(size.width, 0f)
                    lineTo(size.width, pathBottom)
                    lineTo(0f, pathBottom)
                }
                onDrawBehind {
                    withTransform({
                        if (this@drawWithCache.layoutDirection == LayoutDirection.Rtl) {
                            scale(scaleX = -1f, scaleY = 1f)
                        }
                    }) {
                        drawPath(path, brush = brush, style = stroke)
                        translate(top = size.height - headOutline.bounds.height) {
                            drawOutline(
                                headOutline,
                                brush = brush,
                                style = when (head) {
                                    ArrowHeadType.Filled -> Fill
                                    ArrowHeadType.Outlined -> stroke
                                }
                            )
                        }
                    }
                }
            }
    )
}

private val ArrowShape = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val startX = if (layoutDirection == LayoutDirection.Ltr) 0f else size.width
        val endX = if (layoutDirection == LayoutDirection.Ltr) size.width else 0f
        val path = Path().apply {
            moveTo(startX, 0f)
            lineTo(endX, size.height / 2)
            lineTo(startX, size.height)
        }
        return Outline.Generic(path)
    }
}

private fun LayoutDirection.reversed() = when (this) {
    LayoutDirection.Ltr -> LayoutDirection.Rtl
    LayoutDirection.Rtl -> LayoutDirection.Ltr
}

//@Preview
@Composable
private fun ArrowPreview() {
    Column(verticalArrangement = spacedBy(2.dp), modifier = Modifier.padding(8.dp)) {
        HorizontalArrow(brush = SolidColor(Color.Black), stroke = Stroke())
        ArrowToSelf(
            brush = SolidColor(Color.Black),
            stroke = Stroke(),
            head = ArrowHeadType.Outlined
        )
        HorizontalArrow(
            brush = SolidColor(Color.Black),
            stroke = Stroke(),
            modifier = Modifier.size(50.dp)
        )
        ArrowToSelf(
            brush = SolidColor(Color.Black),
            stroke = Stroke(),
            head = ArrowHeadType.Filled,
            modifier = Modifier.size(50.dp)
        )
        HorizontalArrow(
            brush = SolidColor(Color.Black), stroke = Stroke(),
            modifier = Modifier.size(50.dp),
            startHead = ArrowHeadType.Outlined,
            endHead = ArrowHeadType.Filled
        )
        HorizontalArrow(
            brush = SolidColor(Color.Black),
            stroke = Stroke(
                width = 3f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
            ),
            modifier = Modifier.size(50.dp),
            startHead = ArrowHeadType.Outlined,
            endHead = ArrowHeadType.Filled
        )
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            HorizontalArrow(
                brush = SolidColor(Color.Black), stroke = Stroke(),
                modifier = Modifier.size(50.dp),
                startHead = ArrowHeadType.Outlined,
                endHead = ArrowHeadType.Filled
            )
        }
        ArrowToSelf(
            brush = SolidColor(Color.Black),
            stroke = Stroke(),
            modifier = Modifier.size(50.dp),
            head = ArrowHeadType.Filled
        )
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            ArrowToSelf(
                brush = SolidColor(Color.Black),
                stroke = Stroke(),
                modifier = Modifier.size(50.dp),
                head = ArrowHeadType.Outlined
            )
        }
    }
}
