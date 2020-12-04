package io.github.diubruteforce.smartcr.ui.onboading

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.ExperimentalFocus
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.ui.common.CRTextField
import io.github.diubruteforce.smartcr.ui.common.LargeButton
import io.github.diubruteforce.smartcr.ui.theme.Margin
import io.github.diubruteforce.smartcr.ui.theme.SmartCRTheme
import io.github.diubruteforce.smartcr.ui.theme.grayText

@OptIn(ExperimentalFocus::class)
@Composable
fun SignInScreenContent(){
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = stringResource(id = R.string.welcome_student),
            style = MaterialTheme.typography.h4
        )

        Text(
            text = stringResource(id = R.string.stay_sign_in),
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.grayText
        )
        Spacer(modifier = Modifier.weight(1f))
        
        CRTextField(
            modifier = Modifier.padding(horizontal = Margin.normal),
            value = "",
            onValueChange = { /*TODO*/ },
            placeHolder = "Email",
            focusRequester = FocusRequester(),
            onFocusChange = { /*TODO*/ }
        )
        
        Spacer(modifier = Modifier.height(Margin.big))

        CRTextField(
            modifier = Modifier.padding(horizontal = Margin.normal),
            value = "",
            onValueChange = { /*TODO*/ },
            placeHolder = "Email",
            focusRequester = FocusRequester(),
            onFocusChange = { /*TODO*/ }
        )

        Spacer(modifier = Modifier.height(Margin.big))

        Text(
            text = stringResource(id = R.string.forgot_password),
            color = MaterialTheme.colors.error
        )

        Spacer(modifier = Modifier.weight(1f))

        LargeButton(text = stringResource(id = R.string.sign_in))

        Spacer(modifier = Modifier.weight(0.5f))

        Text(
            text = stringResource(id = R.string.dont_have_account),
            color = MaterialTheme.colors.error
        )

        Spacer(modifier = Modifier.weight(0.5f))
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewSignInScreenContent(){
    SmartCRTheme {
        SignInScreenContent()
    }
}