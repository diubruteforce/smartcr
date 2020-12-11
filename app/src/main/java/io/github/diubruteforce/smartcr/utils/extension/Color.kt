package io.github.diubruteforce.smartcr.utils.extension

import androidx.compose.ui.graphics.Color

fun Color.lighten(value: Float): Color {
    require(value in 0.0..1.0){
        "parameter for Color#lighten has to be inside 0 to 1"
    }

    val red = red + (1 - red) * value
    val green = green + (1 - green) * value
    val blue = blue + (1 - blue) * value

    return copy(red = red, green = green, blue = blue)
}

fun Color.darken(value: Float): Color {
    require(value in 0.0..1.0) {
        "parameter for Color#darken has to be inside 0 to 1"
    }

    val red = red * (1 - value)
    val green = green * (1 - value)
    val blue = blue * (1 - value)

    return copy(red = red, green = green, blue = blue)
}