package io.github.diubruteforce.smartcr.ui.event

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.vectorResource
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.ui.common.BackPressTopAppBar
import io.github.diubruteforce.smartcr.ui.common.Empty

@Composable
fun EventScreen(
    onBackPress: () -> Unit
) {
    Scaffold(topBar = {
        BackPressTopAppBar(onBackPress = onBackPress, title = "Event")
    }) {
        Empty(
            title = "No Event",
            message = "No event found. Please check later for new events",
            image = vectorResource(id = R.drawable.no_extra)
        )
    }
}