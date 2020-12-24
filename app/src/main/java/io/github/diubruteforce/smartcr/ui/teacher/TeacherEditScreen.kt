package io.github.diubruteforce.smartcr.ui.teacher

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AmbientFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import dev.chrisbanes.accompanist.insets.navigationBarsWithImePadding
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.model.data.Designation
import io.github.diubruteforce.smartcr.model.data.Gender
import io.github.diubruteforce.smartcr.ui.bottomsheet.ListBottomSheet
import io.github.diubruteforce.smartcr.ui.common.*
import io.github.diubruteforce.smartcr.ui.theme.Margin
import io.github.diubruteforce.smartcr.utils.extension.getMainActivity
import io.github.diubruteforce.smartcr.utils.extension.rememberBackPressAwareBottomSheetState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private sealed class TeacherEditSheet {
    object Gender : TeacherEditSheet()
    object Department : TeacherEditSheet()
    object Designation : TeacherEditSheet()
}

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalMaterialApi::class)
@Composable
fun TeacherEditScreen(
    viewModel: TeacherEditViewModel,
    teacherId: String?,
    onBackPress: (() -> Unit)
) {
    val mainActivity = getMainActivity()
    val sideEffect = viewModel.sideEffect.collectAsState().value
    val state = viewModel.state.collectAsState().value

    var teacherEditSheet by remember { mutableStateOf<TeacherEditSheet>(TeacherEditSheet.Gender) }
    val sheetState = rememberBackPressAwareBottomSheetState()
    val scope = rememberCoroutineScope()

    onActive {
        viewModel.loadInitialData(teacherId)
    }

    SideEffect(
        sideEffectState = sideEffect,
        onSuccess = { if (it == TeacherEditSuccess.ProfileSaved) onBackPress.invoke() },
        onFailAlertDismissRequest = viewModel::clearSideEffect
    )

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            when (teacherEditSheet) {
                TeacherEditSheet.Gender -> {
                    ListBottomSheet(
                        title = stringResource(id = R.string.select_gender),
                        icon = vectorResource(id = R.drawable.gender),
                        onClose = sheetState::hide,
                        list = Gender.values().toList(),
                        onItemClick = {
                            viewModel.changeGender(it)
                            sheetState.hide()
                        }
                    )
                }
                TeacherEditSheet.Department -> {
                    ListBottomSheet(
                        title = stringResource(id = R.string.select_department),
                        icon = Icons.Outlined.Book,
                        onClose = sheetState::hide,
                        list = state.departmentList,
                        onItemClick = {
                            viewModel.changeDepartment(it)
                            sheetState.hide()
                        }
                    )
                }
                TeacherEditSheet.Designation -> {
                    ListBottomSheet(
                        title = stringResource(id = R.string.select_designation),
                        icon = Icons.Outlined.Book,
                        onClose = sheetState::hide,
                        list = Designation.values().toList(),
                        onItemClick = {
                            viewModel.changeDesignation(it)
                            sheetState.hide()
                        }
                    )
                }
            }
        }
    ) {
        TeacherEditScreenContent(
            stateFlow = viewModel.state,
            onFullNameChange = viewModel::changeFullName,
            onInitialChange = viewModel::changeInitial,
            selectGender = {
                scope.launch {
                    teacherEditSheet = TeacherEditSheet.Gender
                    delay(10)
                    sheetState.show()
                }
            },
            onEmailChange = viewModel::changeDiuEmail,
            onPhoneChange = viewModel::changePhoneNumber,
            selectDepartment = {
                scope.launch {
                    teacherEditSheet = TeacherEditSheet.Department
                    delay(10)
                    sheetState.show()
                }
            },
            onRoomChange = viewModel::changeRoom,
            selectDesignation = {
                scope.launch {
                    teacherEditSheet = TeacherEditSheet.Designation
                    delay(10)
                    sheetState.show()
                }
            },
            saveTeacherProfile = viewModel::saveProfile,
            changeImage = { mainActivity.pickImage { viewModel.uploadImage(it) } },
            onBackPress = onBackPress
        )
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
private fun TeacherEditScreenContent(
    stateFlow: StateFlow<TeacherEditState>,
    onFullNameChange: (String) -> Unit,
    onInitialChange: (String) -> Unit,
    selectGender: () -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    selectDepartment: () -> Unit,
    onRoomChange: (String) -> Unit,
    selectDesignation: () -> Unit,
    saveTeacherProfile: () -> Unit,
    changeImage: () -> Unit,
    onBackPress: (() -> Unit)?
) {
    Column(modifier = Modifier.navigationBarsWithImePadding()) {
        val focusManager = AmbientFocusManager.current
        val (nameFocusRequester, initialFocusRequester,
            emailFocusRequester, phoneFocusRequester,
            roomFocusRequester) = FocusRequester.createRefs()
        val state = stateFlow.collectAsState().value

        ProfileTopAppBar(
            title = stringResource(id = R.string.teacher_profile),
            imageUrl = state.profileUrl,
            navigationIcon = {
                onBackPress?.let {
                    IconButton(onClick = it) {
                        Icon(imageVector = Icons.Outlined.KeyboardArrowLeft)
                    }
                }
            },
            imageCaption = {
                TextButton(
                    onClick = changeImage,
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Black)
                ) {
                    Text(
                        text = stringResource(id = R.string.change_image),
                        style = MaterialTheme.typography.body1
                    )
                }
            }
        )

        ScrollableColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(Margin.normal),
            verticalArrangement = Arrangement.spacedBy(Margin.tiny)
        ) {
            Text(
                text = stringResource(id = R.string.teacher_info),
                style = MaterialTheme.typography.h5
            )

            Spacer(modifier = Modifier.size(Margin.small))

            FullName(
                state = state.fullName,
                onValueChange = onFullNameChange,
                focusRequester = nameFocusRequester,
                onImeActionPerformed = { initialFocusRequester.requestFocus() }
            )

            FullName(
                state = state.initial,
                onValueChange = onInitialChange,
                placeHolder = stringResource(id = R.string.teacher_initial),
                focusRequester = initialFocusRequester,
                onImeActionPerformed = {
                    emailFocusRequester.requestFocus()
                }
            )

            DiuEmail(
                state = state.diuEmail,
                onValueChange = onEmailChange,
                focusRequester = emailFocusRequester,
                onImeActionPerformed = {
                    phoneFocusRequester.requestFocus()
                }
            )

            PhoneNumber(
                state = state.phone,
                onValueChange = onPhoneChange,
                focusRequester = phoneFocusRequester,
                onImeActionPerformed = {
                    roomFocusRequester.requestFocus()
                }
            )

            FullName(
                state = state.room,
                onValueChange = onRoomChange,
                placeHolder = stringResource(id = R.string.room),
                focusRequester = roomFocusRequester,
                onImeActionPerformed = {
                    focusManager.clearFocus()
                    selectGender.invoke()
                }
            )

            CRSelection(
                state = state.gender,
                placeHolder = stringResource(id = R.string.gender),
                onClick = {
                    selectGender.invoke()
                    focusManager.clearFocus()
                },
            )

            CRSelection(
                state = state.designation,
                placeHolder = stringResource(id = R.string.designation),
                onClick = {
                    selectDesignation.invoke()
                    focusManager.clearFocus()
                },
            )

            CRSelection(
                state = state.department,
                placeHolder = stringResource(id = R.string.department),
                onClick = {
                    selectDepartment.invoke()
                    focusManager.clearFocus()
                },
            )

            LargeButton(text = stringResource(id = R.string.save), onClick = saveTeacherProfile)
        }
    }
}