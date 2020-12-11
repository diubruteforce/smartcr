package io.github.diubruteforce.smartcr.utils.extension

import android.util.Patterns

val Regex.Companion.EmailValidator get() = Patterns.EMAIL_ADDRESS.toRegex()
val Regex.Companion.DiuIdValidator get() = "\\d{3}-\\d{2}-\\d{4}".toRegex()
val Regex.Companion.NonEmptyValidator get() = ".+".toRegex()
val Regex.Companion.PasswordValidator get() = "\\S{6}.+".toRegex()