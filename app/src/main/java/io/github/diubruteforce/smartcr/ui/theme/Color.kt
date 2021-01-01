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

val Colors.routine: Color @Composable get() = MaterialTheme.colors.primary
val Colors.quiz: Color @Composable get() = MaterialTheme.colors.primary
val Colors.assignment: Color @Composable get() = MaterialTheme.colors.primary
val Colors.presentation: Color @Composable get() = MaterialTheme.colors.primary
val Colors.project: Color @Composable get() = MaterialTheme.colors.primary
val Colors.fees: Color @Composable get() = MaterialTheme.colors.primary
val Colors.event: Color @Composable get() = MaterialTheme.colors.primary

val Colors.grayText: Color
    @Composable get() = AmbientContentColor.current.lighten(0.55f)


val Colors.grayBackground: Color
    @Composable get() = MaterialTheme.colors.background.darken(0.05f)


val Colors.grayBorder: Color
    @Composable get() = MaterialTheme.colors.onBackground.copy(alpha = 0.08f)