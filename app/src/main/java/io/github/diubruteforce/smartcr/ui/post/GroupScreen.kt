package io.github.diubruteforce.smartcr.ui.post

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.chrisbanes.accompanist.insets.navigationBarsPadding
import dev.chrisbanes.accompanist.insets.navigationBarsWithImePadding
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.model.data.Group
import io.github.diubruteforce.smartcr.ui.common.*
import io.github.diubruteforce.smartcr.ui.theme.Margin
import io.github.diubruteforce.smartcr.ui.theme.grayText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun GroupScreen(
    viewModel: GroupViewModel,
    postId: String,
    sectionId: String,
    onBackPress: () -> Unit
) {
    val sideEffect = viewModel.sideEffect.collectAsState().value

    LaunchedEffect(true) {
        viewModel.loadData(postId = postId, sectionId = sectionId)
    }

    SideEffect(
        sideEffectState = sideEffect,
        onSuccess = {
            if (it == GroupSuccess.GroupSaved) viewModel.loadData(
                postId = postId,
                sectionId = sectionId
            )
        },
        onFailAlertDismissRequest = viewModel::clearSideEffect
    )

    GroupScreenContent(
        stateFlow = viewModel.state,
        joinGroup = viewModel::joinGroup,
        leaveGroup = viewModel::leaveGroup,
        startEditing = viewModel::startEditing,
        onNameChange = viewModel::changeGroupName,
        onDetailChange = viewModel::changeGroupDetails,
        savePost = viewModel::saveGroup,
        onBackPress = onBackPress
    )
}

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
private fun GroupScreenContent(
    stateFlow: StateFlow<GroupState>,
    joinGroup: (String) -> Unit,
    leaveGroup: (String) -> Unit,
    startEditing: (Group) -> Unit,
    onNameChange: (String) -> Unit,
    onDetailChange: (String) -> Unit,
    savePost: () -> Unit,
    onBackPress: () -> Unit
) {
    val state = stateFlow.collectAsState().value
    val nameFocusRequester = FocusRequester()
    val detailFocusRequester = FocusRequester()

    Scaffold(
        topBar = {
            BackPressTopAppBar(
                onBackPress = onBackPress,
                title = stringResource(id = R.string.groups)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.navigationBarsPadding(),
                onClick = { startEditing.invoke(Group()) },
                backgroundColor = MaterialTheme.colors.primary
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "SmartCR"
                )
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsWithImePadding(),
            contentPadding = PaddingValues(Margin.normal),
            verticalArrangement = Arrangement.spacedBy(Margin.normal)
        ) {
            if (state.editingGroup != null) {
                GroupEdit(
                    state = state,
                    onNameChange = onNameChange,
                    onDetailChange = onDetailChange,
                    nameFocusRequester = nameFocusRequester,
                    detailFocusRequester = detailFocusRequester,
                    savePost = savePost
                )
            } else {
                if (state.groups.isEmpty()) {
                    EmptyGroup(startEditing = startEditing)
                } else {
                    GroupList(
                        state = state,
                        joinGroup = joinGroup,
                        leaveGroup = leaveGroup,
                        startEditing = startEditing
                    )
                }
            }
        }
    }
}

private fun LazyListScope.GroupList(
    state: GroupState,
    joinGroup: (String) -> Unit,
    leaveGroup: (String) -> Unit,
    startEditing: (Group) -> Unit,
) {
    state.groups.forEach { group ->
        item {
            Row {
                Text(
                    modifier = Modifier.weight(1f),
                    text = group.name,
                    style = MaterialTheme.typography.h5,
                    color = MaterialTheme.colors.grayText,
                    textAlign = TextAlign.Start
                )

                if (group.id == state.joinedGroupId) {
                    Button(
                        onClick = { leaveGroup.invoke(group.id) },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error)
                    ) {
                        Text(text = stringResource(id = R.string.leave))
                    }
                } else {
                    Button(
                        onClick = { joinGroup.invoke(group.id) },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
                    ) {
                        Text(text = stringResource(id = R.string.join))
                    }
                }
            }
        }

        item {
            TitleRow(
                title = stringResource(id = R.string.detail),
                onEdit = { startEditing.invoke(group) }
            )
        }

        item {
            Text(text = group.detail)
        }

        state.groupMembers[group.id]?.forEach { member ->
            item {
                ProfileListItem(
                    title = member.fullName,
                    subTitle = "Student ID: ${member.diuId}",
                    profileUrl = member.profileUrl,
                    itemClick = { }
                )
            }
        }

        item {
            Divider()
            Spacer(modifier = Modifier.size(Margin.normal))
        }
    }
}

private fun LazyListScope.EmptyGroup(
    startEditing: (Group) -> Unit
) {
    item {
        Empty(
            modifier = Modifier.sizeIn(minHeight = 600.dp),
            title = "No Group Available",
            message = "Currently there is no group. To get started create a new group",
            image = painterResource(id = R.drawable.no_exam),
            actionTitle = "Create a group",
            onAction = { startEditing.invoke(Group()) }
        )
    }
}

private fun LazyListScope.GroupEdit(
    state: GroupState,
    onNameChange: (String) -> Unit,
    onDetailChange: (String) -> Unit,
    nameFocusRequester: FocusRequester,
    detailFocusRequester: FocusRequester,
    savePost: () -> Unit,
) {
    item {
        FullName(
            state = state.groupName,
            onValueChange = onNameChange,
            placeHolder = stringResource(id = R.string.group_name),
            focusRequester = nameFocusRequester
        )

        Description(
            state = state.groupDetails,
            onValueChange = onDetailChange,
            placeHolder = stringResource(id = R.string.detail),
            focusRequester = detailFocusRequester
        )

        LargeButton(text = stringResource(id = R.string.save), onClick = savePost)
    }
}