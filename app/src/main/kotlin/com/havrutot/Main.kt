package com.havrutot

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.havrutot.ui.screens.MainScreen

fun main(args: Array<String>) = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Havrutot Matcher",
        icon = painterResource("icon.png")
    ) {
        MainScreen()
    }
}
