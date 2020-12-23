package io.github.diubruteforce.smartcr.utils.extension

fun <T> Array<T>.toPairList(): List<Pair<T, T>> {
    require(this.size % 2 == 0) { "List size is not even" }
    val list = mutableListOf<Pair<T, T>>()

    for (i in 0 until this.count() step 2) {
        list.add(Pair(get(i), get(i + 1)))
    }

    return list
}