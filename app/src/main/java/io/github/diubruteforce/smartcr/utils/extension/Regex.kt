package io.github.diubruteforce.smartcr.utils.extension

val Regex.Companion.DiuEmailValidator get() = ".+@(diu|daffodilvarsity).edu.bd".toRegex()
val Regex.Companion.DiuIdValidator get() = "\\d{3}-\\d{2}-\\d{4}".toRegex()
val Regex.Companion.NonEmptyValidator get() = ".+".toRegex()
val Regex.Companion.EmptyValidator get() = ".*".toRegex()
val Regex.Companion.NameValidator get() = "[\\S\\s]{3}.+".toRegex()
val Regex.Companion.PasswordValidator get() = "\\S{6}.+".toRegex()
val Regex.Companion.PhoneValidator get() = "\\+?(88)?01[0-9]{9}\\b".toRegex()
val Regex.Companion.OptionalPhoneValidator get() = "^\$|\\+?(88)?01[0-9]{9}\\b".toRegex()