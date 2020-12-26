package io.github.diubruteforce.smartcr.model.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class Section(
    override val id: String = "",
    override val isActive: Boolean = true,
    override val updaterId: String = "",
    override val updaterEmail: String = "",
    @ServerTimestamp override val updatedOn: Timestamp = Timestamp.now(),

    val name: String = "",
    val course: Course = Course(),
    val instructor: Instructor = Instructor(),
    val googleCode: String = "",
    val blcCode: String = "",
    val courseOutline: String = "",
) : EditableModel()

data class Instructor(
    val id: String = "",
    val name: String = "",
    val designation: String = "",
    val department: String = "",
    val profileUrl: String = ""
)

fun Teacher.toInstructor() = Instructor(
    id = this.id,
    name = this.fullName,
    designation = this.designation,
    department = this.departmentName,
    profileUrl = this.profileUrl
)