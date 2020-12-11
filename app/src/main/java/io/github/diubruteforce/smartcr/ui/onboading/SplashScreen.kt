package io.github.diubruteforce.smartcr.ui.onboading

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import io.github.diubruteforce.smartcr.R

@Composable
fun SplashScreen(){
    SplashScreenContent()
}

@Composable
private fun SplashScreenContent(

) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            imageVector = vectorResource(id = R.drawable.logo_primary)
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewCompSplashScreenContent(){
    SplashScreenContent()
}
