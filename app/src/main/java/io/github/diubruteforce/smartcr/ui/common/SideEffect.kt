package io.github.diubruteforce.smartcr.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.model.ui.TypedSideEffectState
import timber.log.Timber

@Composable
fun <Loading, Success, Fail> SideEffect(
    sideEffectState: TypedSideEffectState<Loading, Success, Fail>,
    onSuccess: (Success) -> Unit,
    onFailAlertDismissRequest: (Fail) -> Unit,
    onFailAlertDenial: (Fail) -> Unit = onFailAlertDismissRequest,
    onFailAlertAffirmation: (Fail) -> Unit = onFailAlertDismissRequest,
    title: String = stringResource(id = R.string.error),
    affirmationText: String = stringResource(id = R.string.try_again),
    denialText: String = stringResource(id = R.string.cancel)
) {
    when (sideEffectState) {
        TypedSideEffectState.Uninitialized -> {
        }
        is TypedSideEffectState.Loading -> Loading()
        is TypedSideEffectState.Success -> onSuccess.invoke(sideEffectState.type)
        is TypedSideEffectState.Fail -> {

            val message = try {
                sideEffectState.type as String
            } catch (ex: Exception) {
                Timber.e(ex)
                stringResource(id = R.string.something_went_wrong)
            }

            CRAlertDialog(
                title = title,
                message = message,
                onDenial = { onFailAlertDenial.invoke(sideEffectState.type) },
                denialText = denialText,
                onAffirmation = { onFailAlertAffirmation.invoke(sideEffectState.type) },
                affirmationText = affirmationText,
                onDismissRequest = { onFailAlertDismissRequest.invoke(sideEffectState.type) }
            )
        }
    }
}