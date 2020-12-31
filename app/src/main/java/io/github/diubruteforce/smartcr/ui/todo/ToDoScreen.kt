package io.github.diubruteforce.smartcr.ui.todo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.onActive
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.model.data.PostType
import io.github.diubruteforce.smartcr.ui.common.Empty
import io.github.diubruteforce.smartcr.ui.common.PostCard
import io.github.diubruteforce.smartcr.ui.common.SideEffect
import io.github.diubruteforce.smartcr.ui.theme.Margin
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun ToDoScreen(
    viewModel: ToDoViewModel,
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

    ToDoScreenContent(
        stateFlow = viewModel.state,
        navigateToPostDetail = navigateToPostDetail
    )
}

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
private fun ToDoScreenContent(
    stateFlow: StateFlow<ToDoState>,
    navigateToPostDetail: (PostType, String) -> Unit
) {
    val state = stateFlow.collectAsState().value

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
                        navigateToPostDetail.invoke(post.type, post.postId)
                    }
                )
            }
        }
    } else {
        Empty(
            title = stringResource(id = R.string.no_todo),
            message = stringResource(id = R.string.no_todo_message),
            image = vectorResource(id = R.drawable.no_exam)
        )
    }
}