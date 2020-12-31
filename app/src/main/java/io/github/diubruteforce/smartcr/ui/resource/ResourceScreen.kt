package io.github.diubruteforce.smartcr.ui.resource

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.ui.common.Empty

@Composable
fun ResourceScreen() {
    Empty(
        title = stringResource(id = R.string.no_resource),
        message = stringResource(id = R.string.no_resource_message),
        image = vectorResource(id = R.drawable.no_exam)
    )
}