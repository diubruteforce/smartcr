package io.github.diubruteforce.smartcr.ui.resource

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.HistoryEdu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.AmbientFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import dev.chrisbanes.accompanist.insets.AmbientWindowInsets
import dev.chrisbanes.accompanist.insets.navigationBarsHeight
import dev.chrisbanes.accompanist.insets.navigationBarsPadding
import dev.chrisbanes.accompanist.insets.toPaddingValues
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.model.data.Resource
import io.github.diubruteforce.smartcr.ui.bottomsheet.SheetHeader
import io.github.diubruteforce.smartcr.ui.bottomsheet.SheetListItem
import io.github.diubruteforce.smartcr.ui.common.*
import io.github.diubruteforce.smartcr.ui.theme.Margin
import io.github.diubruteforce.smartcr.utils.extension.getMainActivity
import io.github.diubruteforce.smartcr.utils.extension.getName
import io.github.diubruteforce.smartcr.utils.extension.rememberBackPressAwareBottomSheetState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterialApi::class, ExperimentalCoroutinesApi::class)
@Composable
fun ResourceScreen(
    viewModel: ResourceViewModel
) {
    val sheetState = rememberBackPressAwareBottomSheetState()
    val sideEffect = viewModel.sideEffect.collectAsState().value
    val mainActivity = getMainActivity()

    SideEffect(
        sideEffectState = sideEffect,
        onSuccess = {},
        onFailAlertDismissRequest = viewModel::clearSideEffect
    )

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            SheetHeader(
                title = stringResource(id = R.string.select_section_name),
                icon = Icons.Outlined.HistoryEdu,
                onClose = sheetState::hide
            )

            viewModel.joinedSections.forEach {
                SheetListItem(
                    name = "${it.course.courseCode} (${it.name})",
                    onSelected = {
                        sheetState.hide()
                        viewModel.changeSection(it)
                    }
                )
            }

            Spacer(modifier = Modifier.navigationBarsHeight())
        }
    ) {
        ResourceScreenContent(
            stateFlow = viewModel.state,
            onQueryChange = viewModel::search,
            onNameChange = viewModel::onTitleChange,
            changeSection = { sheetState.show() },
            changeFile = {
                if (viewModel.canChangeFile()) {
                    mainActivity.pickFile {
                        it?.let {
                            viewModel.changeFile(
                                newFile = it,
                                fileName = it.getName(mainActivity.contentResolver) ?: ""
                            )
                        }
                    }
                }
            },
            startEdit = viewModel::startEditing,
            cancelEdit = viewModel::cancelEditing,
            uploadFile = viewModel::uploadFile,
            downloadFile = viewModel::downloadFile
        )
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
private fun ResourceScreenContent(
    stateFlow: StateFlow<ResourceState>,
    onQueryChange: (String) -> Unit,
    onNameChange: (String) -> Unit,
    changeSection: () -> Unit,
    changeFile: () -> Unit,
    startEdit: (Resource) -> Unit,
    cancelEdit: () -> Unit,
    uploadFile: () -> Unit,
    downloadFile: (Resource) -> Unit
) {
    val state = stateFlow.collectAsState().value

    val inset = AmbientWindowInsets.current
    val focusManager = AmbientFocusManager.current

    Scaffold(
        floatingActionButton = {
            if (state.editingResource == null) {
                ExtendedFloatingActionButton(
                    modifier = Modifier.navigationBarsPadding(),
                    text = { Text(text = stringResource(id = R.string.upload)) },
                    icon = { Icon(imageVector = Icons.Outlined.Add) },
                    backgroundColor = MaterialTheme.colors.primary,
                    onClick = { startEdit.invoke(Resource()) }
                )
            }
        }
    ) {
        LazyColumn(
            contentPadding = inset.navigationBars.toPaddingValues().copy(
                start = Margin.normal,
                end = Margin.normal,
                top = Margin.normal
            ),
            verticalArrangement = Arrangement.spacedBy(Margin.normal)
        ) {
            when {
                state.editingResource != null -> item {
                    ResourceScreenEdit(
                        state = state,
                        onNameChange = onNameChange,
                        changeSection = changeSection,
                        changeFile = changeFile,
                        cancelEdit = cancelEdit,
                        uploadFile = uploadFile
                    )
                }
                state.resources.isEmpty() && state.query.value.isEmpty() -> item {
                    Empty(
                        title = stringResource(id = R.string.no_resource),
                        message = stringResource(id = R.string.no_resource_message),
                        image = vectorResource(id = R.drawable.no_exam)
                    )
                }
                else -> {
                    item {
                        FullName(
                            modifier = Modifier
                                .padding(horizontal = Margin.normal)
                                .padding(top = Margin.normal),
                            state = state.query,
                            placeHolder = stringResource(id = R.string.search_resource),
                            onValueChange = onQueryChange,
                            imeAction = ImeAction.Search,
                            focusRequester = FocusRequester(),
                            onImeActionPerformed = {
                                focusManager.clearFocus()
                            }
                        )
                    }

                    items(state.resources) {
                        ResourceListItem(
                            resource = it.first,
                            progressType = it.second,
                            onClick = { downloadFile(it.first) }
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.navigationBarsHeight()) }
        }
    }
}

@Composable
private fun ResourceScreenEdit(
    state: ResourceState,
    onNameChange: (String) -> Unit,
    changeSection: () -> Unit,
    changeFile: () -> Unit,
    cancelEdit: () -> Unit,
    uploadFile: () -> Unit,
) {
    val focusManager = AmbientFocusManager.current

    Column(verticalArrangement = Arrangement.spacedBy(Margin.normal)) {
        FullName(
            state = state.title,
            placeHolder = stringResource(id = R.string.title),
            onValueChange = onNameChange,
            focusRequester = FocusRequester(),
            onImeActionPerformed = {
                changeSection.invoke()
                focusManager.clearFocus()
            }
        )

        CRSelection(
            state = state.section,
            placeHolder = stringResource(id = R.string.section),
            onClick = {
                changeSection.invoke()
                focusManager.clearFocus()
            }
        )

        CRSelection(
            state = state.file,
            placeHolder = stringResource(id = R.string.select_file),
            onClick = {
                changeFile.invoke()
                focusManager.clearFocus()
            }
        )

        Row(horizontalArrangement = Arrangement.spacedBy(Margin.small)) {
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = cancelEdit
            ) {
                Text(text = stringResource(id = R.string.cancel))
            }

            Button(
                modifier = Modifier.weight(1f),
                onClick = uploadFile
            ) {
                Text(text = stringResource(id = R.string.upload))
            }
        }
    }
}