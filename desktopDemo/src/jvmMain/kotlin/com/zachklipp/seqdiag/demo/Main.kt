package com.zachklipp.seqdiag.demo

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.zachklipp.seqdiag.samples.DemoApp

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        DemoApp()
    }
}
