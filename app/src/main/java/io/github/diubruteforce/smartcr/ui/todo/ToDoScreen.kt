package io.github.diubruteforce.smartcr.ui.todo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.onActive
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import dev.chrisbanes.accompanist.insets.navigationBarsPadding
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
                        navigateToPostDetail.invoke(post.type, post.postId)
                    }
                )
            }

            item { Spacer(modifier = Modifier.height(Margin.inset)) }
        }
    } else {
        Empty(
            title = stringResource(id = R.string.no_todo),
            message = stringResource(id = R.string.no_todo_message),
            image = vectorResource(id = R.drawable.no_exam)
        )
    }
}