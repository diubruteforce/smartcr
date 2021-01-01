package io.github.diubruteforce.smartcr.ui.event

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.chrisbanes.accompanist.insets.navigationBarsPadding
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.ui.common.*
import io.github.diubruteforce.smartcr.ui.theme.Margin
import io.github.diubruteforce.smartcr.ui.theme.event
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun EventDetailScreen(
    viewModel: EventDetailsViewModel,
    eventId: String,
    navigateToEventEdit: (String) -> Unit,
    onBackPress: () -> Unit
) {
    val sideEffect = viewModel.sideEffect.collectAsState().value
    var deleteEvent by remember { mutableStateOf(false) }

    onActive {
        viewModel.loadData(eventId)
    }

    SideEffect(
        sideEffectState = sideEffect,
        onSuccess = { if (it == EventDetailSuccess.Deleted) onBackPress.invoke() },
        onFailAlertDismissRequest = viewModel::clearSideEffect
    )

    if (deleteEvent) {
        CRAlertDialog(
            title = stringResource(id = R.string.are_you_sure),
            message = stringResource(id = R.string.event_delete),
            onDenial = {
                viewModel.deleteEvent()
                deleteEvent = false
            },
            denialText = stringResource(id = R.string.delete),
            onAffirmation = { deleteEvent = false },
            affirmationText = stringResource(id = R.string.cancel),
            onDismissRequest = { deleteEvent = false }
        )
    }

    EventDetailScreenContent(
        stateFlow = viewModel.state,
        navigateToEventEdit = { navigateToEventEdit.invoke(eventId) },
        deleteEvent = { deleteEvent = true },
        onBackPress = onBackPress
    )
}

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
private fun EventDetailScreenContent(
    stateFlow: StateFlow<EventDetailsState>,
    navigateToEventEdit: () -> Unit,
    deleteEvent: () -> Unit,
    onBackPress: () -> Unit
) {
    val state = stateFlow.collectAsState().value

    Scaffold(
        topBar = {
            InsetAwareTopAppBar {
                IconButton(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    onClick = onBackPress
                ) {
                    Icon(imageVector = Icons.Outlined.KeyboardArrowLeft)
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = stringResource(id = R.string.event_details),
                )

                Spacer(modifier = Modifier.weight(1f))

                UpdateDeleteMenu(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    icon = Icons.Outlined.MoreVert,
                    onEdit = navigateToEventEdit,
                    onDelete = deleteEvent
                )
            }
        }
    ) {
        ScrollableColumn(
            modifier = Modifier.navigationBarsPadding(),
            contentPadding = PaddingValues(Margin.normal),
            verticalArrangement = Arrangement.spacedBy(Margin.tiny)
        ) {
            state.event?.let { event ->
                Spacer(modifier = Modifier.size(Margin.normal))

                Text(
                    text = event.title,
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.event
                )

                Spacer(modifier = Modifier.size(Margin.normal))

                LabelText(label = stringResource(id = R.string.type), text = event.type)
                LabelText(label = stringResource(id = R.string.date), text = event.date)
                LabelText(label = stringResource(id = R.string.time), text = event.time)
                LabelText(label = stringResource(id = R.string.place), text = event.place)

                TitleRow(
                    title = stringResource(id = R.string.detail),
                    onEdit = navigateToEventEdit
                )

                Text(text = event.details)
            }
        }
    }
}