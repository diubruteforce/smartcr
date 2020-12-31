package io.github.diubruteforce.smartcr.ui.feesschedule

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.vectorResource
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.ui.common.BackPressTopAppBar
import io.github.diubruteforce.smartcr.ui.common.Empty


@Composable
fun FeesScheduleScreen(
    onBackPress: () -> Unit
) {
    Scaffold(topBar = {
        BackPressTopAppBar(onBackPress = onBackPress, title = "Fees Schedule")
    }) {
        Empty(
            title = "No Schedule",
            message = "No schedule found. Please check later for fees schedule",
            image = vectorResource(id = R.drawable.no_fees)
        )
    }
}