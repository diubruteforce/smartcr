package io.github.diubruteforce.smartcr.ui.smartcr.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.onActive
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.model.data.PostType
import io.github.diubruteforce.smartcr.ui.common.Empty
import io.github.diubruteforce.smartcr.ui.common.PostCard
import io.github.diubruteforce.smartcr.ui.common.SideEffect
import io.github.diubruteforce.smartcr.ui.theme.Margin
import io.github.diubruteforce.smartcr.ui.theme.grayText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navigateToSectionDetail: (String) -> Unit,
    navigateToPostDetail: (PostType, String) -> Unit
) {
    val sideEffect = viewModel.sideEffect.collectAsState().value

    onActive {
        viewModel.loadData()
    }

    SideEffect(
        sideEffectState = sideEffect,
        onSuccess = {},
        onFailAlertDismissRequest = viewModel::clearSideEffect
    )

    HomeScreenContent(
        stateFlow = viewModel.state,
        navigateToSectionDetail = navigateToSectionDetail,
        navigateToPostDetail = navigateToPostDetail,
        onNextDay = viewModel::nextDay,
        onPreviousDay = viewModel::previousDay
    )
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
    stateFlow: StateFlow<HomeState>,
    navigateToSectionDetail: (String) -> Unit,
    navigateToPostDetail: (PostType, String) -> Unit,
    onNextDay: () -> Unit,
    onPreviousDay: () -> Unit
) {
    val state = stateFlow.collectAsState().value

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        if (state.hasJoinedSection) {
            Row(
                modifier = Modifier.padding(Margin.normal),
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
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(Margin.normal),
                    contentPadding = PaddingValues(Margin.normal)
                ) {
                    items(state.posts) { post ->
                        PostCard(
                            title = post.title,
                            firstRow = post.firstRow,
                            secondRow = post.secondRow,
                            color = MaterialTheme.colors.primary,
                            onItemClick = {
                                if (post.type == PostType.Routine) {
                                    navigateToSectionDetail.invoke(post.sectionId)
                                } else {
                                    navigateToPostDetail.invoke(post.type, post.postId)
                                }
                            }
                        )
                    }
                }
            } else {
                Empty(
                    title = stringResource(id = R.string.no_class_today),
                    message = stringResource(id = R.string.no_class_today_message),
                    image = vectorResource(id = R.drawable.new_class)
                )
            }
        } else {
            Empty(
                title = stringResource(id = R.string.new_semester),
                message = stringResource(id = R.string.join_section),
                image = vectorResource(id = R.drawable.new_semseter)
            )
        }
    }
}