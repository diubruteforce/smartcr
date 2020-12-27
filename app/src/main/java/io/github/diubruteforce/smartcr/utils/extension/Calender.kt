package io.github.diubruteforce.smartcr.utils.extension

import android.text.format.DateFormat
import java.text.SimpleDateFormat
import java.util.*

private val dayFormatter by lazy { SimpleDateFormat("EEEE", Locale.getDefault()) }
private val dateFormatter by lazy { SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()) }
private val timeFormatter by lazy {
    SimpleDateFormat(
        "MMMM dd, yyyy hh:mm aa",
        Locale.getDefault()
    )
}

fun Calendar.toDay(): String = dayFormatter.format(this.time).capitalize(Locale.getDefault())
fun Calendar.toDateString(): String = dateFormatter.format(this.time)

fun Calendar.toDateStringWeek(): String {
    val dayOfTheWeek = DateFormat.format("EEE", this) as String // Thu
    val day = DateFormat.format("dd", this) as String // 20
    val monthString = DateFormat.format("MMM", this) as String // Jun
    val year = DateFormat.format("yyyy", this) as String

    return "$dayOfTheWeek, $monthString $day, $year"
}

fun String.toDateTimeMillis(): Long = timeFormatter.parse(this)!!.time

fun String.toCalender(): Calendar {
    val calender = Calendar.getInstance(Locale.getDefault())
    calender.time = dateFormatter.parse(this)!!

    return calender
}