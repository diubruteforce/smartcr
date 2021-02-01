package io.github.diubruteforce.smartcr.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.chrisbanes.accompanist.insets.navigationBarsPadding
import io.github.diubruteforce.smartcr.ui.theme.Margin
import io.github.diubruteforce.smartcr.ui.theme.grayText

@Composable
fun Empty(
    modifier: Modifier = Modifier,
    title: String,
    message: String,
    image: ImageVector,
    actionTitle: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .heightIn(min = 600.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.weight(0.5f))

        Image(
            modifier = Modifier.fillMaxWidth(0.4f),
            imageVector = image,
            contentDescription = "Empty Screen Image"
        )

        Spacer(modifier = Modifier.weight(0.3f))

        Text(
            text = title,
            style = MaterialTheme.typography.h5
        )

        Spacer(modifier = Modifier.size(Margin.small))

        Text(
            modifier = Modifier.fillMaxWidth(0.6f),
            text = message,
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.grayText,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.weight(1f))

        if (actionTitle != null && onAction != null) {
            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Margin.big),
                border = BorderStroke(1.dp, MaterialTheme.colors.primary),
                onClick = onAction
            ) {
                Text(text = actionTitle)
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}