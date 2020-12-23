package io.github.diubruteforce.smartcr.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import io.github.diubruteforce.smartcr.ui.theme.Margin
import timber.log.Timber

@Composable
fun InputLayout(
    modifier: Modifier = Modifier,
    isError: Boolean,
    errorText: String,
    content: @Composable () -> Unit
) {
    val errorColor = if (isError) MaterialTheme.colors.error else Color.Transparent

    Timber.d("error: $isError errorText: $errorText")

    Column(modifier = modifier) {
        content()

        Text(
            text = errorText,
            color = errorColor,
            style = MaterialTheme.typography.body2.copy(fontSize = 12.sp),
            modifier = Modifier.padding(start = Margin.small, bottom = Margin.tiny),
        )
    }
}