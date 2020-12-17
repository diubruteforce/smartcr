package io.github.diubruteforce.smartcr.ui.onboading

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.vectorResource
import dev.chrisbanes.accompanist.insets.navigationBarsPadding
import io.github.diubruteforce.smartcr.R

@Composable
fun SignUpScreen(
    navigateToHome: () -> Unit
){
    Column(modifier = Modifier.fillMaxSize().background(Color.Yellow)) {
        Image(
            modifier = Modifier.fillMaxWidth().aspectRatio(375f/371f),
            imageVector = vectorResource(id = R.drawable.sign_up)
        )
    }
}