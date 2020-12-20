package io.github.diubruteforce.smartcr.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.chrisbanes.accompanist.insets.statusBarsPadding

@Composable
fun HomeScreen(
    navigateToOnBoarding: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        Button(onClick = {

        }) {
            Text(text = "Click")
        }
    }
}