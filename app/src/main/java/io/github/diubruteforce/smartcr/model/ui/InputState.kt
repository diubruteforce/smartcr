package io.github.diubruteforce.smartcr.model.ui

import io.github.diubruteforce.smartcr.utils.extension.*

data class InputState(
    val value: String,
    val errorText: String,
    val isError: Boolean,
    private val validator: Regex,
) {
    fun validate(): InputState = this.copy(isError = !validator.matches(value))

    companion object {
        val FullNameState = InputState(
            value = "",
            errorText = "Name must be more than 3 letters",
            validator = Regex.NameValidator,
            isError = false
        )

        val DiuIdState = InputState(
            value = "",
            errorText = "Invalid Student Id",
            validator = Regex.DiuIdValidator,
            isError = false
        )

        val DiuEmailState = InputState(
            value = "",
            errorText = "Invalid DIU Email",
            validator = Regex.DiuEmailValidator,
            isError = false
        )

        val PhoneState = InputState(
            value = "",
            errorText = "Invalid phone number",
            validator = Regex.PhoneValidator,
            isError = false
        )

        val PasswordState = InputState(
            value = "",
            errorText = "Must be more than 6 letter",
            validator = Regex.PasswordValidator,
            isError = false
        )

        val RePasswordState = InputState(
            value = "",
            errorText = "Your password and confirmation password do not match.",
            validator = Regex.NonEmptyValidator,
            isError = false
        )

        val NotEmptyState = InputState(
            value = "",
            errorText = "This field can't be empty",
            validator = Regex.NonEmptyValidator,
            isError = false
        )

        val EmptyState = InputState(
            value = "",
            errorText = "This field can be empty",
            validator = Regex.EmptyValidator,
            isError = false
        )
    }
}