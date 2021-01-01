package io.github.diubruteforce.smartcr.model.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class ExamRoutine(
    val courseId: String = "",
    val courseCode: String = "",
    val courseName: String = "",
    val date: String = "",
    val time: String = "08:00 AM",
    val dateTimeMillis: Long = -1,

    override val id: String = "",
    override val isActive: Boolean = true,
    override val updaterId: String = "",
    override val updaterEmail: String = "",
    @ServerTimestamp override val updatedOn: Timestamp = Timestamp.now(),
) : EditableModel()