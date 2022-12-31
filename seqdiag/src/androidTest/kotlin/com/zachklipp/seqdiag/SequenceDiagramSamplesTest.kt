package com.zachklipp.seqdiag

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composer
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.currentComposer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameter.TestParameterValuesProvider
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.reflect.Method

@Suppress("JUnitMalformedDeclaration")
@RunWith(TestParameterInjector::class)
class SequenceDiagramSamplesTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5,
        showSystemUi = false,
        renderingMode = SessionParams.RenderingMode.SHRINK,
    )

    fun interface SampleFunction {
        @Composable
        operator fun invoke()
    }

    @Test
    fun sample(
        @TestParameter(valuesProvider = SampleFunctionProvider::class)
        sampleFunction: SampleFunction,
        @TestParameter layoutDirection: LayoutDirection,
    ) {
        paparazzi.snapshot {
            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                sampleFunction()
            }
        }
    }

    /**
     * Provides [SampleFunction]s for every sample function defined in a class. Sample functions
     * must by `@Composable` and have no kotlin parameters, aside from the ones generated by
     * compose.
     */
    class SampleFunctionProvider : TestParameterValuesProvider {
        override fun provideValues(): List<SampleFunction> =
            Class.forName("com.zachklipp.seqdiag.samples.SequenceDiagramSamples").declaredMethods
                .mapNotNull { method ->
                    val params = method.parameters
                    if (params.size == 2 &&
                        params[0].type == Composer::class.java &&
                        params[1].type == Int::class.java
                    ) {
                        method.asSampleFunction()
                    } else null
                }

        private fun Method.asSampleFunction() = object : SampleFunction {
            override fun toString(): String = this@asSampleFunction.name

            @Composable
            override fun invoke() {
                val composer = currentComposer
                this@asSampleFunction.invoke(
                    null,
                    composer,
                    0
                )
            }
        }
    }
}