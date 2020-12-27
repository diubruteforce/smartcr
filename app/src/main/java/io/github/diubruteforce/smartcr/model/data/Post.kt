package io.github.diubruteforce.smartcr.model.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

sealed class Post : EditableModel() {
    abstract val postType: String
    abstract val number: Int
    abstract val date: String
    abstract val dateTimeMillis: Long
    abstract val time: String
    abstract val details: String
    abstract val sectionId: String
    abstract val sectionName: String
    abstract val courseCode: String
    abstract val courseName: String
    abstract val place: String
}

enum class PostType {
    Quiz, Assignment, Presentation, Project, Routine
}

enum class TaskType {
    Group, Single
}

data class Quiz(
    val syllabus: String = "",

    override val postType: String = PostType.Quiz.name,
    override val number: Int = -1,
    override val date: String = "January 25, 2021",
    override val dateTimeMillis: Long = -1,
    override val time: String = "08:00 AM",
    override val details: String = "",
    override val sectionId: String = "",
    override val sectionName: String = "",
    override val courseCode: String = "",
    override val courseName: String = "",
    override val place: String = "",

    override val id: String = "",
    override val isActive: Boolean = true,
    override val updaterId: String = "",
    override val updaterEmail: String = "",
    @ServerTimestamp override val updatedOn: Timestamp = Timestamp.now()
) : Post()

data class Assignment(
    val type: String = TaskType.Single.name,
    val maxMember: Int = 1,

    override val postType: String = PostType.Assignment.name,
    override val number: Int = -1,
    override val date: String = "January 25, 2021",
    override val dateTimeMillis: Long = -1,
    override val time: String = "08:00 AM",
    override val details: String = "",
    override val sectionId: String = "",
    override val sectionName: String = "",
    override val courseCode: String = "",
    override val courseName: String = "",
    override val place: String = "",

    override val id: String = "",
    override val isActive: Boolean = true,
    override val updaterId: String = "",
    override val updaterEmail: String = "",
    @ServerTimestamp override val updatedOn: Timestamp = Timestamp.now()
) : Post()

data class Presentation(
    val type: String = TaskType.Single.name,
    val maxMember: Int = 1,

    override val postType: String = PostType.Presentation.name,
    override val number: Int = -1,
    override val date: String = "January 25, 2021",
    override val dateTimeMillis: Long = -1,
    override val time: String = "08:00 AM",
    override val details: String = "",
    override val sectionId: String = "",
    override val sectionName: String = "",
    override val courseCode: String = "",
    override val courseName: String = "",
    override val place: String = "",

    override val id: String = "",
    override val isActive: Boolean = true,
    override val updaterId: String = "",
    override val updaterEmail: String = "",
    @ServerTimestamp override val updatedOn: Timestamp = Timestamp.now()
) : Post()

data class Project(
    val type: String = TaskType.Single.name,
    val maxMember: Int = 1,

    override val postType: String = PostType.Project.name,
    override val number: Int = -1,
    override val date: String = "January 25, 2021",
    override val dateTimeMillis: Long = -1,
    override val time: String = "08:00 AM",
    override val details: String = "",
    override val sectionId: String = "",
    override val sectionName: String = "",
    override val courseCode: String = "",
    override val courseName: String = "",
    override val place: String = "",

    override val id: String = "",
    override val isActive: Boolean = true,
    override val updaterId: String = "",
    override val updaterEmail: String = "",
    @ServerTimestamp override val updatedOn: Timestamp = Timestamp.now()
) : Post()