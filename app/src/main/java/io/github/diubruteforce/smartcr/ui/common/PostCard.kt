package io.github.diubruteforce.smartcr.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.diubruteforce.smartcr.ui.theme.CornerRadius
import io.github.diubruteforce.smartcr.ui.theme.Margin
import io.github.diubruteforce.smartcr.ui.theme.SmartCRTheme
import io.github.diubruteforce.smartcr.ui.theme.grayText

@Composable
fun PostCard(
    modifier: Modifier = Modifier,
    title: String,
    firstRow: String,
    secondRow: String,
    color: Color,
    onItemClick: () -> Unit,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null
) {
    val interactionState = remember { MutableInteractionSource() }

    Card(
        modifier = modifier.clickable(
            onClick = onItemClick,
            interactionSource = interactionState,
            indication = null
        ),
        shape = RoundedCornerShape(CornerRadius.normal),
        elevation = 4.dp,
        border = BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .indication(interactionState, LocalIndication.current)
                .padding(horizontal = Margin.normal, vertical = Margin.small),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Margin.tiny)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.W400,
                    color = color,
                    maxLines = 1
                )

                Text(
                    text = firstRow,
                    style = MaterialTheme.typography.body1,
                    maxLines = 1
                )

                Text(
                    text = secondRow,
                    style = MaterialTheme.typography.body1,
                    maxLines = 1
                )
            }

            if (onEdit != null && onDelete != null) {
                UpdateDeleteMenu(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 16.dp, y = (-16).dp),
                    iconColor = MaterialTheme.colors.grayText,
                    onEdit = onEdit,
                    onDelete = onDelete
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewPostCard() {
    SmartCRTheme {
        LazyColumn(
            contentPadding = PaddingValues(Margin.normal),
            verticalArrangement = Arrangement.spacedBy(Margin.normal)
        ) {
            item {
                PostCard(
                    title = "CSE323 (Class)",
                    firstRow = "Time: 10:00 AM - 12:00 PM",
                    secondRow = "Room: 343 DT5",
                    color = MaterialTheme.colors.primary,
                    onItemClick = {},
                    onDelete = {},
                    onEdit = {}
                )
            }

            (0..10).forEach {
                item {
                    PostCard(
                        title = "CSE323 (Section $it)",
                        firstRow = "Time: 10:00 AM - 12:00 PM",
                        secondRow = "Room: 343 DT5",
                        color = MaterialTheme.colors.primary,
                        onItemClick = {}
                    )
                }
            }
        }
    }
}