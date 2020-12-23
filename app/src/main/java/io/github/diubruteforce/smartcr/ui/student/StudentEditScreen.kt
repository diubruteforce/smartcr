package io.github.diubruteforce.smartcr.ui.student

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DepartureBoard
import androidx.compose.material.icons.outlined.Error
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
import io.github.diubruteforce.smartcr.R.string.select_your_level
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

private sealed class StudentEditSheet {
    object Email : StudentEditSheet()
    object Gender : StudentEditSheet()
    object Department : StudentEditSheet()
    object Level : StudentEditSheet()
    object Term : StudentEditSheet()
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalCoroutinesApi::class)
@Composable
fun StudentEditScreen(
    viewModel: StudentEditViewModel,
    onBackPress: (() -> Unit)?,
    onNavigateToHome: () -> Unit
) {
    val sheetState = rememberBackPressAwareBottomSheetState()
    val scope = rememberCoroutineScope()

    var studentEditSheet by remember { mutableStateOf<StudentEditSheet>(StudentEditSheet.Gender) }
    val sideEffectState = viewModel.sideEffect.collectAsState().value
    val state = viewModel.state.collectAsState().value

    val mainActivity = getMainActivity()

    SideEffect(
        sideEffectState = sideEffectState,
        onSuccess = {
            when (it) {
                StudentEditSuccess.Loaded -> {

                }
                StudentEditSuccess.ImageSaved -> {

                }
                StudentEditSuccess.ProfileSaved -> {
                    onNavigateToHome.invoke()
                }
            }
        },
        onFailAlertDismissRequest = viewModel::clearSideEffect
    )

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            when (studentEditSheet) {
                StudentEditSheet.Gender -> {
                    ListBottomSheet(
                        title = stringResource(id = R.string.select_your_gender),
                        icon = vectorResource(id = R.drawable.gender),
                        onClose = { sheetState.hide() },
                        list = Gender.values().toList(),
                        onItemClick = {
                            sheetState.hide()
                            viewModel.changeGender(it)
                        }
                    )
                }
                StudentEditSheet.Email -> {
                    ListBottomSheet(
                        title = stringResource(id = R.string.prohibited),
                        icon = Icons.Outlined.Error,
                        onClose = { sheetState.hide() },
                        list = listOf("You can't change email."),
                        onItemClick = { sheetState.hide() }
                    )
                }
                StudentEditSheet.Department -> {
                    ListBottomSheet(
                        title = stringResource(id = R.string.select_your_department),
                        icon = Icons.Outlined.DepartureBoard,
                        onClose = { sheetState.hide() },
                        list = state.departmentList,
                        onItemClick = {
                            sheetState.hide()
                            viewModel.changeDepartment(it)
                        }
                    )
                }
                StudentEditSheet.Level -> {
                    ListBottomSheet(
                        title = stringResource(id = select_your_level),
                        icon = Icons.Outlined.DepartureBoard,
                        onClose = { sheetState.hide() },
                        list = (1..4).toList(),
                        onItemClick = {
                            sheetState.hide()
                            viewModel.changeLevel(it)
                        }
                    )
                }
                StudentEditSheet.Term -> {
                    ListBottomSheet(
                        title = stringResource(id = R.string.select_your_term),
                        icon = Icons.Outlined.DepartureBoard,
                        onClose = { sheetState.hide() },
                        list = (1..3).toList(),
                        onItemClick = {
                            sheetState.hide()
                            viewModel.changeTerm(it)
                        }
                    )
                }
            }
        }
    ) {
        StudentEditScreenContent(
            stateFlow = viewModel.state,
            onFullNameChange = viewModel::changeFullName,
            onDiuIdChange = viewModel::changeDiuId,
            selectGender = {
                scope.launch {
                    studentEditSheet = StudentEditSheet.Gender
                    delay(10)
                    sheetState.show()
                }
            },
            onEmailClick = {
                scope.launch {
                    studentEditSheet = StudentEditSheet.Email
                    delay(10)
                    sheetState.show()
                }
            },
            onPhoneChange = viewModel::changePhoneNumber,
            selectDepartment = {
                scope.launch {
                    studentEditSheet = StudentEditSheet.Department
                    delay(10)
                    sheetState.show()
                }
            },
            selectLevel = {
                scope.launch {
                    studentEditSheet = StudentEditSheet.Level
                    delay(10)
                    sheetState.show()
                }
            },
            selectTerm = {
                scope.launch {
                    studentEditSheet = StudentEditSheet.Term
                    delay(10)
                    sheetState.show()
                }
            },
            changeImage = {
                mainActivity.pickImage { viewModel.uploadImage(it) }
            },
            saveStudentProfile = viewModel::saveProfile,
            onBackPress = onBackPress
        )
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
private fun StudentEditScreenContent(
    stateFlow: StateFlow<StudentEditState>,
    onFullNameChange: (String) -> Unit,
    onDiuIdChange: (String) -> Unit,
    selectGender: () -> Unit,
    onEmailClick: () -> Unit,
    onPhoneChange: (String) -> Unit,
    selectDepartment: () -> Unit,
    selectLevel: () -> Unit,
    selectTerm: () -> Unit,
    changeImage: () -> Unit,
    saveStudentProfile: () -> Unit,
    onBackPress: (() -> Unit)?
) {
    Column(modifier = Modifier.navigationBarsWithImePadding()) {
        val focusManager = AmbientFocusManager.current
        val (nameFocusRequester, idFocusRequester, phoneFocusRequester) = FocusRequester.createRefs()
        val state = stateFlow.collectAsState().value

        ProfileTopAppBar(
            title = stringResource(id = R.string.student_profile),
            imageUrl = state.imageUrl,
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
                text = stringResource(id = R.string.personal_info),
                style = MaterialTheme.typography.h5
            )

            Spacer(modifier = Modifier.size(Margin.small))

            FullName(
                state = state.fullName,
                onValueChange = onFullNameChange,
                focusRequester = nameFocusRequester,
                onImeActionPerformed = { idFocusRequester.requestFocus() }
            )

            DiuId(
                state = state.diuId,
                onValueChange = onDiuIdChange,
                focusRequester = idFocusRequester,
                onImeActionPerformed = {
                    phoneFocusRequester.requestFocus()
                }
            )

            CRSelection(
                state = state.diuEmail,
                placeHolder = stringResource(id = R.string.diu_email),
                icon = null,
                onClick = onEmailClick,
            )

            PhoneNumber(
                state = state.phoneNumber,
                onValueChange = onPhoneChange,
                focusRequester = phoneFocusRequester,
                onImeActionPerformed = {
                    focusManager.clearFocus()
                    selectGender.invoke()
                }
            )

            CRSelection(
                state = state.gender,
                placeHolder = stringResource(id = R.string.gender),
                onClick = selectGender,
            )

            Spacer(modifier = Modifier.size(Margin.normal))

            Text(
                text = stringResource(id = R.string.academic_info),
                style = MaterialTheme.typography.h5
            )

            Spacer(modifier = Modifier.size(Margin.small))

            CRSelection(
                state = state.department,
                placeHolder = stringResource(id = R.string.department),
                onClick = selectDepartment,
            )

            CRSelection(
                state = state.level,
                placeHolder = stringResource(id = R.string.level),
                onClick = { selectLevel.invoke() },
            )

            CRSelection(
                state = state.term,
                placeHolder = stringResource(id = R.string.term),
                onClick = { selectTerm.invoke() },
            )

            LargeButton(text = stringResource(id = R.string.save), onClick = saveStudentProfile)
        }
    }
}