package io.github.diubruteforce.smartcr.ui.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.diubruteforce.smartcr.ui.theme.Margin

@Composable
fun LargeButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
){
    Button(
        modifier = modifier.fillMaxWidth().heightIn(min = 48.dp),
        onClick = onClick
    ) {
        Text(text = text)
    }
}