package io.github.diubruteforce.smartcr.ui.examroutine

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.vectorResource
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.ui.common.BackPressTopAppBar
import io.github.diubruteforce.smartcr.ui.common.Empty


@Composable
fun ExamRoutineScreen(
    onBackPress: () -> Unit
) {
    Scaffold(topBar = {
        BackPressTopAppBar(onBackPress = onBackPress, title = "Exam Routine")
    }) {
        Empty(
            title = "No Exam Routine",
            message = "No routine found. Please check later for exam routines",
            image = vectorResource(id = R.drawable.no_exam)
        )
    }
}