package com.healthaware.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val HealthAwareColors = lightColorScheme(
    primary = Color(0xFF087F8C),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1F1F1),
    secondary = Color(0xFF386B66),
    secondaryContainer = Color(0xFFD9EEE8),
    background = Color(0xFFF7FAFA),
    surface = Color(0xFFFFFFFF),
    error = Color(0xFFB3261E)
)

@Composable
fun HealthAwareTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = HealthAwareColors, content = content)
}
