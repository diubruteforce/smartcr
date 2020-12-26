package io.github.diubruteforce.smartcr.ui.section

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.onActive
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.chrisbanes.accompanist.insets.navigationBarsPadding
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.ui.common.BackPressTopAppBar
import io.github.diubruteforce.smartcr.ui.common.SectionListItem
import io.github.diubruteforce.smartcr.ui.common.SideEffect
import io.github.diubruteforce.smartcr.ui.theme.Margin
import io.github.diubruteforce.smartcr.ui.theme.grayText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun SectionListScreen(
    viewModel: SectionListViewModel,
    courseId: String,
    navigateToSectionDetail: (String) -> Unit,
    createNewSection: (String) -> Unit,
    onBackPress: () -> Unit
) {
    val sideEffect = viewModel.sideEffect.collectAsState().value

    onActive {
        viewModel.loadData(courseId)
    }

    SideEffect(
        sideEffectState = sideEffect,
        onSuccess = { },
        onFailAlertDismissRequest = viewModel::clearSideEffect
    )

    SectionListScreenContent(
        stateFlow = viewModel.state,
        navigateToSectionDetail = navigateToSectionDetail,
        joinSection = viewModel::joinSection,
        leaveSection = viewModel::leaveSection,
        createNewSection = {
            if (viewModel.canCreateNewSection()) {
                createNewSection.invoke(courseId)
            }
        },
        onBackPress = onBackPress
    )
}

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
private fun SectionListScreenContent(
    stateFlow: StateFlow<SectionListState>,
    navigateToSectionDetail: (String) -> Unit,
    joinSection: (String) -> Unit,
    leaveSection: (String) -> Unit,
    createNewSection: () -> Unit,
    onBackPress: () -> Unit
) {
    val state = stateFlow.collectAsState().value

    Scaffold(
        topBar = {
            BackPressTopAppBar(
                onBackPress = onBackPress,
                title = stringResource(id = R.string.section_list)
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier.navigationBarsPadding(),
            contentPadding = PaddingValues(Margin.normal),
            verticalArrangement = Arrangement.spacedBy(Margin.normal)
        ) {
            if (state.course.id.isNotEmpty()) {
                item {
                    Column {
                        Text(
                            text = state.course.courseTitle,
                            style = MaterialTheme.typography.h6,
                            color = MaterialTheme.colors.primary
                        )

                        Text(text = "Course Code: ${state.course.courseCode}")
                        Text(text = "Credit Hour: ${state.course.credit}")
                        Text(text = "Level: ${state.course.level}")
                        Text(text = "Term: ${state.course.term}")
                    }
                }

                item {
                    Column {
                        Spacer(modifier = Modifier.size(Margin.normal))

                        OutlinedButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = createNewSection
                        ) {
                            Text(text = stringResource(id = R.string.create_new_section))
                        }

                        Spacer(modifier = Modifier.size(Margin.big))

                        if (state.sections.isNotEmpty()) {
                            Text(
                                text = stringResource(id = R.string.existing_sections),
                                style = MaterialTheme.typography.h6,
                                color = MaterialTheme.colors.grayText
                            )
                        }
                    }
                }
            }

            items(state.sections) { item ->
                SectionListItem(
                    state = item,
                    itemClick = { navigateToSectionDetail.invoke(item.courseId) },
                    onJoin = { joinSection.invoke(item.courseId) },
                    onLeave = { leaveSection.invoke(item.courseId) }
                )
            }
        }
    }
}