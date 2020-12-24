package io.github.diubruteforce.smartcr.ui.teacher

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.chrisbanes.accompanist.insets.navigationBarsWithImePadding
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.model.data.CounselingHourState
import io.github.diubruteforce.smartcr.model.data.Week
import io.github.diubruteforce.smartcr.ui.bottomsheet.ListBottomSheet
import io.github.diubruteforce.smartcr.ui.bottomsheet.TimePicker
import io.github.diubruteforce.smartcr.ui.common.*
import io.github.diubruteforce.smartcr.ui.theme.Margin
import io.github.diubruteforce.smartcr.utils.extension.getMainActivity
import io.github.diubruteforce.smartcr.utils.extension.rememberBackPressAwareBottomSheetState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterialApi::class, ExperimentalCoroutinesApi::class)
@Composable
fun TeacherDetailScreen(
    viewModel: TeacherDetailViewModel,
    teacherId: String,
    navigateToTeacherEdit: (String) -> Unit,
    onBackPress: () -> Unit
) {
    val sheetState = rememberBackPressAwareBottomSheetState()
    val sideEffect = viewModel.sideEffect.collectAsState().value
    val mainActivity = getMainActivity()

    var deleteCounselingHour by remember { mutableStateOf<CounselingHourState?>(null) }
    var deleteTeacherProfile by remember { mutableStateOf<String?>(null) }

    onActive {
        viewModel.loadInitialData(teacherId)
    }

    SideEffect(
        sideEffectState = sideEffect,
        onSuccess = {
            if (it == TeacherDetailSuccess.ProfileDeleted) onBackPress.invoke()
        },
        onFailAlertDismissRequest = viewModel::clearSideEffect
    )

    if (deleteCounselingHour != null) {
        CRAlertDialog(
            title = stringResource(id = R.string.are_you_sure),
            message = stringResource(id = R.string.counseling_hour_delete),
            onDenial = {
                viewModel.deleteCounselingHour(deleteCounselingHour!!)
                deleteCounselingHour = null
            },
            denialText = stringResource(id = R.string.delete),
            onAffirmation = { deleteCounselingHour = null },
            affirmationText = stringResource(id = R.string.cancel),
            onDismissRequest = { deleteCounselingHour = null }
        )
    }

    if (deleteTeacherProfile != null) {
        CRAlertDialog(
            title = stringResource(id = R.string.are_you_sure),
            message = stringResource(id = R.string.teacher_profile_delete),
            onDenial = {
                viewModel.deleteProfile()
                deleteTeacherProfile = null
            },
            denialText = stringResource(id = R.string.delete),
            onAffirmation = { deleteTeacherProfile = null },
            affirmationText = stringResource(id = R.string.cancel),
            onDismissRequest = { deleteTeacherProfile = null }
        )
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            ListBottomSheet(
                title = stringResource(id = R.string.select_day),
                icon = Icons.Outlined.CalendarToday,
                onClose = sheetState::hide,
                list = Week.values().toList(),
                onItemClick = {
                    viewModel.changeDay(it)
                    sheetState.hide()
                }
            )
        }
    ) {
        TeacherDetailScreenContent(
            stateFlow = viewModel.state,
            onProfileEdit = navigateToTeacherEdit,
            onProfileDelete = { deleteTeacherProfile = it },
            onCall = { mainActivity.makeCall(it) },
            onCounselingHourEdit = viewModel::startEditCounselingHour,
            onCounselingHourDelete = { deleteCounselingHour = it },
            onCounselingHourAdd = viewModel::startAddCounselingHour,
            changeDay = sheetState::show,
            changeStartTime = {
                val timePicker = TimePicker(time = it, onResult = viewModel::changeStartTime)
                timePicker.show(mainActivity.supportFragmentManager, "StartTime")
            },
            changeEndTime = {
                val timePicker = TimePicker(time = it, onResult = viewModel::changeEndTime)
                timePicker.show(mainActivity.supportFragmentManager, "EndTime")
            },
            cancelCounselingEditing = viewModel::cancelCounselingEditing,
            saveCounseling = viewModel::saveCounselingHour,
            onBackPress = onBackPress
        )
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
private fun TeacherDetailScreenContent(
    stateFlow: StateFlow<TeacherDetailState>,
    onProfileEdit: (String) -> Unit,
    onProfileDelete: (String) -> Unit,
    onCall: (String) -> Unit,
    onCounselingHourEdit: (CounselingHourState) -> Unit,
    onCounselingHourDelete: (CounselingHourState) -> Unit,
    onCounselingHourAdd: () -> Unit,
    changeDay: () -> Unit,
    changeStartTime: (String) -> Unit,
    changeEndTime: (String) -> Unit,
    cancelCounselingEditing: () -> Unit,
    saveCounseling: () -> Unit,
    onBackPress: () -> Unit
) {
    val state = stateFlow.collectAsState().value

    Scaffold(
        topBar = {
            ProfileTopAppBar(
                title = stringResource(id = R.string.facult_profile),
                navigationIcon = {
                    IconButton(onClick = onBackPress) {
                        Icon(imageVector = Icons.Outlined.KeyboardArrowLeft)
                    }
                },
                actions = { IconButton(onClick = { }) {} },
                imageUrl = state.teacher?.profileUrl ?: "",
                imageCaption = {
                    Text(text = state.teacher?.fullName ?: "")
                }
            )
        }
    ) {
        ScrollableColumn(
            modifier = Modifier.navigationBarsWithImePadding(),
            verticalArrangement = Arrangement.spacedBy(Margin.normal),
            contentPadding = PaddingValues(
                top = Margin.big,
                start = Margin.normal,
                end = Margin.normal,
                bottom = Margin.large
            )
        ) {
            if (state.isEditMode) {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = state.editTitle,
                    style = MaterialTheme.typography.h5
                )

                CRSelection(
                    state = state.day,
                    placeHolder = stringResource(id = R.string.select_day),
                    onClick = changeDay
                )

                CRSelection(
                    state = state.startTime,
                    placeHolder = stringResource(id = R.string.start_time),
                    onClick = { changeStartTime.invoke(state.startTime.value) }
                )

                CRSelection(
                    state = state.endTime,
                    placeHolder = stringResource(id = R.string.end_time),
                    onClick = { changeEndTime.invoke(state.endTime.value) }
                )

                Row(horizontalArrangement = Arrangement.spacedBy(Margin.normal)) {
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = cancelCounselingEditing
                    ) {
                        Text(text = stringResource(id = R.string.cancel))
                    }

                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = saveCounseling
                    ) {
                        Text(text = stringResource(id = R.string.save))
                    }
                }

            } else {
                state.teacher?.let {
                    TeacherProfileCard(
                        teacher = it,
                        onCall = onCall,
                        onEdit = onProfileEdit,
                        onDelete = onProfileDelete
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.counseling_hour),
                        style = MaterialTheme.typography.h5
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(onClick = onCounselingHourAdd) {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            tint = MaterialTheme.colors.primary
                        )
                    }
                }

                state.counselingHours.forEach {
                    CounselingHour(
                        state = it,
                        onEdit = onCounselingHourEdit,
                        onDelete = onCounselingHourDelete
                    )
                }
            }
        }
    }
}