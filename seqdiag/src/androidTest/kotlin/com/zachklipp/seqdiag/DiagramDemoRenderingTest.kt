package com.zachklipp.seqdiag

import com.google.testing.junit.testparameterinjector.TestParameter
import com.zachklipp.seqdiag.samples.DiagramDevDemo
import org.junit.Test

@Suppress("JUnitMalformedDeclaration")
internal class DiagramDemoRenderingTest : BaseSnapshotTest() {

    @Test
    fun demoRendering(@TestParameter balanceLabels: Boolean) {
        snapshot {
            DiagramDevDemo(balanceLabelDimensions = balanceLabels)
        }
    }
}