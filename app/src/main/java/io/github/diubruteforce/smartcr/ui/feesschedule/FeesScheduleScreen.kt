package io.github.diubruteforce.smartcr.ui.feesschedule

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Money
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.onActive
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.platform.AmbientFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import dev.chrisbanes.accompanist.insets.AmbientWindowInsets
import dev.chrisbanes.accompanist.insets.navigationBarsHeight
import dev.chrisbanes.accompanist.insets.navigationBarsPadding
import dev.chrisbanes.accompanist.insets.toPaddingValues
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.model.data.FeesReason
import io.github.diubruteforce.smartcr.model.data.FeesSchedule
import io.github.diubruteforce.smartcr.model.ui.StringFailSideEffectState
import io.github.diubruteforce.smartcr.model.ui.TypedSideEffectState
import io.github.diubruteforce.smartcr.ui.bottomsheet.DatePickerBottomSheet
import io.github.diubruteforce.smartcr.ui.bottomsheet.SheetHeader
import io.github.diubruteforce.smartcr.ui.bottomsheet.SheetListItem
import io.github.diubruteforce.smartcr.ui.common.*
import io.github.diubruteforce.smartcr.ui.theme.Margin
import io.github.diubruteforce.smartcr.ui.theme.fees
import io.github.diubruteforce.smartcr.utils.extension.rememberBackPressAwareBottomSheetState
import io.github.diubruteforce.smartcr.utils.extension.rememberOnBackPressCallback
import io.github.diubruteforce.smartcr.utils.extension.toCalender
import io.github.diubruteforce.smartcr.utils.extension.toDateString
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterialApi::class, ExperimentalCoroutinesApi::class)
@Composable
fun FeesScheduleScreen(
    viewModel: FeesScheduleViewModel,
    onBackPress: () -> Unit
) {
    val sheetState = rememberBackPressAwareBottomSheetState()
    val sideEffect = viewModel.sideEffect.collectAsState().value
    val focusManager = AmbientFocusManager.current
    val activity = AmbientContext.current as AppCompatActivity

    onActive {
        viewModel.loadData()
    }

    SideEffect(
        sideEffectState = sideEffect,
        onSuccess = {},
        onFailAlertDismissRequest = viewModel::clearSideEffect
    )

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            SheetHeader(
                title = stringResource(id = R.string.select_fees_for),
                icon = Icons.Outlined.Money,
                onClose = sheetState::hide
            )

            FeesReason.values().forEach {
                SheetListItem(
                    name = it.title,
                    onSelected = {
                        viewModel.changeFeesFor(it)
                        sheetState.hide()
                    }
                )
            }

            Spacer(modifier = Modifier.navigationBarsHeight())
        }
    ) {
        FeesScheduleScreenContent(
            sideEffectState = sideEffect,
            stateFlow = viewModel.state,
            onEdit = viewModel::startEditing,
            onDelete = viewModel::deleteFeesSchedule,
            saveFeesSchedule = viewModel::saveFeesSchedule,
            onBatchCodeChange = viewModel::changeBatchCode,
            changeFeesFor = {
                sheetState.show()
                focusManager.clearFocus()
            },
            changeLastDate = { dateString ->
                val datePicker = DatePickerBottomSheet(dateString.toCalender()) { calender ->
                    viewModel.changeLastDate(calender.toDateString())
                }
                datePicker.show(activity.supportFragmentManager, "last date")
            },
            cancelEditing = viewModel::clearEditing,
            onBackPress = onBackPress
        )
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
private fun FeesScheduleScreenContent(
    sideEffectState: StringFailSideEffectState,
    stateFlow: StateFlow<FeesScheduleState>,
    onEdit: (FeesSchedule) -> Unit,
    onDelete: (FeesSchedule) -> Unit,
    saveFeesSchedule: () -> Unit,
    onBatchCodeChange: (String) -> Unit,
    changeFeesFor: () -> Unit,
    changeLastDate: (String) -> Unit,
    cancelEditing: () -> Unit,
    onBackPress: () -> Unit
) {
    val state = stateFlow.collectAsState().value
    val inset = AmbientWindowInsets.current
    val onBackPressCallback = rememberOnBackPressCallback(onBackPress = cancelEditing)
    onBackPressCallback.isEnabled = state.editingFeesSchedule != null

    val titleRes = if (state.editingFeesSchedule == null) R.string.fees_schedule
    else R.string.edit_fees_schedule

    Scaffold(
        topBar = {
            BackPressTopAppBar(
                onBackPress = {
                    if (state.editingFeesSchedule == null) onBackPress.invoke()
                    else cancelEditing.invoke()
                },
                title = stringResource(id = titleRes)
            )
        },
        floatingActionButton = {
            if (state.editingFeesSchedule == null) {
                FloatingActionButton(
                    modifier = Modifier.navigationBarsPadding(),
                    onClick = { onEdit.invoke(FeesSchedule()) },
                    backgroundColor = MaterialTheme.colors.primary
                ) {
                    Icon(imageVector = Icons.Outlined.Add)
                }
            }
        }
    ) {
        ScrollableColumn(
            verticalArrangement = Arrangement.spacedBy(Margin.normal),
            contentPadding = inset.navigationBars.toPaddingValues().copy(
                start = Margin.normal,
                end = Margin.normal,
                top = Margin.normal
            )
        ) {
            when {
                state.editingFeesSchedule != null -> {
                    FeesScheduleEdit(
                        state = state,
                        onBatchCodeChange = onBatchCodeChange,
                        changeFeesFor = changeFeesFor,
                        changeLastDate = changeLastDate,
                        saveFeesSchedule = saveFeesSchedule,
                        cancelEditing = cancelEditing
                    )
                }
                sideEffectState is TypedSideEffectState.Success
                        && state.feesSchedules.isEmpty() -> {
                    Empty(
                        title = "No Schedule",
                        message = "No schedule found. Please check later for fees schedule",
                        image = vectorResource(id = R.drawable.no_fees)
                    )
                }
                else -> {
                    state.feesSchedules.forEach { feesSchedule ->
                        PostCard(
                            title = "Batch Code: ${feesSchedule.batchCode}",
                            firstRow = "Last Date: ${feesSchedule.lastDate}",
                            secondRow = "Fees for: ${feesSchedule.feesFor}",
                            color = MaterialTheme.colors.fees,
                            onItemClick = {},
                            onEdit = { onEdit.invoke(feesSchedule) },
                            onDelete = { onDelete.invoke(feesSchedule) }
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun ColumnScope.FeesScheduleEdit(
    state: FeesScheduleState,
    onBatchCodeChange: (String) -> Unit,
    changeFeesFor: () -> Unit,
    changeLastDate: (String) -> Unit,
    saveFeesSchedule: () -> Unit,
    cancelEditing: () -> Unit
) {
    val focusManager = AmbientFocusManager.current
    val roomFocusRequester = FocusRequester()

    FullName(
        state = state.batchCode,
        onValueChange = onBatchCodeChange,
        placeHolder = stringResource(id = R.string.batch_code),
        focusRequester = roomFocusRequester,
        onImeActionPerformed = {
            focusManager.clearFocus()
            changeFeesFor.invoke()
        }
    )

    CRSelection(
        state = state.feesFor,
        placeHolder = stringResource(id = R.string.fees_for),
        onClick = {
            focusManager.clearFocus()
            changeFeesFor.invoke()
        }
    )

    CRSelection(
        state = state.lastDate,
        placeHolder = stringResource(id = R.string.last_date),
        onClick = {
            focusManager.clearFocus()
            changeLastDate.invoke(state.lastDate.value)
        }
    )

    Row(horizontalArrangement = Arrangement.spacedBy(Margin.small)) {
        OutlinedButton(
            modifier = Modifier.weight(1f),
            onClick = {
                focusManager.clearFocus()
                cancelEditing.invoke()
            }
        ) {
            Text(text = stringResource(id = R.string.cancel))
        }

        Button(
            modifier = Modifier.weight(1f),
            onClick = {
                focusManager.clearFocus()
                saveFeesSchedule.invoke()
            }
        ) {
            Text(text = stringResource(id = R.string.save))
        }
    }
}