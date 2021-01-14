package io.github.diubruteforce.smartcr.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollableRow
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.model.ui.InputState
import io.github.diubruteforce.smartcr.ui.theme.CornerRadius
import io.github.diubruteforce.smartcr.ui.theme.Margin
import io.github.diubruteforce.smartcr.ui.theme.grayBorder
import io.github.diubruteforce.smartcr.ui.theme.grayText

@Composable
fun FullName(
    modifier: Modifier = Modifier,
    state: InputState,
    placeHolder: String = stringResource(id = R.string.enter_full_name),
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester,
    imeAction: ImeAction = ImeAction.Next,
    onImeActionPerformed: (ImeAction) -> Unit = {}
) {
    InputLayout(
        modifier = modifier,
        isError = state.isError,
        errorText = state.errorText
    ) {
        CRTextField(
            value = state.value,
            onValueChange = onValueChange,
            placeHolder = placeHolder,
            focusRequester = focusRequester,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = imeAction
            ),
            onImeActionPerformed = onImeActionPerformed,
        )
    }
}

@Composable
fun DiuEmail(
    modifier: Modifier = Modifier,
    state: InputState,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester,
    imeAction: ImeAction = ImeAction.Next,
    onImeActionPerformed: (ImeAction) -> Unit = {}
) {
    InputLayout(
        modifier = modifier,
        isError = state.isError,
        errorText = state.errorText
    ) {
        CRTextField(
            value = state.value,
            onValueChange = onValueChange,
            placeHolder = stringResource(id = R.string.enter_diu_email),
            focusRequester = focusRequester,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = imeAction
            ),
            onImeActionPerformed = onImeActionPerformed,
        )
    }
}

@Composable
fun PhoneNumber(
    modifier: Modifier = Modifier,
    state: InputState,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester,
    imeAction: ImeAction = ImeAction.Next,
    onImeActionPerformed: (ImeAction) -> Unit = {}
) {
    InputLayout(
        modifier = modifier,
        isError = state.isError,
        errorText = state.errorText
    ) {
        CRTextField(
            value = state.value,
            onValueChange = onValueChange,
            placeHolder = stringResource(id = R.string.enter_phone_number),
            focusRequester = focusRequester,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = imeAction
            ),
            onImeActionPerformed = onImeActionPerformed,
        )
    }
}

@Composable
fun DiuId(
    modifier: Modifier = Modifier,
    state: InputState,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester,
    imeAction: ImeAction = ImeAction.Next,
    onImeActionPerformed: (ImeAction) -> Unit = {}
) {
    InputLayout(
        modifier = modifier,
        isError = state.isError,
        errorText = state.errorText
    ) {
        CRTextField(
            value = state.value,
            onValueChange = onValueChange,
            placeHolder = stringResource(id = R.string.enter_your_student_id),
            focusRequester = focusRequester,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = imeAction
            ),
            onImeActionPerformed = onImeActionPerformed,
        )
    }
}

@Composable
fun Password(
    modifier: Modifier = Modifier,
    state: InputState,
    placeHolder: String = stringResource(id = R.string.enter_your_password),
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester,
    imeAction: ImeAction = ImeAction.Done,
    onImeActionPerformed: (ImeAction) -> Unit = {}
) {
    InputLayout(
        modifier = modifier,
        isError = state.isError,
        errorText = state.errorText
    ) {
        CRTextField(
            value = state.value,
            onValueChange = onValueChange,
            placeHolder = placeHolder,
            focusRequester = focusRequester,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = imeAction
            ),
            onImeActionPerformed = onImeActionPerformed,
            visualTransformation = PasswordVisualTransformation()
        )
    }
}

@Composable
fun Description(
    modifier: Modifier = Modifier,
    state: InputState,
    placeHolder: String = stringResource(id = R.string.enter_your_password),
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester
) {
    InputLayout(
        modifier = modifier,
        isError = state.isError,
        errorText = state.errorText
    ) {
        Card(
            shape = RoundedCornerShape(CornerRadius.normal),
            elevation = 4.dp,
            border = BorderStroke(1.dp, MaterialTheme.colors.grayBorder),
            modifier = modifier.clickable(indication = null) {
                focusRequester.requestFocus()
            }
        ) {
            Box(
                modifier = Modifier
                    .heightIn(min = 112.dp)
                    .padding(Margin.normal),
                contentAlignment = Alignment.TopStart
            ) {
                BasicTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    value = state.value,
                    onValueChange = onValueChange,
                    textStyle = MaterialTheme.typography.body1,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Unspecified,
                        keyboardType = KeyboardType.Text
                    ),
                    cursorColor = MaterialTheme.colors.primary
                )

                if (state.value.isEmpty()) {
                    Text(
                        text = placeHolder,
                        modifier = Modifier,
                        color = MaterialTheme.colors.grayText,
                        style = MaterialTheme.typography.body1
                    )
                }
            }

        }
    }
}

@Composable
private fun CRTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeHolder: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTextInputStarted: (SoftwareKeyboardController) -> Unit = {},
    onImeActionPerformed: (ImeAction) -> Unit = {},
    focusRequester: FocusRequester
) {
    Card(
        shape = RoundedCornerShape(CornerRadius.normal),
        elevation = 4.dp,
        border = BorderStroke(1.dp, MaterialTheme.colors.grayBorder),
        modifier = modifier.clickable(indication = null) {
            focusRequester.requestFocus()
        }
    ) {
        ScrollableRow(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp),
            contentPadding = PaddingValues(start = Margin.medium, end = Margin.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.CenterStart
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    textStyle = MaterialTheme.typography.body1,
                    keyboardOptions = keyboardOptions,
                    onTextInputStarted = onTextInputStarted,
                    maxLines = 1,
                    visualTransformation = visualTransformation,
                    cursorColor = MaterialTheme.colors.primary,
                    onImeActionPerformed = onImeActionPerformed
                )

                if (value.isEmpty()) {
                    Text(
                        text = placeHolder,
                        modifier = Modifier,
                        color = MaterialTheme.colors.grayText,
                        style = MaterialTheme.typography.body1
                    )
                }
            }
        }
    }
}