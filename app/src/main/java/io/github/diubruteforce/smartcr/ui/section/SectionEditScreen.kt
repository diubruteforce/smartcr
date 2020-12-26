package io.github.diubruteforce.smartcr.ui.section

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.SettingsCell
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.AmbientFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import dev.chrisbanes.accompanist.insets.navigationBarsWithImePadding
import dev.chrisbanes.accompanist.insets.statusBarsPadding
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.ui.bottomsheet.ListBottomSheet
import io.github.diubruteforce.smartcr.ui.bottomsheet.SheetHeader
import io.github.diubruteforce.smartcr.ui.common.*
import io.github.diubruteforce.smartcr.ui.theme.Margin
import io.github.diubruteforce.smartcr.utils.extension.rememberBackPressAwareBottomSheetState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private enum class SectionEditSheet {
    Course, Instructor, SectionName, NoCourse, NoSectionName
}

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalMaterialApi::class)
@Composable
fun SectionEditScreen(
    viewModel: SectionEditViewModel,
    sectionId: String?,
    courseId: String,
    onBackPress: () -> Unit
) {
    val sideEffect = viewModel.sideEffect.collectAsState().value
    val sheetState = rememberBackPressAwareBottomSheetState()
    val scope = rememberCoroutineScope()
    var currentSheet by remember { mutableStateOf(SectionEditSheet.Course) }

    val focusManager = AmbientFocusManager.current
    val (courseFocusRequester, instructorFocusRequester) = FocusRequester.createRefs()
    val teacherState = viewModel.teacherState.collectAsState().value
    val courseState = viewModel.courseState.collectAsState().value

    onActive {
        viewModel.loadDate(sectionId = sectionId, courseId = courseId)
    }

    SideEffect(
        sideEffectState = sideEffect,
        onSuccess = { if (it == SectionEditSuccess.Saved) onBackPress.invoke() },
        onFailAlertDismissRequest = viewModel::clearSideEffect
    )

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            when (currentSheet) {
                SectionEditSheet.Course -> {
                    Column(
                        modifier = Modifier.statusBarsPadding()
                            .navigationBarsWithImePadding().fillMaxSize()
                    ) {
                        SheetHeader(
                            title = stringResource(id = R.string.select_course),
                            icon = Icons.Outlined.AccountCircle,
                            onClose = {
                                scope.launch {
                                    focusManager.clearFocus()
                                    delay(10)
                                    sheetState.hide()
                                }
                            }
                        )

                        FullName(
                            modifier = Modifier.padding(horizontal = Margin.normal),
                            state = courseState.query,
                            onValueChange = viewModel::searchCourse,
                            placeHolder = stringResource(id = R.string.search_course),
                            focusRequester = courseFocusRequester,
                            imeAction = ImeAction.Search,
                            onImeActionPerformed = { focusManager.clearFocus() }
                        )

                        LazyColumn(
                            contentPadding = PaddingValues(Margin.normal),
                            verticalArrangement = Arrangement.spacedBy(Margin.normal)
                        ) {
                            items(courseState.courseList) { course ->
                                CRListItem(
                                    text = course.courseTitle,
                                    itemClick = {
                                        viewModel.changeCourse(course)
                                        scope.launch {
                                            focusManager.clearFocus()
                                            delay(10)
                                            sheetState.hide()
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
                SectionEditSheet.Instructor -> {
                    Column(
                        modifier = Modifier.statusBarsPadding()
                            .navigationBarsWithImePadding().fillMaxSize()
                    ) {
                        SheetHeader(
                            title = stringResource(id = R.string.select_instructor),
                            icon = Icons.Outlined.AccountCircle,
                            onClose = {
                                scope.launch {
                                    focusManager.clearFocus()
                                    delay(10)
                                    sheetState.hide()
                                }
                            }
                        )

                        FullName(
                            modifier = Modifier.padding(horizontal = Margin.normal),
                            state = teacherState.query,
                            onValueChange = viewModel::searchTeacher,
                            placeHolder = stringResource(id = R.string.search_teacher),
                            focusRequester = instructorFocusRequester,
                            imeAction = ImeAction.Search,
                            onImeActionPerformed = { focusManager.clearFocus() }
                        )

                        LazyColumn(
                            contentPadding = PaddingValues(Margin.normal),
                            verticalArrangement = Arrangement.spacedBy(Margin.normal)
                        ) {
                            items(teacherState.teacherList) { teacher ->
                                TeacherListItem(
                                    teacher = teacher,
                                    itemClick = {
                                        viewModel.changeTeacher(teacher)
                                        scope.launch {
                                            focusManager.clearFocus()
                                            delay(10)
                                            sheetState.hide()
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
                SectionEditSheet.SectionName -> {
                    ListBottomSheet(
                        title = stringResource(id = R.string.select_section_name),
                        icon = Icons.Outlined.SettingsCell,
                        onClose = { sheetState.hide() },
                        list = ('A'..'Z').map { "Section $it" },
                        onItemClick = {
                            viewModel.changeSectionName(it)
                            sheetState.hide()
                        }
                    )
                }
                SectionEditSheet.NoCourse -> {
                    ListBottomSheet(
                        title = stringResource(id = R.string.prohibited),
                        icon = Icons.Outlined.Error,
                        onClose = { sheetState.hide() },
                        list = listOf("You can't change your course."),
                        onItemClick = { sheetState.hide() }
                    )
                }
                SectionEditSheet.NoSectionName -> {
                    ListBottomSheet(
                        title = stringResource(id = R.string.prohibited),
                        icon = Icons.Outlined.Error,
                        onClose = { sheetState.hide() },
                        list = listOf("You can't change your section name."),
                        onItemClick = { sheetState.hide() }
                    )
                }
            }
        }
    ) {
        SectionEditScreenContent(
            stateFlow = viewModel.state,
            selectSectionName = {
                scope.launch {
                    currentSheet =
                        if (viewModel.canChangeCourseOrName()) SectionEditSheet.SectionName
                        else SectionEditSheet.NoSectionName
                    delay(10)
                    sheetState.show()
                }
            },
            selectCourse = {
                scope.launch {
                    currentSheet = if (viewModel.canChangeCourseOrName()) SectionEditSheet.Course
                    else SectionEditSheet.NoCourse
                    delay(10)
                    sheetState.show()
                }
            },
            selectInstructor = {
                scope.launch {
                    currentSheet = SectionEditSheet.Instructor
                    delay(10)
                    sheetState.show()
                }
            },
            changeGoogleCode = viewModel::changeGoogleCode,
            changeBlcCode = viewModel::changeBlcCode,
            changeCourseOutline = viewModel::changeCourseOutline,
            saveSection = viewModel::saveSection,
            onBackPress = onBackPress
        )
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
private fun SectionEditScreenContent(
    stateFlow: StateFlow<SectionEditState>,
    selectSectionName: () -> Unit,
    selectCourse: () -> Unit,
    selectInstructor: () -> Unit,
    changeGoogleCode: (String) -> Unit,
    changeBlcCode: (String) -> Unit,
    changeCourseOutline: (String) -> Unit,
    saveSection: () -> Unit,
    onBackPress: () -> Unit
) {
    Scaffold(
        topBar = {
            BackPressTopAppBar(
                onBackPress = onBackPress,
                title = stringResource(id = R.string.edit_section)
            )
        }
    ) {
        val (googleFocusRequester, blcFocusRequester,
            outlineFocusRequester) = FocusRequester.createRefs()
        val state = stateFlow.collectAsState().value

        ScrollableColumn(
            modifier = Modifier.fillMaxSize().navigationBarsWithImePadding(),
            contentPadding = PaddingValues(Margin.normal),
            verticalArrangement = Arrangement.spacedBy(Margin.tiny)
        ) {
            CRSelection(
                state = state.sectionName,
                placeHolder = stringResource(id = R.string.section_name),
                onClick = selectSectionName
            )

            CRSelection(
                state = state.courseTitle,
                placeHolder = stringResource(id = R.string.course),
                onClick = selectCourse
            )

            CRSelection(
                state = state.instructorName,
                placeHolder = stringResource(id = R.string.instructor),
                onClick = selectInstructor
            )

            FullName(
                state = state.googleCode,
                onValueChange = changeGoogleCode,
                placeHolder = stringResource(id = R.string.google_code),
                focusRequester = googleFocusRequester,
                onImeActionPerformed = {
                    blcFocusRequester.requestFocus()
                }
            )

            FullName(
                state = state.blcCode,
                onValueChange = changeBlcCode,
                placeHolder = stringResource(id = R.string.blc_code),
                focusRequester = blcFocusRequester,
                onImeActionPerformed = {
                    outlineFocusRequester.requestFocus()
                }
            )

            Description(
                state = state.courseOutline,
                onValueChange = changeCourseOutline,
                placeHolder = stringResource(id = R.string.course_outline),
                focusRequester = blcFocusRequester
            )

            LargeButton(text = stringResource(id = R.string.save), onClick = saveSection)
        }
    }
}