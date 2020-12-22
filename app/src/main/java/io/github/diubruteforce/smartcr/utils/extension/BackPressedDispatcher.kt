package io.github.diubruteforce.smartcr.utils.extension

import androidx.activity.addCallback
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.onDispose
import androidx.compose.runtime.remember


@ExperimentalMaterialApi
@Composable
fun rememberBackPressAwareBottomSheetState(): ModalBottomSheetState {
    val sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val mainActivity = getMainActivity()
    val onBackPressedCallback = remember {
        mainActivity.onBackPressedDispatcher.addCallback {
            if (sheetState.isVisible) sheetState.hide()
        }
    }

    onBackPressedCallback.isEnabled = sheetState.isVisible
    onDispose { onBackPressedCallback.remove() }

    return sheetState
}