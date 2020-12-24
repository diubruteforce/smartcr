package io.github.diubruteforce.smartcr.ui.teacher

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.chrisbanes.accompanist.insets.navigationBarsWithImePadding
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.model.data.CounselingHourState
import io.github.diubruteforce.smartcr.model.data.Week
import io.github.diubruteforce.smartcr.ui.bottomsheet.ListBottomSheet
import io.github.diubruteforce.smartcr.ui.common.CounselingHour
import io.github.diubruteforce.smartcr.ui.common.TeacherProfileCard
import io.github.diubruteforce.smartcr.ui.theme.Margin
import io.github.diubruteforce.smartcr.utils.extension.rememberBackPressAwareBottomSheetState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TeacherDetailScreen(
    viewModel: TeacherDetailViewModel,
    navigateToTeacherEdit: (String) -> Unit,
    onBackPress: () -> Unit
) {
    val sheetState = rememberBackPressAwareBottomSheetState()

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            ListBottomSheet(
                title = stringResource(id = R.string.select_day),
                icon = Icons.Outlined.CalendarToday,
                onClose = sheetState::hide,
                list = Week.values().toList(),
                onItemClick = { }
            )
        }
    ) {
        TeacherDetailScreenContent(
            stateFlow = viewModel.state,
            onProfileEdit = navigateToTeacherEdit,
            onProfileDelete = viewModel::deleteProfile,
            onCall = { /*TODO*/ },
            onCounselingHourEdit = { /*TODO*/ },
            onCounselingHourDelete = { /*TODO*/ },
            onCounselingHourAdd = { /*TODO*/ }
        )
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
private fun TeacherDetailScreenContent(
    stateFlow: StateFlow<TeacherDetailState>,
    onProfileEdit: (String) -> Unit,
    onProfileDelete: (String) -> Unit,
    onCall: (String) -> Unit,
    onCounselingHourEdit: (CounselingHourState) -> Unit,
    onCounselingHourDelete: (CounselingHourState) -> Unit,
    onCounselingHourAdd: () -> Unit
) {
    val state = stateFlow.collectAsState().value

    Scaffold {
        ScrollableColumn(
            modifier = Modifier.navigationBarsWithImePadding(),
            verticalArrangement = Arrangement.spacedBy(Margin.normal),
            contentPadding = PaddingValues(Margin.normal)
        ) {
            state.teacher?.let {
                TeacherProfileCard(
                    teacher = it,
                    onCall = onCall,
                    onEdit = onProfileEdit,
                    onDelete = onProfileDelete
                )
            }

            Spacer(modifier = Modifier.size(Margin.normal))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(id = R.string.counseling_hour))

                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = onCounselingHourAdd) {
                    Icon(imageVector = Icons.Outlined.Edit)
                }
            }

            state.counselingHours.forEach {
                CounselingHour(
                    state = it,
                    onEdit = onCounselingHourEdit,
                    onDelete = onCounselingHourDelete
                )
            }
        }
    }
}