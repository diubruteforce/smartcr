package io.github.diubruteforce.smartcr.ui.event

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Event
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.onActive
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.platform.AmbientFocusManager
import androidx.compose.ui.res.stringResource
import dev.chrisbanes.accompanist.insets.navigationBarsHeight
import dev.chrisbanes.accompanist.insets.navigationBarsWithImePadding
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.model.data.EventType
import io.github.diubruteforce.smartcr.ui.bottomsheet.DatePickerBottomSheet
import io.github.diubruteforce.smartcr.ui.bottomsheet.SheetHeader
import io.github.diubruteforce.smartcr.ui.bottomsheet.SheetListItem
import io.github.diubruteforce.smartcr.ui.bottomsheet.TimePicker
import io.github.diubruteforce.smartcr.ui.common.*
import io.github.diubruteforce.smartcr.ui.theme.Margin
import io.github.diubruteforce.smartcr.utils.extension.rememberBackPressAwareBottomSheetState
import io.github.diubruteforce.smartcr.utils.extension.toCalender
import io.github.diubruteforce.smartcr.utils.extension.toDateString
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalMaterialApi::class)
@Composable
fun EventEditScreen(
    viewModel: EventEditViewModel,
    eventId: String?,
    onBackPress: () -> Unit
) {
    val sideEffect = viewModel.sideEffect.collectAsState().value
    val sheetState = rememberBackPressAwareBottomSheetState()
    val activity = AmbientContext.current as AppCompatActivity

    onActive {
        viewModel.loadData(eventId)
    }

    SideEffect(
        sideEffectState = sideEffect,
        onSuccess = { if (it == EventEditSuccess.Saved) onBackPress.invoke() },
        onFailAlertDismissRequest = viewModel::clearSideEffect
    )

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            SheetHeader(
                title = stringResource(id = R.string.select_event_type),
                icon = Icons.Outlined.Event,
                onClose = sheetState::hide
            )

            EventType.values().forEach {
                SheetListItem(
                    name = it.name,
                    onSelected = {
                        viewModel.changeType(it)
                        sheetState.hide()
                    }
                )
            }

            Spacer(modifier = Modifier.navigationBarsHeight())
        }
    ) {
        EventEditScreenContent(
            stateFlow = viewModel.state,
            onTitleChange = viewModel::changeTitle,
            selectType = sheetState::show,
            selectDate = { date ->
                val datePicker = DatePickerBottomSheet(date.toCalender()) {
                    viewModel.changeDate(it.toDateString())
                }

                datePicker.show(activity.supportFragmentManager, "Date")
            },
            selectTime = {
                val timePicker = TimePicker(time = it, onResult = viewModel::changeTime)
                timePicker.show(activity.supportFragmentManager, "Time")
            },
            onPlaceChange = viewModel::changePlace,
            onDetailsChange = viewModel::changeDetails,
            saveEvent = viewModel::saveEvent,
            onBackPress = onBackPress
        )
    }
}

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalComposeUiApi::class)
@Composable
private fun EventEditScreenContent(
    stateFlow: StateFlow<EventEditState>,
    onTitleChange: (String) -> Unit,
    selectType: () -> Unit,
    selectDate: (String) -> Unit,
    selectTime: (String) -> Unit,
    onPlaceChange: (String) -> Unit,
    onDetailsChange: (String) -> Unit,
    saveEvent: () -> Unit,
    onBackPress: () -> Unit
) {
    val state = stateFlow.collectAsState().value

    val focusManager = AmbientFocusManager.current
    val (placeFocusRequester,
        titleFocusRequester,
        detailsFocusRequester) = FocusRequester.createRefs()

    Scaffold(
        topBar = {
            BackPressTopAppBar(
                onBackPress = onBackPress,
                title = stringResource(id = R.string.edit_event)
            )
        }
    ) {
        ScrollableColumn(
            modifier = Modifier.navigationBarsWithImePadding(),
            contentPadding = PaddingValues(Margin.normal),
            verticalArrangement = Arrangement.spacedBy(Margin.small)
        ) {
            FullName(
                state = state.title,
                onValueChange = onTitleChange,
                placeHolder = stringResource(id = R.string.title),
                focusRequester = titleFocusRequester,
                onImeActionPerformed = {
                    selectType.invoke()
                    focusManager.clearFocus()
                }
            )

            CRSelection(
                state = state.type,
                placeHolder = stringResource(id = R.string.type),
                onClick = {
                    selectType.invoke()
                    focusManager.clearFocus()
                }
            )

            CRSelection(
                state = state.date,
                placeHolder = stringResource(id = R.string.date),
                onClick = { selectDate.invoke(state.date.value) }
            )

            CRSelection(
                state = state.time,
                placeHolder = stringResource(id = R.string.time),
                onClick = { selectTime.invoke(state.time.value) }
            )

            Description(
                state = state.place,
                onValueChange = onPlaceChange,
                placeHolder = stringResource(id = R.string.place),
                focusRequester = placeFocusRequester
            )

            Description(
                state = state.details,
                onValueChange = onDetailsChange,
                placeHolder = stringResource(id = R.string.detail),
                focusRequester = detailsFocusRequester
            )

            LargeButton(text = stringResource(id = R.string.save), onClick = saveEvent)
        }
    }
}