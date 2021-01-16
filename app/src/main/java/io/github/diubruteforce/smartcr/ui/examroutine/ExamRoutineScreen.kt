package io.github.diubruteforce.smartcr.ui.examroutine

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CastForEducation
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import dev.chrisbanes.accompanist.insets.AmbientWindowInsets
import dev.chrisbanes.accompanist.insets.navigationBarsHeight
import dev.chrisbanes.accompanist.insets.navigationBarsPadding
import dev.chrisbanes.accompanist.insets.toPaddingValues
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.model.data.ExamRoutine
import io.github.diubruteforce.smartcr.model.ui.StringFailSideEffectState
import io.github.diubruteforce.smartcr.model.ui.TypedSideEffectState
import io.github.diubruteforce.smartcr.ui.bottomsheet.DatePickerBottomSheet
import io.github.diubruteforce.smartcr.ui.bottomsheet.SheetHeader
import io.github.diubruteforce.smartcr.ui.bottomsheet.SheetListItem
import io.github.diubruteforce.smartcr.ui.bottomsheet.TimePicker
import io.github.diubruteforce.smartcr.ui.common.*
import io.github.diubruteforce.smartcr.ui.theme.Margin
import io.github.diubruteforce.smartcr.ui.theme.examRoutine
import io.github.diubruteforce.smartcr.utils.extension.rememberBackPressAwareBottomSheetState
import io.github.diubruteforce.smartcr.utils.extension.toCalender
import io.github.diubruteforce.smartcr.utils.extension.toDateString
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalMaterialApi::class)
@Composable
fun ExamRoutineScreen(
    viewModel: ExamRoutineViewModel,
    navigateToCourseList: () -> Unit,
    onBackPress: () -> Unit
) {
    val sideEffect = viewModel.sideEffect.collectAsState().value
    val sheetState = rememberBackPressAwareBottomSheetState()
    val activity = AmbientContext.current as AppCompatActivity
    var deleteExamRoutine by remember { mutableStateOf<ExamRoutine?>(null) }
    var showNoEditAlert by remember { mutableStateOf(false) }

    onActive {
        viewModel.loadData()
    }

    SideEffect(
        sideEffectState = sideEffect,
        onSuccess = { },
        onFailAlertDismissRequest = viewModel::clearSideEffect
    )

    if (deleteExamRoutine != null) {
        CRAlertDialog(
            title = stringResource(id = R.string.are_you_sure),
            message = stringResource(id = R.string.exam_routine_delete),
            onDenial = {
                viewModel.deleteExamRoutine(deleteExamRoutine!!)
                deleteExamRoutine = null
            },
            denialText = stringResource(id = R.string.delete),
            onAffirmation = { deleteExamRoutine = null },
            affirmationText = stringResource(id = R.string.cancel),
            onDismissRequest = { deleteExamRoutine = null }
        )
    }

    if (showNoEditAlert) {
        CRAlertDialog(
            title = stringResource(id = R.string.prohibited),
            message = stringResource(id = R.string.exam_routine_no_edit),
            onDenial = {},
            denialText = "",
            onAffirmation = {
                showNoEditAlert = false
                navigateToCourseList.invoke()
            },
            affirmationText = stringResource(id = R.string.join_section),
            onDismissRequest = { showNoEditAlert = false }
        )
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            SheetHeader(
                title = stringResource(id = R.string.select_course),
                icon = Icons.Outlined.CastForEducation,
                onClose = sheetState::hide
            )

            viewModel.courseList.forEach { course ->
                SheetListItem(
                    name = course.courseTitle,
                    onSelected = {
                        viewModel.changeCourse(course)
                        sheetState.hide()
                    }
                )
            }

            Spacer(modifier = Modifier.navigationBarsHeight())
        }
    ) {
        ExamRoutineScreenContent(
            sideEffectState = sideEffect,
            stateFlow = viewModel.state,
            startEdit = {
                if (viewModel.canEdit()) viewModel.startEditing(it)
                else showNoEditAlert = true
            },
            onDelete = { deleteExamRoutine = it },
            cancelEdit = viewModel::cancelEditing,
            selectCourse = sheetState::show,
            selectDate = { dateString ->
                val datePicker = DatePickerBottomSheet(dateString.toCalender()) { calender ->
                    viewModel.changeDate(calender.toDateString())
                }
                datePicker.show(activity.supportFragmentManager, "last date")
            },
            selectTime = {
                val timePicker = TimePicker(time = it, onResult = viewModel::changeTime)
                timePicker.show(activity.supportFragmentManager, "Time")
            },
            saveExamRoutine = viewModel::saveExamRoutine,
            onBackPress = onBackPress
        )
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
private fun ExamRoutineScreenContent(
    sideEffectState: StringFailSideEffectState,
    stateFlow: StateFlow<ExamRoutineState>,
    startEdit: (ExamRoutine) -> Unit,
    onDelete: (ExamRoutine) -> Unit,
    cancelEdit: () -> Unit,
    selectCourse: () -> Unit,
    selectDate: (String) -> Unit,
    selectTime: (String) -> Unit,
    saveExamRoutine: () -> Unit,
    onBackPress: () -> Unit
) {
    val state = stateFlow.collectAsState().value
    val inset = AmbientWindowInsets.current

    Scaffold(
        topBar = {
            BackPressTopAppBar(onBackPress = onBackPress, title = "Exam Routine")
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.navigationBarsPadding(),
                onClick = { startEdit.invoke(ExamRoutine()) },
                backgroundColor = MaterialTheme.colors.primary
            ) {
                Icon(imageVector = Icons.Outlined.Add)
            }
        }
    ) {
        ScrollableColumn(
            contentPadding = inset.navigationBars.toPaddingValues().copy(
                start = Margin.normal,
                end = Margin.normal,
                top = Margin.normal
            ),
            verticalArrangement = Arrangement.spacedBy(Margin.normal)
        ) {
            when {
                state.editingExamRoutine != null -> {
                    EditExamRoutine(
                        state = state,
                        cancelEdit = cancelEdit,
                        selectCourse = selectCourse,
                        selectDate = selectDate,
                        selectTime = selectTime,
                        saveExamRoutine = saveExamRoutine
                    )
                }
                sideEffectState is TypedSideEffectState.Success
                        && state.routines.isEmpty() -> {
                    Empty(
                        title = "No Exam Routine",
                        message = "No routine found. Please check later for exam routines",
                        image = vectorResource(id = R.drawable.no_exam)
                    )
                }
                else -> state.routines.forEach { examRoutine ->
                    PostCard(
                        title = examRoutine.courseCode,
                        firstRow = examRoutine.date,
                        secondRow = examRoutine.time,
                        color = MaterialTheme.colors.examRoutine,
                        onItemClick = { },
                        onEdit = { startEdit.invoke(examRoutine) },
                        onDelete = { onDelete.invoke(examRoutine) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.EditExamRoutine(
    state: ExamRoutineState,
    cancelEdit: () -> Unit,
    selectCourse: () -> Unit,
    selectDate: (String) -> Unit,
    selectTime: (String) -> Unit,
    saveExamRoutine: () -> Unit,
) {
    CRSelection(
        state = state.course,
        placeHolder = stringResource(id = R.string.course),
        onClick = selectCourse
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

    Row(horizontalArrangement = Arrangement.spacedBy(Margin.small)) {
        OutlinedButton(
            modifier = Modifier.weight(1f),
            onClick = cancelEdit
        ) {
            Text(text = stringResource(id = R.string.cancel))
        }

        Button(
            modifier = Modifier.weight(1f),
            onClick = saveExamRoutine
        ) {
            Text(text = stringResource(id = R.string.save))
        }
    }
}