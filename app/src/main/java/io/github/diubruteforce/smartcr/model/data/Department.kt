package io.github.diubruteforce.smartcr.model.data

data class Department(
    val id: String = "",
    val codeName: String = "",
    val name: String = "",
    val faculty: String = "",
    val facultyCode: String = ""
) {
    override fun toString(): String = name
}