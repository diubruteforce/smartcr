package io.github.diubruteforce.smartcr.ui.student

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.chrisbanes.accompanist.insets.navigationBarsWithImePadding
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.ui.common.*
import io.github.diubruteforce.smartcr.ui.theme.Margin
import io.github.diubruteforce.smartcr.ui.theme.grayText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun StudentDetailScreen(
    viewModel: StudentDetailViewModel,
    navigateToOnBoarding: () -> Unit,
    navigateToProfileEdit: () -> Unit,
    navigateToSectionDetail: (String) -> Unit,
    onBackPress: () -> Unit,
) {
    val sideEffect = viewModel.sideEffect.collectAsState().value
    var deleteProfile by remember { mutableStateOf(false) }

    onActive {
        viewModel.loadData()
    }

    SideEffect(
        sideEffectState = sideEffect,
        onSuccess = {
            if (it == StudentDetailSuccess.Deleted || it == StudentDetailSuccess.SignOut)
                navigateToOnBoarding.invoke()
        },
        onFailAlertDismissRequest = viewModel::clearSideEffect
    )

    if (deleteProfile) {
        CRAlertDialog(
            title = stringResource(id = R.string.are_you_sure),
            message = stringResource(id = R.string.profile_delete),
            onDenial = {
                viewModel.deleteProfile()
                deleteProfile = false
            },
            denialText = stringResource(id = R.string.delete),
            onAffirmation = { deleteProfile = false },
            affirmationText = stringResource(id = R.string.cancel),
            onDismissRequest = { deleteProfile = false }
        )
    }

    StudentDetailScreenContent(
        stateFlow = viewModel.state,
        navigateToSectionDetail = navigateToSectionDetail,
        joinSection = viewModel::joinSection,
        leaveSection = viewModel::leaveSection,
        onProfileEdit = { navigateToProfileEdit.invoke() },
        onProfileDelete = { deleteProfile = true },
        signOut = viewModel::signOut,
        onBackPress = onBackPress
    )

}

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
private fun StudentDetailScreenContent(
    stateFlow: StateFlow<StudentDetailState>,
    navigateToSectionDetail: (String) -> Unit,
    joinSection: (String, String) -> Unit,
    leaveSection: (String, String) -> Unit,
    onProfileEdit: (String) -> Unit,
    onProfileDelete: (String) -> Unit,
    signOut: () -> Unit,
    onBackPress: () -> Unit
) {
    val state = stateFlow.collectAsState().value

    Scaffold(
        topBar = {
            ProfileTopAppBar(
                title = stringResource(id = R.string.student_profile),
                navigationIcon = {
                    IconButton(onClick = onBackPress) {
                        Icon(imageVector = Icons.Outlined.KeyboardArrowLeft)
                    }
                },
                actions = { IconButton(onClick = { }) {} },
                imageUrl = state.student.profileUrl,
                imageCaption = {
                    Text(text = state.student.fullName)
                }
            )
        }
    ) {
        ScrollableColumn(
            modifier = Modifier.navigationBarsWithImePadding(),
            contentPadding = PaddingValues(Margin.normal),
            verticalArrangement = Arrangement.spacedBy(Margin.normal),
        ) {

            StudentProfileCard(
                student = state.student,
                onEdit = onProfileEdit,
                onDelete = onProfileDelete
            )

            Spacer(modifier = Modifier.size(Margin.medium))

            Text(
                text = stringResource(id = R.string.joined_sections),
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.grayText
            )

            state.joinedSections.forEach {
                SectionListItem(
                    state = it,
                    itemClick = { navigateToSectionDetail.invoke(it.sectionId) },
                    onJoin = { joinSection.invoke(it.sectionId, it.courseCode) },
                    onLeave = { leaveSection.invoke(it.sectionId, it.courseCode) }
                )
            }

            Spacer(modifier = Modifier.size(Margin.medium))

            LargeButton(text = stringResource(id = R.string.sign_out), onClick = signOut)

            Spacer(modifier = Modifier.size(Margin.medium))
        }
    }
}
