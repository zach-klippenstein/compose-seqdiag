package com.zachklipp.seqdiag

import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.PlatformTextStyle

@Suppress("DEPRECATION")
@OptIn(ExperimentalTextApi::class)
internal actual val RootPlatformTextStyle = PlatformTextStyle(includeFontPadding = false)