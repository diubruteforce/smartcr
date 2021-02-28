package io.github.diubruteforce.smartcr.utils.extension

import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.activity.addCallback
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch


@ExperimentalMaterialApi
@Composable
fun rememberBackPressAwareBottomSheetState(): ModalBottomSheetState {
    val sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val mainActivity = LocalContext.current as OnBackPressedDispatcherOwner
    val scope = rememberCoroutineScope()
    val onBackPressedCallback = remember {
        mainActivity.onBackPressedDispatcher.addCallback {
            if (sheetState.isVisible) scope.launch { sheetState.hide() }
        }
    }

    onBackPressedCallback.isEnabled = sheetState.isVisible

    DisposableEffect(true) {
        onDispose { onBackPressedCallback.remove() }
    }

    return sheetState
}

@Composable
fun rememberOnBackPressCallback(onBackPress: () -> Unit): OnBackPressedCallback {
    val mainActivity = LocalContext.current as OnBackPressedDispatcherOwner
    return remember {
        mainActivity.onBackPressedDispatcher.addCallback {
            onBackPress.invoke()
        }
    }
}