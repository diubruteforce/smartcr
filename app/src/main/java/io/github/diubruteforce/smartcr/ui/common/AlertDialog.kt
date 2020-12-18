package io.github.diubruteforce.smartcr.ui.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.diubruteforce.smartcr.ui.theme.Margin

@Composable
fun CRAlertDialog(
    title: String,
    message: String,
    onDenial: () -> Unit,
    denialText: String,
    onAffirmation: () -> Unit,
    affirmationText: String,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.h5
            )
        },
        text = {
            Text(text = message)
        },
        buttons = {
            Row(
                modifier = Modifier.padding(horizontal = Margin.normal)
                    .padding(bottom = Margin.normal)
            ) {
                TextButton(
                    modifier = Modifier.weight(1f),
                    onClick = onDenial,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colors.error
                    )
                ) {
                    Text(text = denialText)
                }

                Spacer(modifier = Modifier.width(Margin.normal))

                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = onAffirmation
                ) {
                    Text(text = affirmationText)
                }
            }
        }
    )
}