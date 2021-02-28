package io.github.diubruteforce.smartcr.ui.theme

import androidx.compose.material.Colors
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.github.diubruteforce.smartcr.utils.extension.darken
import io.github.diubruteforce.smartcr.utils.extension.lighten

val orange200 = Color(0xFFF0b185)
val orange500 = Color(0xFFF39200)
val redError = Color(0xFFe91c1c)
val green500 = Color(0xFF1FA363)

val routine = Color(0xFFF39200)
val quiz = Color(0xFFF76357)
val assignment = Color(0xFFCF5486)
val presentation = Color(0xFF8A5995)
val project = Color(0xFF495681)

val Colors.fees: Color @Composable get() = Color(0xFFF39200)
val Colors.event: Color @Composable get() = Color(0xFFF39200)
val Colors.examRoutine: Color @Composable get() = Color(0xFFF39200)

val Colors.grayText: Color
    @Composable get() = LocalContentColor.current.lighten(0.55f)


val Colors.grayBackground: Color
    @Composable get() = MaterialTheme.colors.background.darken(0.05f)


val Colors.grayBorder: Color
    @Composable get() = MaterialTheme.colors.onBackground.copy(alpha = 0.08f)