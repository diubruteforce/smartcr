package io.github.diubruteforce.smartcr.model.data

data class Student(
    val id: String = "",
    val fullName: String = "",
    val profileUrl: String = "https://firebasestorage.googleapis.com/v0/b/smartcr-aa8e8.appspot.com/o/profile%2Fc0055a46-df2f-4b6b-a46f-b8ac7865dd20?alt=media&token=5414dacc-2425-4c12-b66f-a2c867967263",
    val diuId: String = "",
    val diuEmail: String = "",
    val gender: String = "",
    val phone: String = "",
    val departmentId: String = "",
    val departmentCode: String = "",
    val departmentName: String = "",
    val level: String = "",
    val term: String = "",
    val batch: String = "",
    val joinedSection: List<String> = emptyList()
)