package io.github.diubruteforce.smartcr.ui.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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

    LaunchedEffect(true) {
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
                        icon = painterResource(id = R.drawable.gender),
                        onClose = { scope.launch { sheetState.hide() } },
                        list = Gender.values().toList(),
                        onItemClick = {
                            scope.launch {
                                viewModel.changeGender(it)
                                sheetState.hide()
                            }
                        }
                    )
                }
                TeacherEditSheet.Department -> {
                    ListBottomSheet(
                        title = stringResource(id = R.string.select_department),
                        imageVector = Icons.Outlined.Book,
                        onClose = { scope.launch { sheetState.hide() } },
                        list = state.departmentList,
                        onItemClick = {
                            scope.launch {
                                viewModel.changeDepartment(it)
                                sheetState.hide()
                            }
                        }
                    )
                }
                TeacherEditSheet.Designation -> {
                    ListBottomSheet(
                        title = stringResource(id = R.string.select_designation),
                        imageVector = Icons.Outlined.Book,
                        onClose = { scope.launch { sheetState.hide() } },
                        list = Designation.values().toList(),
                        onItemClick = {
                            scope.launch {
                                viewModel.changeDesignation(it)
                                sheetState.hide()
                            }
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

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalComposeUiApi::class)
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
        val focusManager = LocalFocusManager.current
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
                        Icon(
                            imageVector = Icons.Outlined.KeyboardArrowLeft,
                            contentDescription = "Back"
                        )
                    }
                }
            },
            actions = { TextButton(onClick = {}) { Text(text = "") } },
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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentPadding = PaddingValues(Margin.normal),
            verticalArrangement = Arrangement.spacedBy(Margin.tiny)
        ) {
            item {
                Text(
                    text = stringResource(id = R.string.teacher_info),
                    style = MaterialTheme.typography.h5
                )
            }

            item {
                Spacer(modifier = Modifier.size(Margin.small))
            }

            item {
                FullName(
                    state = state.fullName,
                    onValueChange = onFullNameChange,
                    focusRequester = nameFocusRequester,
                    onImeActionPerformed = { initialFocusRequester.requestFocus() }
                )
            }

            item {
                FullName(
                    state = state.initial,
                    onValueChange = onInitialChange,
                    placeHolder = stringResource(id = R.string.teacher_initial),
                    focusRequester = initialFocusRequester,
                    onImeActionPerformed = {
                        emailFocusRequester.requestFocus()
                    }
                )
            }

            item {
                DiuEmail(
                    state = state.diuEmail,
                    onValueChange = onEmailChange,
                    focusRequester = emailFocusRequester,
                    onImeActionPerformed = {
                        phoneFocusRequester.requestFocus()
                    }
                )
            }

            item {
                PhoneNumber(
                    state = state.phone,
                    onValueChange = onPhoneChange,
                    focusRequester = phoneFocusRequester,
                    onImeActionPerformed = {
                        roomFocusRequester.requestFocus()
                    }
                )
            }

            item {
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
            }

            item {
                CRSelection(
                    state = state.gender,
                    placeHolder = stringResource(id = R.string.gender),
                    onClick = {
                        selectGender.invoke()
                        focusManager.clearFocus()
                    },
                )
            }

            item {
                CRSelection(
                    state = state.designation,
                    placeHolder = stringResource(id = R.string.designation),
                    onClick = {
                        selectDesignation.invoke()
                        focusManager.clearFocus()
                    },
                )
            }

            item {
                CRSelection(
                    state = state.department,
                    placeHolder = stringResource(id = R.string.department),
                    onClick = {
                        selectDepartment.invoke()
                        focusManager.clearFocus()
                    },
                )
            }

            item {
                LargeButton(text = stringResource(id = R.string.save), onClick = saveTeacherProfile)
            }
        }
    }
}