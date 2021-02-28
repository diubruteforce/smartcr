package io.github.diubruteforce.smartcr.ui.resource

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.HistoryEdu
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.core.content.ContextCompat
import dev.chrisbanes.accompanist.insets.LocalWindowInsets
import dev.chrisbanes.accompanist.insets.toPaddingValues
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.model.data.Resource
import io.github.diubruteforce.smartcr.model.ui.ReadPermissionError
import io.github.diubruteforce.smartcr.model.ui.StringFailSideEffectState
import io.github.diubruteforce.smartcr.model.ui.TypedSideEffectState
import io.github.diubruteforce.smartcr.model.ui.WritePermissionError
import io.github.diubruteforce.smartcr.ui.bottomsheet.SheetHeader
import io.github.diubruteforce.smartcr.ui.bottomsheet.SheetListItem
import io.github.diubruteforce.smartcr.ui.common.*
import io.github.diubruteforce.smartcr.ui.theme.Margin
import io.github.diubruteforce.smartcr.utils.extension.getMainActivity
import io.github.diubruteforce.smartcr.utils.extension.getName
import io.github.diubruteforce.smartcr.utils.extension.isUpLoadable
import io.github.diubruteforce.smartcr.utils.extension.rememberBackPressAwareBottomSheetState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalCoroutinesApi::class)
@Composable
fun ResourceScreen(
    viewModel: ResourceViewModel
) {
    val sheetState = rememberBackPressAwareBottomSheetState()
    val sideEffect = viewModel.sideEffect.collectAsState().value
    val mainActivity = getMainActivity()
    var deleteEvent by remember { mutableStateOf<Resource?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(true) {
        val readPermission = ContextCompat.checkSelfPermission(
            mainActivity,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        if (readPermission == PackageManager.PERMISSION_GRANTED) {
            viewModel.loadData()
        } else {
            mainActivity.requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE) {
                if (it) viewModel.loadData()
                else viewModel.permissionNotGranted(String.ReadPermissionError)
            }
        }
    }

    if (deleteEvent != null) {
        CRAlertDialog(
            title = stringResource(id = R.string.are_you_sure),
            message = stringResource(id = R.string.resource_delete),
            onDenial = {
                viewModel.deleteFile(deleteEvent!!)
                deleteEvent = null
            },
            denialText = stringResource(id = R.string.delete),
            onAffirmation = { deleteEvent = null },
            affirmationText = stringResource(id = R.string.cancel),
            onDismissRequest = { deleteEvent = null }
        )
    }

    SideEffect(
        sideEffectState = sideEffect,
        onSuccess = {},
        onFailAlertDismissRequest = {
            if (it != String.ReadPermissionError) {
                viewModel.clearSideEffect(it)
            }
        },
        denialText = "",
        onFailAlertAffirmation = { failType ->
            if (failType == String.ReadPermissionError) {
                mainActivity.requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE) {
                    if (it) viewModel.loadData()
                    else viewModel.permissionNotGranted(String.ReadPermissionError)
                }
            }

            viewModel.clearSideEffect(failType)
        }
    )

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            SheetHeader(
                title = stringResource(id = R.string.select_section_name),
                imageVector = Icons.Outlined.HistoryEdu,
                onClose = { scope.launch { sheetState.hide() } }
            )

            viewModel.joinedSections.forEach {
                SheetListItem(
                    name = "${it.course.courseCode} (${it.name})",
                    onSelected = {
                        scope.launch { sheetState.hide() }
                        viewModel.changeSection(it)
                    }
                )
            }

            Spacer(modifier = Modifier.height(height = Margin.inset))
        }
    ) {
        ResourceScreenContent(
            sideEffectState = sideEffect,
            stateFlow = viewModel.state,
            onQueryChange = viewModel::search,
            onNameChange = viewModel::onTitleChange,
            changeSection = { scope.launch { sheetState.show() } },
            changeFile = {
                if (viewModel.canChangeFile()) {
                    mainActivity.pickFile {
                        it?.let {
                            if (it.isUpLoadable(mainActivity.contentResolver)) {
                                viewModel.changeFile(
                                    newFile = it,
                                    fileName = it.getName(mainActivity.contentResolver) ?: ""
                                )
                            } else {
                                viewModel.setFileNotUpLoadable()
                            }
                        }
                    }
                }
            },
            startEdit = viewModel::startEditing,
            cancelEdit = viewModel::cancelEditing,
            uploadFile = viewModel::uploadFile,
            downloadFile = { resource ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    viewModel.downloadFile(resource)
                } else {
                    val readPermission = ContextCompat.checkSelfPermission(
                        mainActivity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )

                    if (readPermission == PackageManager.PERMISSION_GRANTED) {
                        viewModel.downloadFile(resource)
                    } else {
                        mainActivity.requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                            if (it) viewModel.downloadFile(resource)
                            else viewModel.permissionNotGranted(String.WritePermissionError)
                        }
                    }
                }
            },
            openFile = { uri, mimeType ->
                val intent = Intent()
                intent.action = Intent.ACTION_VIEW
                intent.setDataAndType(uri, mimeType)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                mainActivity.startActivity(intent)
            },
            deleteFile = { deleteEvent = it },
            updateFile = viewModel::startEditing
        )
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
private fun ResourceScreenContent(
    sideEffectState: StringFailSideEffectState,
    stateFlow: StateFlow<ResourceState>,
    onQueryChange: (String) -> Unit,
    onNameChange: (String) -> Unit,
    changeSection: () -> Unit,
    changeFile: () -> Unit,
    startEdit: (Resource) -> Unit,
    cancelEdit: () -> Unit,
    uploadFile: () -> Unit,
    updateFile: (Resource) -> Unit,
    deleteFile: (Resource) -> Unit,
    downloadFile: (Resource) -> Unit,
    openFile: (Uri, String) -> Unit
) {
    val state = stateFlow.collectAsState().value

    val inset = LocalWindowInsets.current
    val focusManager = LocalFocusManager.current

    Scaffold(
        floatingActionButton = {
            if (state.editingResource == null) {
                ExtendedFloatingActionButton(
                    modifier = Modifier.padding(bottom = Margin.inset),
                    text = { Text(text = stringResource(id = R.string.upload)) },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = "Add"
                        )
                    },
                    backgroundColor = MaterialTheme.colors.primary,
                    onClick = { startEdit.invoke(Resource()) }
                )
            }
        }
    ) {
        LazyColumn(
            contentPadding = inset.navigationBars.toPaddingValues(
                additionalStart = Margin.normal,
                additionalEnd = Margin.normal,
                additionalTop = Margin.normal
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
                sideEffectState is TypedSideEffectState.Success
                        && state.query.value.isEmpty()
                        && state.resources.isEmpty() -> item {
                    Empty(
                        title = stringResource(id = R.string.no_resource),
                        message = stringResource(id = R.string.no_resource_message),
                        image = painterResource(id = R.drawable.no_exam)
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
                            onClick = {
                                val progressType = it.second

                                if (progressType is ProgressType.View) openFile(
                                    progressType.uri,
                                    it.first.mimeType
                                )
                                else downloadFile(it.first)
                            },
                            onDelete = { deleteFile.invoke(it.first) },
                            onEdit = { updateFile.invoke(it.first) }
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(Margin.inset)) }
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
    val focusManager = LocalFocusManager.current

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