package io.github.diubruteforce.smartcr.ui.smartcr.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.PostAdd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.onActive
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import dev.chrisbanes.accompanist.insets.navigationBarsPadding
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.model.data.PostType
import io.github.diubruteforce.smartcr.model.ui.StringFailSideEffectState
import io.github.diubruteforce.smartcr.model.ui.TypedSideEffectState
import io.github.diubruteforce.smartcr.ui.bottomsheet.ListBottomSheet
import io.github.diubruteforce.smartcr.ui.common.Empty
import io.github.diubruteforce.smartcr.ui.common.PostCard
import io.github.diubruteforce.smartcr.ui.common.SideEffect
import io.github.diubruteforce.smartcr.ui.theme.Margin
import io.github.diubruteforce.smartcr.ui.theme.grayText
import io.github.diubruteforce.smartcr.utils.extension.rememberBackPressAwareBottomSheetState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navigateToSectionDetail: (String) -> Unit,
    navigateToPostDetail: (PostType, String) -> Unit,
    navigateToPostEdit: (PostType) -> Unit,
    navigateToCourseList: () -> Unit
) {
    val sheetState = rememberBackPressAwareBottomSheetState()
    val sideEffect = viewModel.sideEffect.collectAsState().value

    onActive {
        viewModel.loadData()
    }

    SideEffect(
        sideEffectState = sideEffect,
        onSuccess = {},
        onFailAlertDismissRequest = viewModel::clearSideEffect
    )

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            ListBottomSheet(
                title = stringResource(id = R.string.what_to_create),
                icon = Icons.Outlined.PostAdd,
                onClose = sheetState::hide,
                list = PostType.values().toList().dropLast(1),
                onItemClick = {
                    sheetState.hide {
                        navigateToPostEdit.invoke(it)
                    }
                }
            )
        }
    ) {
        HomeScreenContent(
            sideEffectState = sideEffect,
            stateFlow = viewModel.state,
            navigateToSectionDetail = navigateToSectionDetail,
            navigateToPostDetail = navigateToPostDetail,
            navigateToCourseList = navigateToCourseList,
            onNextDay = viewModel::nextDay,
            createNewPost = sheetState::show,
            onPreviousDay = viewModel::previousDay
        )
    }
}

/*
* Plus Button Option
* 1. Add Extra Class
* 2. Dismiss Class
* 3. Add New Quiz
* 4. Add New Assignment
* 5. Add new Presentation
* 6. Add new Project
* 7. Join a Section
* */

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
private fun HomeScreenContent(
    sideEffectState: StringFailSideEffectState,
    stateFlow: StateFlow<HomeState>,
    navigateToSectionDetail: (String) -> Unit,
    navigateToPostDetail: (PostType, String) -> Unit,
    navigateToCourseList: () -> Unit,
    createNewPost: () -> Unit,
    onNextDay: () -> Unit,
    onPreviousDay: () -> Unit
) {
    val state = stateFlow.collectAsState().value

    Scaffold(
        floatingActionButton = {
            if (state.hasJoinedSection) {
                FloatingActionButton(
                    modifier = Modifier.padding(bottom = Margin.inset),
                    backgroundColor = MaterialTheme.colors.primary,
                    onClick = createNewPost
                ) {
                    Icon(imageVector = Icons.Outlined.Add)
                }
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            if (state.hasJoinedSection) {
                Row(
                    modifier = Modifier.padding(Margin.small),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onPreviousDay) {
                        Icon(
                            imageVector = Icons.Outlined.KeyboardArrowLeft,
                            tint = MaterialTheme.colors.primary
                        )
                    }

                    Text(
                        modifier = Modifier.weight(1f),
                        text = state.currentDate,
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.W400,
                        color = MaterialTheme.colors.grayText,
                        textAlign = TextAlign.Center
                    )

                    IconButton(onClick = onNextDay) {
                        Icon(
                            imageVector = Icons.Outlined.KeyboardArrowRight,
                            tint = MaterialTheme.colors.primary
                        )
                    }
                }

                if (state.posts.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .navigationBarsPadding(),
                        verticalArrangement = Arrangement.spacedBy(Margin.normal),
                        contentPadding = PaddingValues(Margin.normal)
                    ) {
                        items(state.posts) { post ->
                            PostCard(
                                title = post.title,
                                firstRow = post.firstRow,
                                secondRow = post.secondRow,
                                color = post.type.color,
                                onItemClick = {
                                    if (post.type == PostType.Routine) {
                                        navigateToSectionDetail.invoke(post.sectionId)
                                    } else {
                                        navigateToPostDetail.invoke(post.type, post.postId)
                                    }
                                }
                            )
                        }

                        item { Spacer(modifier = Modifier.height(Margin.inset)) }
                    }
                } else if (sideEffectState is TypedSideEffectState.Success) {
                    Empty(
                        title = stringResource(id = R.string.no_class_today),
                        message = stringResource(id = R.string.no_class_today_message),
                        image = vectorResource(id = R.drawable.new_class)
                    )
                }
            } else if (sideEffectState is TypedSideEffectState.Success) {
                Empty(
                    title = stringResource(id = R.string.new_semester),
                    message = stringResource(id = R.string.new_semester_message),
                    image = vectorResource(id = R.drawable.new_semseter),
                    actionTitle = stringResource(id = R.string.join_section),
                    onAction = navigateToCourseList
                )
            }
        }
    }
}