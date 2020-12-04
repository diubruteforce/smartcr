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
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focusObserver
import androidx.compose.ui.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SoftwareKeyboardController
import androidx.compose.ui.unit.dp
import io.github.diubruteforce.smartcr.ui.theme.Margin
import io.github.diubruteforce.smartcr.ui.theme.grayBackground
import io.github.diubruteforce.smartcr.ui.theme.grayText

@Composable
fun TextFieldLayout(
    isError: Boolean,
    errorText: String,
    content: @Composable () -> Unit
) {
    val errorColor = if (isError) MaterialTheme.colors.error else Color.Transparent

    Column {
        content()

        Text(
            text = errorText,
            color = errorColor,
            style = MaterialTheme.typography.overline,
            modifier = Modifier.padding(start = Margin.small, bottom = Margin.tiny),
        )
    }
}

@OptIn(ExperimentalFocus::class)
@Composable
fun CRTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeHolder: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onTextInputStarted: (SoftwareKeyboardController) -> Unit = {},
    focusRequester: FocusRequester,
    onFocusChange: (Boolean) -> Unit
){
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = 4.dp,
        border = BorderStroke(1.dp, MaterialTheme.colors.grayBackground),
        modifier = modifier.clickable(indication = null){
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
            ){
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth()
                        .focusRequester(focusRequester)
                        .focusObserver { onFocusChange(it == FocusState.Active) },
                    textStyle = MaterialTheme.typography.body1,
                    keyboardOptions = keyboardOptions,
                    onTextInputStarted = onTextInputStarted,
                    maxLines = 1,
                    cursorColor = MaterialTheme.colors.primary
                )

                if (value.isEmpty()){
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