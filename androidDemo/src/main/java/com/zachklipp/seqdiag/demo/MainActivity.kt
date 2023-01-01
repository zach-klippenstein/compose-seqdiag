package com.zachklipp.seqdiag.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection.Rtl
import com.zachklipp.seqdiag.samples.DemoApp
import com.zachklipp.seqdiag.samples.DiagramDevDemo

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DemoApp()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DiagramDemoPreview() {
    DiagramDevDemo()
}

@Preview(showBackground = true)
@Composable
fun DiagramDemoPreviewNoBalancing() {
    DiagramDevDemo(balanceLabelDimensions = false)
}

@Preview(showBackground = true)
@Composable
fun DiagramDemoPreviewRtl() {
    CompositionLocalProvider(LocalLayoutDirection provides Rtl) {
        DiagramDevDemo()
    }
}