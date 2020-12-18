package io.github.diubruteforce.smartcr.ui.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.model.ui.TypedSideEffectState
import io.github.diubruteforce.smartcr.ui.theme.Margin
import timber.log.Timber

@Composable
fun <Loading, Success, Fail> SideEffect(
    sideEffectState: TypedSideEffectState<Loading, Success, Fail>,
    onSuccess: (Success) -> Unit,
    onFailAlertDismissRequest: () -> Unit,
    onFailAlertDenial: () -> Unit = onFailAlertDismissRequest,
    onFailAlertAffirmation: () -> Unit = onFailAlertDismissRequest,
    title: String = stringResource(id = R.string.error),
    affirmationText: String = stringResource(id = R.string.ok),
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

            AlertDialog(
                onDismissRequest = onFailAlertDismissRequest,
                title = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.h5
                    )
                },
                text = {
                    Text(text = message)
                },
                buttons = {
                    Row(
                        modifier = Modifier.padding(horizontal = Margin.normal)
                            .padding(bottom = Margin.normal)
                    ) {
                        TextButton(
                            modifier = Modifier.weight(1f),
                            onClick = onFailAlertDenial,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colors.error
                            )
                        ) {
                            Text(text = denialText)
                        }

                        Spacer(modifier = Modifier.width(Margin.normal))

                        OutlinedButton(
                            modifier = Modifier.weight(1f),
                            onClick = onFailAlertAffirmation
                        ) {
                            Text(text = affirmationText)
                        }
                    }
                }
            )
        }
    }
}