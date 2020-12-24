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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.model.data.CounselingHourState
import io.github.diubruteforce.smartcr.ui.theme.CornerRadius
import io.github.diubruteforce.smartcr.ui.theme.Margin
import io.github.diubruteforce.smartcr.ui.theme.SmartCRTheme

@Composable
fun CounselingHour(
    modifier: Modifier = Modifier,
    state: CounselingHourState,
    onEdit: (CounselingHourState) -> Unit,
    onDelete: (CounselingHourState) -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.normal),
        elevation = 8.dp,
        border = BorderStroke(1.dp, MaterialTheme.colors.primary)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.size(Margin.normal))

            LabelText(
                label = stringResource(id = R.string.time),
                text = "${state.startTime} - ${state.endTime}"
            )

            Spacer(modifier = Modifier.weight(1f))

            Card(
                shape = RoundedCornerShape(CornerRadius.normal),
                elevation = 0.dp,
                backgroundColor = MaterialTheme.colors.primary
            ) {
                Box {
                    Text(
                        modifier = Modifier.padding(Margin.normal),
                        text = state.day,
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.Bold
                    )

                    UpdateDeleteMenu(
                        modifier = Modifier.align(Alignment.TopEnd).offset(y = (-16).dp),
                        onEdit = { onEdit.invoke(state) },
                        onDelete = { onDelete.invoke(state) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewCounselingHour() {
    SmartCRTheme {
        CounselingHour(
            modifier = Modifier.padding(Margin.normal),
            state = CounselingHourState(
                startTime = "10:00 PM",
                endTime = "12:00 PM",
                day = "Monday"
            ),
            onEdit = { /*TODO*/ },
            onDelete = { /*TODO*/ })
    }
}