package io.github.diubruteforce.smartcr.ui.bottomsheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.diubruteforce.smartcr.ui.theme.Margin

@Composable
fun SheetListItem(name: String, onSelected: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .clickable(onClick = onSelected)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(vertical = Margin.normal)
                .padding(start = Margin.normal)
        )
        Divider()
    }
}