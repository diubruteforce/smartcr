package io.github.diubruteforce.smartcr.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = orange200,
    error = redError,
    secondary = green500
)

private val LightColorPalette = lightColors(
    primary = orange500,
    error = redError,
    secondary = green500
)

@Composable
fun SmartCRTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = LightColorPalette,
        typography = typography,
        shapes = shapes,
        content = content
    )
}