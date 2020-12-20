package io.github.diubruteforce.smartcr.ui.profile.student

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AmbientFocusManager
import androidx.compose.ui.res.stringResource
import dev.chrisbanes.accompanist.insets.navigationBarsWithImePadding
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.ui.common.*
import io.github.diubruteforce.smartcr.ui.theme.Margin
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

@Composable
fun StudentEditScreen(
    viewModel: StudentEditViewModel,
    onBackPress: (() -> Unit)?,
    onNavigateToHome: () -> Unit
) {
    StudentEditScreenContent(
        stateFlow = viewModel.state,
        onFullNameChange = viewModel::changeFullName,
        onDiuIdChange = viewModel::changeDiuId,
        selectGender = { /*TODO*/ },
        onEmailClick = { /*TODO*/ },
        onPhoneChange = viewModel::changePhoneNumber,
        selectDepartment = { /*TODO*/ },
        selectLevel = { /*TODO*/ },
        selectTerm = { /*TODO*/ },
        changeImage = {},
        saveStudentProfile = {},
        onBackPress = onBackPress
    )
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
    selectLevel: (Int?) -> Unit,
    selectTerm: (Int?) -> Unit,
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
            imageUrl = state.imageUrl ?: "",
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
            contentPadding = PaddingValues(Margin.big),
            verticalArrangement = Arrangement.spacedBy(Margin.normal)
        ) {
            Text(
                text = stringResource(id = R.string.personal_info),
                style = MaterialTheme.typography.h5
            )

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
                text = state.diuEmail,
                placeHolder = stringResource(id = R.string.enter_diu_email),
                icon = null,
                onClick = onEmailClick,
            )

            Spacer(modifier = Modifier.size(Margin.small))

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
                text = state.gender,
                placeHolder = stringResource(id = R.string.select_your_gender),
                onClick = selectGender,
            )

            Spacer(modifier = Modifier.size(Margin.normal))

            Text(
                text = stringResource(id = R.string.academic_info),
                style = MaterialTheme.typography.h5
            )

            CRSelection(
                text = state.department,
                placeHolder = stringResource(id = R.string.select_your_department),
                onClick = selectDepartment,
            )

            CRSelection(
                text = state.level?.toString(),
                placeHolder = stringResource(id = R.string.select_your_level),
                onClick = { selectLevel.invoke(state.level) },
            )

            CRSelection(
                text = state.term?.toString(),
                placeHolder = stringResource(id = R.string.select_your_term),
                onClick = { selectTerm.invoke(state.term) },
            )

            LargeButton(text = stringResource(id = R.string.save), onClick = saveStudentProfile)
        }
    }
}