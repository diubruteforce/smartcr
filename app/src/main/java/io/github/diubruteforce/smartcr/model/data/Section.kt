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
) : EditableModel() {
    override fun toString(): String {
        return "${course.courseCode} (${name})"
    }
}

data class Instructor(
    val id: String = "",
    val fullName: String = "",
    val initial: String = "",
    val designation: String = "",
    val department: String = "",
    val departmentCode: String = "",
    val profileUrl: String = "",
)

fun Teacher.toInstructor() = Instructor(
    id = this.id,
    fullName = this.fullName,
    initial = this.initial,
    designation = this.designation,
    department = this.departmentName,
    departmentCode = this.departmentCode,
    profileUrl = this.profileUrl
)