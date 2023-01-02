package com.zachklipp.seqdiag.layout

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color

/**
 * Used to erase the participant lines behind labels with transparent backgrounds.
 */
internal fun Modifier.clearBackground(): Modifier = drawBehind {
    drawRect(Color.Black, blendMode = BlendMode.Clear)
}