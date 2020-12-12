package io.github.diubruteforce.smartcr.ui.onboading

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.chrisbanes.accompanist.insets.navigationBarsPadding

@Composable
fun SignUpScreen(
    navigateToHome: () -> Unit
){
    Column(modifier = Modifier.fillMaxSize().background(Color.Yellow)) {
        Button(modifier = Modifier.navigationBarsPadding(), onClick = navigateToHome) {
            Text(text = "Click")
        }
    }
}