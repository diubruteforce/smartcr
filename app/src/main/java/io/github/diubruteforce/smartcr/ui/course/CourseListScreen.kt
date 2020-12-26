package io.github.diubruteforce.smartcr.ui.course

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.onActive
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.chrisbanes.accompanist.insets.navigationBarsWithImePadding
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.ui.common.BackPressTopAppBar
import io.github.diubruteforce.smartcr.ui.common.CRListItem
import io.github.diubruteforce.smartcr.ui.common.SideEffect
import io.github.diubruteforce.smartcr.ui.theme.Margin
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun CourseListScreen(
    viewModel: CourseListViewModel,
    navigateToSectionList: (String) -> Unit,
    onBackPress: () -> Unit
) {
    val sideEffect = viewModel.sideEffect.collectAsState().value

    onActive {
        viewModel.loadData()
    }

    SideEffect(
        sideEffectState = sideEffect,
        onSuccess = { },
        onFailAlertDismissRequest = viewModel::clearSideEffect
    )

    CourseListScreenContent(
        stateFlow = viewModel.state,
        navigateToSectionList = navigateToSectionList,
        onBackPress = onBackPress
    )
}

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
private fun CourseListScreenContent(
    stateFlow: StateFlow<CourseListState>,
    navigateToSectionList: (String) -> Unit,
    onBackPress: () -> Unit
) {
    val state = stateFlow.collectAsState().value

    Scaffold(
        topBar = {
            BackPressTopAppBar(
                onBackPress = onBackPress,
                title = stringResource(id = R.string.courses)
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier.navigationBarsWithImePadding(),
            verticalArrangement = Arrangement.spacedBy(Margin.normal),
            contentPadding = PaddingValues(Margin.normal)
        ) {
            state.courses.forEach { entry ->
                val totalCredit = entry.value.fold(0.0) { value, course ->
                    value + course.credit
                }

                item {
                    Row(modifier = Modifier.padding(horizontal = Margin.small)) {
                        Text(
                            text = entry.key,
                            style = MaterialTheme.typography.h6
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = "Credit: $totalCredit",
                            style = MaterialTheme.typography.h6
                        )
                    }
                }

                items(entry.value) { course ->
                    CRListItem(
                        text = course.courseTitle,
                        itemClick = { navigateToSectionList.invoke(course.id) }
                    )
                }
            }
        }
    }
}