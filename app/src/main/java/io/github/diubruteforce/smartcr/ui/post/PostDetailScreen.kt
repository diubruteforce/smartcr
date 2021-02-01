package io.github.diubruteforce.smartcr.ui.post

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import dev.chrisbanes.accompanist.insets.navigationBarsPadding
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.model.data.*
import io.github.diubruteforce.smartcr.ui.common.*
import io.github.diubruteforce.smartcr.ui.theme.Margin
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun PostDetailScreen(
    viewModel: PostDetailViewModel,
    postType: PostType,
    postId: String,
    navigateToGroupList: (String, String) -> Unit,
    navigateToPostEdit: (PostType, String) -> Unit,
    onBackPress: () -> Unit
) {
    val sideEffect = viewModel.sideEffect.collectAsState().value
    var deletePost by remember { mutableStateOf(false) }

    onActive {
        viewModel.loadData(postType, postId)
    }

    SideEffect(
        sideEffectState = sideEffect,
        onSuccess = { if (it == PostDetailSuccess.Deleted) onBackPress.invoke() },
        onFailAlertDismissRequest = viewModel::clearSideEffect
    )

    if (deletePost) {
        CRAlertDialog(
            title = stringResource(id = R.string.are_you_sure),
            message = stringResource(id = R.string.post_delete),
            onDenial = {
                viewModel.deletePost()
                deletePost = false
            },
            denialText = stringResource(id = R.string.delete),
            onAffirmation = { deletePost = false },
            affirmationText = stringResource(id = R.string.cancel),
            onDismissRequest = { deletePost = false }
        )
    }

    PostDetailScreenContent(
        stateFlow = viewModel.state,
        postType = postType,
        navigateToGroupList = { sectionId -> navigateToGroupList.invoke(postId, sectionId) },
        navigateToPostEdit = { navigateToPostEdit.invoke(postType, postId) },
        deletePost = { deletePost = true },
        onBackPress = onBackPress
    )
}

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
private fun PostDetailScreenContent(
    stateFlow: StateFlow<PostDetailState>,
    postType: PostType,
    navigateToGroupList: (String) -> Unit,
    navigateToPostEdit: () -> Unit,
    deletePost: () -> Unit,
    onBackPress: () -> Unit
) {
    val state = stateFlow.collectAsState().value
    val title = if (state.post == null) "${postType.name} Details"
    else "${state.post.number} : ${state.post.courseCode}"

    Scaffold(
        topBar = {
            InsetAwareTopAppBar {
                IconButton(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    onClick = onBackPress
                ) {
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowLeft,
                        contentDescription = "Back"
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = title,
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.primary
                )

                Spacer(modifier = Modifier.weight(1f))

                UpdateDeleteMenu(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    icon = Icons.Outlined.MoreVert,
                    onEdit = navigateToPostEdit,
                    onDelete = deletePost
                )
            }
        }
    ) {
        ScrollableColumn(
            modifier = Modifier.navigationBarsPadding(),
            contentPadding = PaddingValues(Margin.normal),
            verticalArrangement = Arrangement.spacedBy(Margin.tiny)
        ) {
            state.post?.let { post ->
                Spacer(modifier = Modifier.size(Margin.normal))

                LabelText(label = stringResource(id = R.string.course), text = post.courseName)
                LabelText(label = stringResource(id = R.string.section), text = post.sectionName)
                LabelText(label = stringResource(id = R.string.place), text = post.place)
                LabelText(label = stringResource(id = R.string.submission_date), text = post.date)
                LabelText(label = stringResource(id = R.string.submission_time), text = post.time)

                when (post) {
                    is Quiz -> {
                        TitleRow(
                            title = stringResource(id = R.string.syllabus),
                            onEdit = navigateToPostEdit
                        )

                        Text(text = post.syllabus)
                    }
                    is Assignment -> if (post.groupType == GroupType.Group.name) {
                        GroupInformation(
                            navigateToGroupList = { navigateToGroupList.invoke(post.sectionId) },
                            maxMember = post.maxMember,
                            joinedGroup = state.joinedGroup
                        )
                    }
                    is Presentation -> if (post.groupType == GroupType.Group.name) {
                        GroupInformation(
                            navigateToGroupList = { navigateToGroupList.invoke(post.sectionId) },
                            maxMember = post.maxMember,
                            joinedGroup = state.joinedGroup
                        )
                    }
                    is Project -> if (post.groupType == GroupType.Group.name) {
                        GroupInformation(
                            navigateToGroupList = { navigateToGroupList.invoke(post.sectionId) },
                            maxMember = post.maxMember,
                            joinedGroup = state.joinedGroup
                        )
                    }
                }

                TitleRow(
                    title = stringResource(id = R.string.detail),
                    onEdit = navigateToPostEdit
                )

                Text(text = post.details)
            }
        }
    }
}

@Composable
private fun ColumnScope.GroupInformation(
    navigateToGroupList: () -> Unit,
    maxMember: Int,
    joinedGroup: Group?
) {
    TitleRow(
        title = stringResource(id = R.string.group),
        onEdit = navigateToGroupList
    )

    LabelText(
        label = stringResource(id = R.string.max_member),
        text = maxMember.toString()
    )
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${stringResource(id = R.string.your_group)}: ",
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )

        if (joinedGroup != null) {
            Text(text = joinedGroup.name, maxLines = 1)
        } else {
            Text(
                text = stringResource(id = R.string.you_have_not),
                maxLines = 1,
                color = MaterialTheme.colors.error
            )
        }
    }

    Spacer(modifier = Modifier.size(Margin.normal))

    OutlinedButton(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .align(Alignment.CenterHorizontally),
        onClick = navigateToGroupList
    ) {
        Text(text = stringResource(id = R.string.join_or_creat_group))
    }
}