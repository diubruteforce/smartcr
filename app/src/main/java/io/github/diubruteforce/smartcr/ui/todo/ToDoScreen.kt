package io.github.diubruteforce.smartcr.ui.todo

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.ui.common.Empty

@Composable
fun ToDoScreen() {
    Empty(
        title = stringResource(id = R.string.no_todo),
        message = stringResource(id = R.string.no_todo_message),
        image = vectorResource(id = R.drawable.nothing_todo)
    )
}