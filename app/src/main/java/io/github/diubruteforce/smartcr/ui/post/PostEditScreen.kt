package io.github.diubruteforce.smartcr.ui.post

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.Grade
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.SettingsCell
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.AmbientFocusManager
import androidx.compose.ui.res.stringResource
import dev.chrisbanes.accompanist.insets.navigationBarsWithImePadding
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.model.data.GroupType
import io.github.diubruteforce.smartcr.model.data.PostType
import io.github.diubruteforce.smartcr.ui.bottomsheet.DatePickerBottomSheet
import io.github.diubruteforce.smartcr.ui.bottomsheet.ListBottomSheet
import io.github.diubruteforce.smartcr.ui.bottomsheet.TimePicker
import io.github.diubruteforce.smartcr.ui.common.*
import io.github.diubruteforce.smartcr.ui.theme.Margin
import io.github.diubruteforce.smartcr.utils.extension.getMainActivity
import io.github.diubruteforce.smartcr.utils.extension.rememberBackPressAwareBottomSheetState
import io.github.diubruteforce.smartcr.utils.extension.toCalender
import io.github.diubruteforce.smartcr.utils.extension.toDateString
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private enum class PostEditSheet {
    Number, Section, GroupType, MaxMember
}

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalMaterialApi::class)
@Composable
fun PostEditScreen(
    viewModel: PostEditViewModel,
    postType: PostType,
    postId: String?,
    onBackPress: () -> Unit
) {
    val sheetState = rememberBackPressAwareBottomSheetState()
    var sheetType by remember { mutableStateOf(PostEditSheet.GroupType) }
    val focusManager = AmbientFocusManager.current
    val scope = rememberCoroutineScope()
    val mainActivity = getMainActivity()

    val sideEffect = viewModel.sideEffect.collectAsState().value

    onActive {
        viewModel.loadData(postType, postId)
    }

    SideEffect(
        sideEffectState = sideEffect,
        onSuccess = {
            if (it == PostEditSuccess.Saved) onBackPress.invoke()
        },
        onFailAlertDismissRequest = viewModel::clearSideEffect
    )

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            when (sheetType) {
                PostEditSheet.Number -> {
                    ListBottomSheet(
                        title = "Select ${postType.name} number",
                        icon = Icons.Outlined.ConfirmationNumber,
                        onClose = sheetState::hide,
                        list = (1..10).map { "${postType.name} $it" },
                        onItemClick = {
                            viewModel.changeNumber(it)
                            sheetState.hide()
                        }
                    )
                }
                PostEditSheet.Section -> {
                    ListBottomSheet(
                        title = stringResource(id = R.string.select_section_name),
                        icon = Icons.Outlined.SettingsCell,
                        onClose = sheetState::hide,
                        list = viewModel.joinedSections,
                        onItemClick = {
                            viewModel.changeSection(it)
                            sheetState.hide()
                        }
                    )
                }
                PostEditSheet.GroupType -> {
                    ListBottomSheet(
                        title = stringResource(id = R.string.select_post_type),
                        icon = Icons.Outlined.Group,
                        onClose = sheetState::hide,
                        list = GroupType.values().toList(),
                        onItemClick = {
                            viewModel.changeGroupType(it)
                            sheetState.hide()
                        }
                    )
                }
                PostEditSheet.MaxMember -> {
                    ListBottomSheet(
                        title = stringResource(id = R.string.select_max_member),
                        icon = Icons.Outlined.Grade,
                        onClose = sheetState::hide,
                        list = (1..10).toList(),
                        onItemClick = {
                            viewModel.changeMaxMember(it.toString())
                            sheetState.hide()
                        }
                    )
                }
            }
        }
    ) {
        PostEditScreenContent(
            stateFlow = viewModel.state,
            postType = postType,
            selectSection = {
                scope.launch {
                    focusManager.clearFocus()
                    sheetType = PostEditSheet.Section
                    delay(10)
                    sheetState.show()
                }
            },
            selectNumber = {
                scope.launch {
                    focusManager.clearFocus()
                    sheetType = PostEditSheet.Number
                    delay(10)
                    sheetState.show()
                }
            },
            selectDate = { date ->
                val datePicker = DatePickerBottomSheet(date.toCalender()) {
                    viewModel.changeDate(it.toDateString())
                }

                datePicker.show(mainActivity.supportFragmentManager, "Date")
            },
            selectTime = {
                val timePicker = TimePicker(time = it, onResult = viewModel::changeTime)
                timePicker.show(mainActivity.supportFragmentManager, "Time")
            },
            selectGroupType = {
                scope.launch {
                    focusManager.clearFocus()
                    sheetType = PostEditSheet.GroupType
                    delay(10)
                    sheetState.show()
                }
            },
            selectMaxMember = {
                scope.launch {
                    focusManager.clearFocus()
                    sheetType = PostEditSheet.MaxMember
                    delay(10)
                    sheetState.show()
                }
            },
            onPlaceChange = viewModel::changePlace,
            onSyllabusChange = viewModel::changeSyllabus,
            onDetailsChange = viewModel::changeDetails,
            savePost = viewModel::savePost,
            onBackPress = onBackPress
        )
    }
}

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalComposeUiApi::class)
@Composable
private fun PostEditScreenContent(
    stateFlow: StateFlow<PostEditState>,
    postType: PostType,
    selectSection: () -> Unit,
    selectNumber: () -> Unit,
    selectDate: (String) -> Unit,
    selectTime: (String) -> Unit,
    selectGroupType: () -> Unit,
    selectMaxMember: () -> Unit,
    onPlaceChange: (String) -> Unit,
    onSyllabusChange: (String) -> Unit,
    onDetailsChange: (String) -> Unit,
    savePost: () -> Unit,
    onBackPress: () -> Unit
) {
    val state = stateFlow.collectAsState().value

    Scaffold(
        topBar = {
            BackPressTopAppBar(
                onBackPress = onBackPress,
                title = "Edit ${postType.name}"
            )
        }
    ) {
        ScrollableColumn(
            modifier = Modifier.navigationBarsWithImePadding(),
            contentPadding = PaddingValues(Margin.normal),
            verticalArrangement = Arrangement.spacedBy(Margin.small)
        ) {
            CRSelection(
                state = state.section,
                placeHolder = stringResource(id = R.string.section),
                onClick = selectSection
            )

            CRSelection(
                state = state.number,
                placeHolder = "${postType.name} number",
                onClick = selectNumber
            )

            CRSelection(
                state = state.date,
                placeHolder = stringResource(id = R.string.submission_date),
                onClick = { selectDate.invoke(state.date.value) }
            )

            CRSelection(
                state = state.time,
                placeHolder = stringResource(id = R.string.submission_time),
                onClick = { selectTime.invoke(state.time.value) }
            )

            if (postType != PostType.Quiz) {
                CRSelection(
                    state = state.groupType,
                    placeHolder = stringResource(id = R.string.type),
                    onClick = selectGroupType
                )

                if (state.groupType.value == GroupType.Group.name) {
                    CRSelection(
                        state = state.maxMember,
                        placeHolder = stringResource(id = R.string.max_member),
                        onClick = selectMaxMember
                    )
                }
            }

            val (placeFocusRequester,
                syllabusFocusRequester,
                detailsFocusRequester) = FocusRequester.createRefs()

            FullName(
                state = state.place,
                onValueChange = onPlaceChange,
                placeHolder = stringResource(id = R.string.place_with_example),
                focusRequester = placeFocusRequester,
                onImeActionPerformed = {
                    if (postType == PostType.Quiz) syllabusFocusRequester.requestFocus()
                    else detailsFocusRequester.requestFocus()
                }
            )

            if (postType == PostType.Quiz) {
                Description(
                    state = state.syllabus,
                    onValueChange = onSyllabusChange,
                    placeHolder = stringResource(id = R.string.syllabus),
                    focusRequester = syllabusFocusRequester
                )
            }

            Description(
                state = state.details,
                onValueChange = onDetailsChange,
                placeHolder = stringResource(id = R.string.detail),
                focusRequester = detailsFocusRequester
            )

            LargeButton(text = stringResource(id = R.string.save), onClick = savePost)
        }
    }
}