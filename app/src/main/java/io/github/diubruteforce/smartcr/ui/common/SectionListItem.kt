package io.github.diubruteforce.smartcr.ui.common

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.ui.theme.CornerRadius
import io.github.diubruteforce.smartcr.ui.theme.Margin
import io.github.diubruteforce.smartcr.ui.theme.SmartCRTheme
import io.github.diubruteforce.smartcr.ui.theme.grayBorder

@Composable
fun SectionListItem(
    modifier: Modifier = Modifier,
    text: String,
    isJoined: Boolean,
    itemClick: () -> Unit,
    onJoin: () -> Unit,
    onQuit: () -> Unit

) {
    val interactionState = remember { InteractionState() }

    Card(
        modifier = modifier.clickable(
            onClick = itemClick,
            interactionState = interactionState,
            indication = null
        ),
        shape = RoundedCornerShape(CornerRadius.normal),
        elevation = 4.dp,
        border = BorderStroke(1.dp, MaterialTheme.colors.grayBorder)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .indication(interactionState, AmbientIndication.current())
                .padding(start = Margin.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(2.5f),
                text = text,
                maxLines = 1,
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.W400
            )

            if (isJoined) {
                Button(
                    modifier = Modifier.weight(1f).heightIn(min = 56.dp),
                    onClick = onQuit,
                    shape = RoundedCornerShape(CornerRadius.normal),
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error)
                ) {
                    Text(text = stringResource(id = R.string.quit))
                }
            } else {
                OutlinedButton(
                    modifier = Modifier.weight(1f).heightIn(min = 56.dp),
                    shape = RoundedCornerShape(CornerRadius.normal),
                    border = BorderStroke(1.dp, MaterialTheme.colors.primary),
                    onClick = onJoin
                ) {
                    Text(text = stringResource(id = R.string.join))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewSectionListItem() {
    SmartCRTheme {
        Column(
            modifier = Modifier.padding(Margin.normal),
            verticalArrangement = Arrangement.spacedBy(Margin.normal)
        ) {
            (0..5).forEach {
                SectionListItem(
                    text = "Section $it",
                    isJoined = it % 2 == 1,
                    itemClick = { },
                    onJoin = { },
                    onQuit = { }
                )
            }
        }
    }
}