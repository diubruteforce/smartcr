package io.github.diubruteforce.smartcr.utils.extension

import io.github.diubruteforce.smartcr.model.data.Teacher

fun <T> Array<T>.toPairList(): List<Pair<T, T>> {
    require(this.size % 2 == 0) { "List size is not even" }
    val list = mutableListOf<Pair<T, T>>()

    for (i in 0 until this.count() step 2) {
        list.add(Pair(get(i), get(i + 1)))
    }

    return list
}

fun List<Teacher>.filterByQuery(query: String) =
    this.filter {
        it.fullName.contains(query, true) ||
                it.diuEmail.contains(query, true) ||
                it.initial.contains(query, true)
    }.sortedBy { it.fullName }