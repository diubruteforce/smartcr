package io.github.diubruteforce.smartcr.ui.onboading

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import io.github.diubruteforce.smartcr.R
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    navigateToOnBoarding: () -> Unit,
    navigateToVerification: (String) -> Unit,
    navigateToProfileEdit: () -> Unit,
    navigateToHome: () -> Unit
){
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(true) {
        coroutineScope.launch {
            if (viewModel.isAuthenticated()) {
                if (viewModel.isEmailVerified()) {
                    if (viewModel.hasProfileData()) {
                        navigateToHome.invoke()
                    } else {
                        navigateToProfileEdit.invoke()
                    }
                } else {
                    navigateToVerification.invoke(viewModel.getUserEmail())
                }
            } else {
                navigateToOnBoarding.invoke()
            }
        }
    }

    SplashScreenContent()
}

@Composable
private fun SplashScreenContent(

) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_primary),
            contentDescription = "SmartCR"
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewCompSplashScreenContent(){
    SplashScreenContent()
}
