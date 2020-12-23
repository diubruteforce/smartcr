package io.github.diubruteforce.smartcr.utils.extension

val Regex.Companion.DiuEmailValidator get() = ".+@diu.edu.bd".toRegex()
val Regex.Companion.DiuIdValidator get() = "\\d{3}-\\d{2}-\\d{4}".toRegex()
val Regex.Companion.NonEmptyValidator get() = ".+".toRegex()
val Regex.Companion.NameValidator get() = "\\S{3}.+".toRegex()
val Regex.Companion.PasswordValidator get() = "\\S{6}.+".toRegex()
val Regex.Companion.PhoneValidator get() = "\\+?(88)?01[0-9]{9}\\b".toRegex()