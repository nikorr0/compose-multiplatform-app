package org.company.app

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.company.app.ui.MainApp

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Laravel Cards KMP"
    ) {
        MainApp()
    }
}