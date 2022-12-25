package com.zachklipp.seqdiag

import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign

@Suppress("DEPRECATION")
@OptIn(ExperimentalTextApi::class)
internal actual val RootTextStyle: TextStyle = TextStyle(
    textAlign = TextAlign.Center,
    platformStyle = PlatformTextStyle(includeFontPadding = false)
)