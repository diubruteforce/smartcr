package io.github.diubruteforce.smartcr.ui.theme

import androidx.compose.material.AmbientContentColor
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.github.diubruteforce.smartcr.utils.extension.darken
import io.github.diubruteforce.smartcr.utils.extension.lighten

val orange200 = Color(0xFFF0b185)
val orange500 = Color(0xFFF39200)
val redError = Color(0xFFe91c1c)

@Composable
val Colors.grayText: Color
    get() = AmbientContentColor.current.lighten(0.55f)

@Composable
val Colors.grayBackground: Color
    get() = MaterialTheme.colors.background.darken(0.05f)


@Composable
val Colors.grayBorder: Color
    get() = MaterialTheme.colors.onBackground.copy(alpha = 0.08f)