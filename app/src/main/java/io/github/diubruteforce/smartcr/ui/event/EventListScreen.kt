package io.github.diubruteforce.smartcr.ui.event

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.chrisbanes.accompanist.insets.LocalWindowInsets
import dev.chrisbanes.accompanist.insets.navigationBarsPadding
import dev.chrisbanes.accompanist.insets.toPaddingValues
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.model.ui.StringFailSideEffectState
import io.github.diubruteforce.smartcr.model.ui.TypedSideEffectState
import io.github.diubruteforce.smartcr.ui.common.BackPressTopAppBar
import io.github.diubruteforce.smartcr.ui.common.Empty
import io.github.diubruteforce.smartcr.ui.common.SideEffect
import io.github.diubruteforce.smartcr.ui.theme.CornerRadius
import io.github.diubruteforce.smartcr.ui.theme.Margin
import io.github.diubruteforce.smartcr.ui.theme.event
import io.github.diubruteforce.smartcr.ui.theme.grayText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun EventListScreen(
    viewModel: EventListViewModel,
    navigateToEventEdit: () -> Unit,
    navigateToEventDetail: (String) -> Unit,
    onBackPress: () -> Unit
) {
    val sideEffect = viewModel.sideEffect.collectAsState().value

    LaunchedEffect(true) {
        viewModel.loadData()
    }

    SideEffect(
        sideEffectState = sideEffect,
        onSuccess = {},
        onFailAlertDismissRequest = viewModel::clearSideEffect
    )

    EVentListScreenContent(
        sideEffectState = sideEffect,
        stateFlow = viewModel.state,
        createNewEvent = navigateToEventEdit,
        navigateToEventDetail = navigateToEventDetail,
        onBackPress = onBackPress
    )
}

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
private fun EVentListScreenContent(
    sideEffectState: StringFailSideEffectState,
    stateFlow: StateFlow<EventListState>,
    createNewEvent: () -> Unit,
    navigateToEventDetail: (String) -> Unit,
    onBackPress: () -> Unit
) {
    val state = stateFlow.collectAsState().value
    val inset = LocalWindowInsets.current

    Scaffold(
        topBar = {
            BackPressTopAppBar(onBackPress = onBackPress, title = "Events")
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.navigationBarsPadding(),
                onClick = createNewEvent,
                backgroundColor = MaterialTheme.colors.primary
            ) {
                Icon(imageVector = Icons.Outlined.Add, contentDescription = null)
            }
        }
    ) {
        if (sideEffectState is TypedSideEffectState.Success && state.events.isEmpty()) {
            Empty(
                title = "No Event",
                message = "No event found. Please check later for new events",
                image = painterResource(id = R.drawable.no_extra)
            )
        } else {
            LazyColumn(
                contentPadding = inset.navigationBars.toPaddingValues(
                    additionalStart = Margin.normal,
                    additionalEnd = Margin.normal,
                    additionalTop = Margin.normal
                ),
                verticalArrangement = Arrangement.spacedBy(Margin.normal)
            ) {
                items(state.events) { event ->
                    EventCard(
                        title = event.title,
                        date = event.date,
                        time = event.time,
                        type = event.type,
                        onItemClick = { navigateToEventDetail.invoke(event.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EventCard(
    modifier: Modifier = Modifier,
    title: String,
    date: String,
    time: String,
    type: String,
    onItemClick: () -> Unit
) {
    val interactionState = remember { MutableInteractionSource() }
    val eventColor = MaterialTheme.colors.event

    Card(
        modifier = modifier.clickable(
            onClick = onItemClick,
            interactionSource = interactionState,
            indication = null
        ),
        shape = RoundedCornerShape(CornerRadius.normal),
        elevation = 4.dp,
        border = BorderStroke(1.dp, eventColor.copy(alpha = 0.4f))
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
                    color = eventColor,
                    maxLines = 1
                )

                Row {
                    Text(
                        text = "$date at $time",
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.grayText,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = type,
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.grayText,
                        maxLines = 1
                    )
                }
            }
        }
    }
}