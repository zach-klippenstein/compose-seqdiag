package com.zachklipp.seqdiag.layout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.zachklipp.seqdiag.LineBuilder
import com.zachklipp.seqdiag.LineStyle

internal class LineBuilderImpl : LineBuilder {

    var style: LineStyle by mutableStateOf(LineStyle())
        private set
    var label by mutableStateOf<(@Composable () -> Unit)?>(null)
        private set

    override fun style(style: LineStyle): LineBuilder = apply {
        this.style = style.fillMissingFrom(this.style)
    }

    override fun label(content: @Composable () -> Unit): LineBuilder =
        apply { this.label = content }
}