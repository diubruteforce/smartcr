package io.github.diubruteforce.smartcr.ui.theme

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val orange200 = Color(0xFFF0b185)
val orange500 = Color(0xFFF39200)
val redError = Color(0xFFe91c1c)

@Composable
val Colors.grayText get()  = MaterialTheme.colors.onBackground.copy(alpha = 0.5f)
@Composable
val Colors.grayBackground get()  = Color(0xFFC2C2C2) //MaterialTheme.colors.onBackground.copy(alpha = 0.05f)