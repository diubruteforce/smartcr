package io.github.diubruteforce.smartcr.ui.resource

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import dev.chrisbanes.accompanist.insets.navigationBarsPadding
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.ui.common.Empty
import io.github.diubruteforce.smartcr.ui.common.SideEffect
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

    SideEffect(
        sideEffectState = sideEffect,
        onSuccess = { /*TODO*/ },
        onFailAlertDismissRequest = viewModel::clearSideEffect
    )

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            Text(text = "Sheet Content")
        }
    ) {
        ResourceScreenContent(
            stateFlow = viewModel.state,
            uploadFile = { /*TODO*/ }
        )
    }
}

@Composable
private fun ResourceScreenContent(
    stateFlow: StateFlow<ResourceState>,
    uploadFile: () -> Unit,
) {
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                modifier = Modifier.navigationBarsPadding(),
                text = { Text(text = stringResource(id = R.string.upload)) },
                icon = { Icon(imageVector = Icons.Outlined.Add) },
                backgroundColor = MaterialTheme.colors.primary,
                onClick = { /*TODO*/ }
            )
        }
    ) {
        LazyColumn {
            item {
                Empty(
                    title = stringResource(id = R.string.no_resource),
                    message = stringResource(id = R.string.no_resource_message),
                    image = vectorResource(id = R.drawable.no_exam)
                )
            }
        }
    }
}