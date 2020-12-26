package io.github.diubruteforce.smartcr.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.model.data.Routine
import io.github.diubruteforce.smartcr.ui.theme.CornerRadius
import io.github.diubruteforce.smartcr.ui.theme.Margin

@Composable
fun RoutineListItem(
    modifier: Modifier = Modifier,
    routine: Routine,
    onEdit: (Routine) -> Unit,
    onDelete: (Routine) -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.normal),
        elevation = 8.dp,
        border = BorderStroke(1.dp, MaterialTheme.colors.primary)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.size(Margin.normal))

            Column(
                modifier = Modifier.weight(2f),
            ) {
                LabelText(
                    label = stringResource(id = R.string.room),
                    text = routine.room
                )

                Spacer(modifier = Modifier.size(Margin.tiny))

                LabelText(
                    label = stringResource(id = R.string.time),
                    text = "${routine.startTime} - ${routine.endTime}"
                )
            }

            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(CornerRadius.normal),
                elevation = 0.dp,
                backgroundColor = MaterialTheme.colors.primary
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        modifier = Modifier.align(Alignment.Center)
                            .padding(vertical = Margin.medium),
                        text = routine.day,
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )

                    UpdateDeleteMenu(
                        modifier = Modifier.align(Alignment.TopEnd).offset(y = (-16).dp),
                        onEdit = { onEdit.invoke(routine) },
                        onDelete = { onDelete.invoke(routine) }
                    )
                }
            }
        }
    }
}
