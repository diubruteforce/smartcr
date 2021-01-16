package io.github.diubruteforce.smartcr.ui.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AmbientFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import dev.chrisbanes.accompanist.insets.navigationBarsWithImePadding
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.model.ui.StringFailSideEffectState
import io.github.diubruteforce.smartcr.model.ui.TypedSideEffectState
import io.github.diubruteforce.smartcr.ui.common.*
import io.github.diubruteforce.smartcr.ui.theme.Margin
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun TeacherListScreen(
    viewModel: TeacherListViewModel,
    onBackPress: () -> Unit,
    navigateToTeacherDetail: (String) -> Unit,
    addNewTeacher: () -> Unit,
) {
    val sideEffect = viewModel.sideEffect.collectAsState().value

    SideEffect(
        sideEffectState = sideEffect,
        onSuccess = { },
        onFailAlertDismissRequest = viewModel::clearSideEffect
    )

    TeacherListContent(
        sideEffectState = sideEffect,
        stateFlow = viewModel.state,
        onQueryChange = viewModel::changeQuery,
        onBackPress = onBackPress,
        addNewTeacher = addNewTeacher,
        navigateToTeacherDetail = navigateToTeacherDetail
    )
}

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
private fun TeacherListContent(
    sideEffectState: StringFailSideEffectState,
    stateFlow: StateFlow<TeacherListState>,
    onQueryChange: (String) -> Unit,
    onBackPress: () -> Unit,
    addNewTeacher: () -> Unit,
    navigateToTeacherDetail: (String) -> Unit
) {
    val focusManager = AmbientFocusManager.current
    val focusRequester = FocusRequester()
    val state = stateFlow.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsWithImePadding()
            .background(Color.White),
    ) {
        Card(
            shape = RoundedCornerShape(0.dp),
            elevation = 8.dp
        ) {
            Column {
                InsetAwareTopAppBar(
                    elevation = 0.dp
                ) {
                    IconButton(onClick = onBackPress) {
                        Icon(imageVector = Icons.Outlined.KeyboardArrowLeft)
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        text = stringResource(id = R.string.faculty)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(onClick = addNewTeacher) {
                        Icon(imageVector = Icons.Outlined.Add)
                    }
                }

                FullName(
                    modifier = Modifier.padding(horizontal = Margin.normal),
                    state = state.query,
                    placeHolder = stringResource(id = R.string.search_teacher),
                    onValueChange = onQueryChange,
                    imeAction = ImeAction.Search,
                    focusRequester = focusRequester,
                    onImeActionPerformed = {
                        focusManager.clearFocus()
                    }
                )
            }
        }

        LazyColumn(
            modifier = Modifier.background(Color.White),
            verticalArrangement = Arrangement.spacedBy(Margin.normal),
            contentPadding = PaddingValues(Margin.normal)
        ) {
            if (sideEffectState is TypedSideEffectState.Success && state.teacherList.isEmpty()) {
                item {
                    Empty(
                        title = "No Teacher Found",
                        message = "There is no teacher found at this point",
                        actionTitle = stringResource(id = R.string.create_teacher_profile),
                        onAction = addNewTeacher,
                        image = vectorResource(id = R.drawable.new_semseter)
                    )
                }
            } else {
                items(state.teacherList) { teacher ->
                    ProfileListItem(
                        title = teacher.fullName,
                        subTitle = "Department: ${teacher.departmentCode}",
                        profileUrl = teacher.profileUrl,
                        itemClick = { navigateToTeacherDetail.invoke(teacher.id) }
                    )
                }
            }
        }
    }
}