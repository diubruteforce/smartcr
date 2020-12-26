package io.github.diubruteforce.smartcr.ui.section

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.AmbientFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.chrisbanes.accompanist.insets.navigationBarsWithImePadding
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.model.data.Routine
import io.github.diubruteforce.smartcr.model.data.Section
import io.github.diubruteforce.smartcr.model.data.Week
import io.github.diubruteforce.smartcr.ui.bottomsheet.ListBottomSheet
import io.github.diubruteforce.smartcr.ui.bottomsheet.TimePicker
import io.github.diubruteforce.smartcr.ui.common.*
import io.github.diubruteforce.smartcr.ui.theme.CornerRadius
import io.github.diubruteforce.smartcr.ui.theme.Margin
import io.github.diubruteforce.smartcr.ui.theme.grayBorder
import io.github.diubruteforce.smartcr.ui.theme.grayText
import io.github.diubruteforce.smartcr.utils.extension.getMainActivity
import io.github.diubruteforce.smartcr.utils.extension.rememberBackPressAwareBottomSheetState
import io.github.diubruteforce.smartcr.utils.extension.rememberOnBackPressCallback
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalCoroutinesApi::class)
@Composable
fun SectionDetailScreen(
    viewModel: SectionDetailViewModel,
    sectionId: String,
    navigateToSectionEdit: (String, String) -> Unit,
    navigateToTeacherDetail: (String) -> Unit,
    onBackPress: () -> Unit
) {
    val sheetState = rememberBackPressAwareBottomSheetState()
    val sideEffect = viewModel.sideEffect.collectAsState().value
    val mainActivity = getMainActivity()
    var deleteRoutineState by remember { mutableStateOf<Routine?>(null) }

    val scope = rememberCoroutineScope()

    onActive {
        viewModel.loadData(sectionId)
    }

    SideEffect(
        sideEffectState = sideEffect,
        onSuccess = { },
        onFailAlertDismissRequest = viewModel::clearSideEffect
    )

    if (deleteRoutineState != null) {
        CRAlertDialog(
            title = stringResource(id = R.string.are_you_sure),
            message = stringResource(id = R.string.routine_delete),
            onDenial = {
                viewModel.deleteRoutine(deleteRoutineState!!)
                deleteRoutineState = null
            },
            denialText = stringResource(id = R.string.delete),
            onAffirmation = { deleteRoutineState = null },
            affirmationText = stringResource(id = R.string.cancel),
            onDismissRequest = { deleteRoutineState = null }
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
        SectionDetailScreenContent(
            stateFlow = viewModel.state,
            startEditingRoutine = viewModel::startEditingRoutine,
            deleteRoutine = { deleteRoutineState = it },
            onCopy = {
                val clipBoard =
                    mainActivity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Google Code", it)
                clipBoard.setPrimaryClip(clip)
            },
            changeRoom = viewModel::changeRoom,
            selectDay = { sheetState.show() },
            selectStarTime = {
                val timePicker = TimePicker(time = it, onResult = viewModel::changeStartTime)
                timePicker.show(mainActivity.supportFragmentManager, "StartTime")
            },
            selectEndTime = {
                val timePicker = TimePicker(time = it, onResult = viewModel::changeEndTime)
                timePicker.show(mainActivity.supportFragmentManager, "Endtime")
            },
            saveRoutine = viewModel::saveRoutine,
            cancelEditing = viewModel::cancelEditing,
            navigateToSectionEdit = { courseId, sectionId ->
                scope.launch {
                    if (viewModel.canEditSection())
                        navigateToSectionEdit.invoke(courseId, sectionId)
                }
            },
            navigateToTeacherDetail = navigateToTeacherDetail,
            onBackPress = onBackPress
        )
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
private fun SectionDetailScreenContent(
    stateFlow: StateFlow<SectionDetailState>,
    startEditingRoutine: (Routine) -> Unit,
    deleteRoutine: (Routine) -> Unit,
    onCopy: (String) -> Unit,
    changeRoom: (String) -> Unit,
    selectDay: () -> Unit,
    selectStarTime: (String) -> Unit,
    selectEndTime: (String) -> Unit,
    saveRoutine: () -> Unit,
    cancelEditing: () -> Unit,
    navigateToSectionEdit: (String, String) -> Unit,
    navigateToTeacherDetail: (String) -> Unit,
    onBackPress: () -> Unit
) {
    val state = stateFlow.collectAsState().value
    val onBackPressCallback = rememberOnBackPressCallback(onBackPress = cancelEditing)

    val titleRes = if (state.editingRoutine != null) R.string.edit_routine
    else R.string.section_detail

    onBackPressCallback.isEnabled = state.editingRoutine != null

    Scaffold(
        topBar = {
            BackPressTopAppBar(
                onBackPress = if (state.editingRoutine != null) cancelEditing else onBackPress,
                title = stringResource(id = titleRes)
            )
        }
    ) {
        ScrollableColumn(
            modifier = Modifier.navigationBarsWithImePadding(),
            contentPadding = PaddingValues(Margin.normal),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = "${state.section.course.courseCode} (${state.section.name})",
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.primary
            )

            Spacer(modifier = Modifier.size(Margin.big))

            if (state.editingRoutine == null) {
                SectionDetail(
                    section = state.section,
                    routines = state.routines,
                    navigateToSectionEdit = navigateToSectionEdit,
                    navigateToTeacherDetail = navigateToTeacherDetail,
                    startEditingRoutine = startEditingRoutine,
                    deleteRoutine = deleteRoutine,
                    onCopy = onCopy
                )
            } else {
                RoutineEdit(
                    state = state,
                    onRoomChange = changeRoom,
                    changeDay = selectDay,
                    changeStartTime = selectStarTime,
                    changeEndTime = selectEndTime,
                    saveRoutine = saveRoutine,
                    cancelEditing = cancelEditing
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.RoutineEdit(
    state: SectionDetailState,
    onRoomChange: (String) -> Unit,
    changeDay: () -> Unit,
    changeStartTime: (String) -> Unit,
    changeEndTime: (String) -> Unit,
    saveRoutine: () -> Unit,
    cancelEditing: () -> Unit
) {
    val focusManager = AmbientFocusManager.current
    val roomFocusRequester = FocusRequester()

    FullName(
        state = state.room,
        onValueChange = onRoomChange,
        placeHolder = stringResource(id = R.string.room),
        focusRequester = roomFocusRequester,
        onImeActionPerformed = {
            focusManager.clearFocus()
            changeDay.invoke()
        }
    )

    CRSelection(
        state = state.day,
        placeHolder = stringResource(id = R.string.select_day),
        onClick = {
            focusManager.clearFocus()
            changeDay.invoke()
        }
    )

    CRSelection(
        state = state.startTime,
        placeHolder = stringResource(id = R.string.start_time),
        onClick = {
            focusManager.clearFocus()
            changeStartTime.invoke(state.startTime.value)
        }
    )

    CRSelection(
        state = state.endTime,
        placeHolder = stringResource(id = R.string.end_time),
        onClick = {
            focusManager.clearFocus()
            changeEndTime.invoke(state.endTime.value)
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
                saveRoutine.invoke()
            }
        ) {
            Text(text = stringResource(id = R.string.save))
        }
    }
}

@Composable
private fun ColumnScope.SectionDetail(
    section: Section,
    routines: List<Routine>,
    navigateToSectionEdit: (String, String) -> Unit,
    navigateToTeacherDetail: (String) -> Unit,
    startEditingRoutine: (Routine) -> Unit,
    deleteRoutine: (Routine) -> Unit,
    onCopy: (String) -> Unit
) {
    TitleRow(
        title = stringResource(id = R.string.course_detail),
        onEdit = { navigateToSectionEdit.invoke(section.course.courseCode, section.id) }
    )

    Text(text = "Course Name: ${section.course.courseTitle}")
    Text(text = "Course Code: ${section.course.courseCode}")
    Text(text = "Credit Hour: ${section.course.credit}")
    Text(text = "Level: ${section.course.level}")
    Text(text = "Term: ${section.course.term}")

    Spacer(modifier = Modifier.size(Margin.big))

    TitleRow(
        title = stringResource(id = R.string.instructor),
        onEdit = { navigateToSectionEdit.invoke(section.course.courseCode, section.id) }
    )

    TeacherListItem(
        instructor = section.instructor,
        itemClick = navigateToTeacherDetail
    )

    Spacer(modifier = Modifier.size(Margin.big))

    TitleRow(
        title = stringResource(id = R.string.google_classroom_code),
        onEdit = { navigateToSectionEdit.invoke(section.course.courseCode, section.id) }
    )

    CodeCopy(code = section.googleCode, onCopy = onCopy)

    Spacer(modifier = Modifier.size(Margin.big))

    TitleRow(
        title = stringResource(id = R.string.blc_classroom_code),
        onEdit = { navigateToSectionEdit.invoke(section.course.courseCode, section.id) }
    )

    CodeCopy(code = section.blcCode, onCopy = onCopy)

    Spacer(modifier = Modifier.size(Margin.big))

    TitleRow(
        title = stringResource(id = R.string.course_outline),
        onEdit = { navigateToSectionEdit.invoke(section.course.courseCode, section.id) }
    )

    Text(text = section.courseOutline)

    Spacer(modifier = Modifier.size(Margin.big))

    TitleRow(
        title = stringResource(id = R.string.routine),
        icon = Icons.Outlined.Add,
        onEdit = { startEditingRoutine.invoke(Routine()) }
    )

    routines.forEach {
        Spacer(modifier = Modifier.size(Margin.tiny))

        RoutineListItem(
            routine = it,
            onEdit = startEditingRoutine,
            onDelete = deleteRoutine
        )

        Spacer(modifier = Modifier.size(Margin.normal))
    }
}

@Composable
private fun TitleRow(
    title: String,
    icon: ImageVector = Icons.Outlined.Edit,
    onEdit: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            modifier = Modifier.weight(1f),
            text = title,
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.W500,
            color = MaterialTheme.colors.grayText
        )

        IconButton(onClick = onEdit) {
            Icon(
                imageVector = icon,
                tint = MaterialTheme.colors.grayText
            )
        }
    }
}

@Composable
private fun CodeCopy(
    modifier: Modifier = Modifier,
    code: String,
    onCopy: (String) -> Unit

) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(CornerRadius.normal),
        elevation = 4.dp,
        border = BorderStroke(1.dp, MaterialTheme.colors.grayBorder)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(start = Margin.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(2.5f),
                text = code,
                maxLines = 1,
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.W400
            )

            OutlinedButton(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(CornerRadius.normal),
                border = BorderStroke(1.dp, MaterialTheme.colors.primary),
                onClick = { onCopy.invoke(code) }
            ) {
                Text(text = stringResource(id = R.string.copy))
            }

        }
    }
}