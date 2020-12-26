package io.github.diubruteforce.smartcr.model.data

data class Student(
    val id: String = "",
    val fcmId: String = "",
    val fullName: String = "",
    val profileUrl: String = "https://firebasestorage.googleapis.com/v0/b/smartcr-aa8e8.appspot.com/o/profile%2FMask%20Group%2018.jpg?alt=media&token=9ed3136c-5221-4c56-bd03-ae2d42f1a9d8",
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

data class MemberStudent(
    val studentId: String = "",
    val fcmId: String = "",
    val diuId: String = "",
    val fullName: String = "",
    val profileUrl: String = ""
)

fun Student.toMemberStudent() = MemberStudent(
    studentId = id,
    fcmId = fcmId,
    diuId = diuId,
    fullName = fullName,
    profileUrl = profileUrl
)