package com.havrutot.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

private fun getDarkColorScheme(accentColorHex: String): ColorScheme {
    val primaryColor = try {
        // Parse hex string (e.g., "#BB86FC" or "BB86FC")
        val hexStr = accentColorHex.removePrefix("#")
        val colorInt = hexStr.toLong(16)
        if (hexStr.length == 6) {
            Color(colorInt or 0xFF000000)
        } else {
            Color(colorInt)
        }
    } catch (e: Exception) {
        Color(0xFFBB86FC)
    }

    return darkColorScheme(
        primary = primaryColor,
        secondary = Color(0xFF03DAC6),
        background = Color(0xFF1E1E1E),
        surface = Color(0xFF2D2D30),
        onPrimary = Color.Black,
        onSecondary = Color.Black,
        onBackground = Color.White,
        onSurface = Color.White,
    )
}

private val MetroShapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(4.dp),
    large = RoundedCornerShape(4.dp)
)

@Composable
fun AppTheme(
    accentColorHex: String = "#BB86FC",
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = getDarkColorScheme(accentColorHex),
            shapes = MetroShapes,
            content = content
        )
    }
}
