package io.github.diubruteforce.smartcr.utils.extension

import androidx.annotation.FloatRange
import androidx.compose.ui.graphics.Color

fun Color.lighten(@FloatRange(from = 0.0, to = 1.0) value: Float): Color {
    val red = red + (1 - red) * value
    val green = green + (1 - green) * value
    val blue = blue + (1 - blue) * value

    return copy(red = red, green = green, blue = blue)
}

fun Color.darken(@FloatRange(from = 0.0, to = 1.0) value: Float): Color {
    val red = red * (1 - value)
    val green = green * (1 - value)
    val blue = blue * (1 - value)

    return copy(red = red, green = green, blue = blue)
}