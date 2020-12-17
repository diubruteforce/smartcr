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
import androidx.compose.ui.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.ui.theme.Margin
import io.github.diubruteforce.smartcr.ui.theme.grayText
import io.github.diubruteforce.smartcr.utils.extension.DiuIdValidator
import io.github.diubruteforce.smartcr.utils.extension.PasswordValidator
import timber.log.Timber

data class TextFieldState(
    val value: String,
    val errorText: String,
    val isError: Boolean,
    private val validator: Regex,
) {
    fun validate(): TextFieldState = this.copy(isError = !validator.matches(value))

    companion object {
        val DiuIdState = TextFieldState(
            value = "",
            errorText = "Invalid Student Id",
            validator = Regex.DiuIdValidator,
            isError = false
        )

        val PasswordState = TextFieldState(
            value = "",
            errorText = "Must be more than 6 letter",
            validator = Regex.PasswordValidator,
            isError = false
        )
    }
}

@Composable
fun DiuId(
    modifier: Modifier = Modifier,
    state: TextFieldState,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester,
    imeAction: ImeAction = ImeAction.Next,
    onImeActionPerformed: (ImeAction) -> Unit = {}
) {
    CRTextFieldLayout(
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
    state: TextFieldState,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester,
    imeAction: ImeAction = ImeAction.Done,
    onImeActionPerformed: (ImeAction) -> Unit = {}
) {
    CRTextFieldLayout(
        modifier = modifier,
        isError = state.isError,
        errorText = state.errorText
    ) {
        CRTextField(
            value = state.value,
            onValueChange = onValueChange,
            placeHolder = stringResource(id = R.string.enter_your_password),
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
fun CRTextFieldLayout(
    modifier: Modifier = Modifier,
    isError: Boolean,
    errorText: String,
    content: @Composable () -> Unit
) {
    val errorColor = if (isError) MaterialTheme.colors.error else Color.Transparent

    Timber.d("error: $isError errorText: $errorText")

    Column(modifier = modifier) {
        content()

        Text(
            text = errorText,
            color = errorColor,
            style = MaterialTheme.typography.body2.copy(fontSize = 12.sp),
            modifier = Modifier.padding(start = Margin.small, bottom = Margin.tiny),
        )
    }
}

@Composable
fun CRTextField(
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
        shape = RoundedCornerShape(16.dp),
        elevation = 4.dp,
        border = BorderStroke(1.dp, MaterialTheme.colors.onBackground.copy(alpha = 0.08f)),
        modifier = modifier.clickable(indication = null) {
            focusRequester.requestFocus()
        }
    ) {
        ScrollableRow(
            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
            contentPadding = PaddingValues(start = Margin.medium, end = Margin.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.CenterStart
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth()
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