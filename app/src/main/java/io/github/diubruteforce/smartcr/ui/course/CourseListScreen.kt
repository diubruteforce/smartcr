package io.github.diubruteforce.smartcr.ui.course

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.onActive
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.chrisbanes.accompanist.insets.navigationBarsWithImePadding
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.ui.common.CRListItem
import io.github.diubruteforce.smartcr.ui.common.InsetAwareTopAppBar
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
            InsetAwareTopAppBar {
                IconButton(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    onClick = onBackPress
                ) {
                    Icon(imageVector = Icons.Outlined.KeyboardArrowLeft)
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = stringResource(id = R.string.courses)
                )

                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = {}) {}
            }
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