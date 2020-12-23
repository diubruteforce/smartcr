package io.github.diubruteforce.smartcr.ui.smartcr.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.vectorResource
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dev.chrisbanes.accompanist.insets.statusBarsPadding
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.ui.common.Empty

@Composable
fun HomeScreen(
    navigateToOnBoarding: () -> Unit
) {
    Empty(
        title = "New Semester!",
        message = "DIU DIU DIU DIU DIU DIU DIU DIU DIU DIU DIU DIU",
        image = vectorResource(id = R.drawable.new_semseter)
    )
}

@Composable
private fun HomeScreenContent() {
    Column(
        modifier = Modifier.fillMaxSize().statusBarsPadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            Firebase.auth.signOut()
        }) {
            Text(text = "Logout")
        }
    }
}