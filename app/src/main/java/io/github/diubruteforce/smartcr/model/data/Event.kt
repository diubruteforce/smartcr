package io.github.diubruteforce.smartcr.model.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class Event(
    val title: String = "",
    val type: String = "",
    val date: String = "",
    val time: String = "08:00 AM",
    val dateTimeMillis: Long = -1,
    val place: String = "",
    val details: String = "",

    override val id: String = "",
    override val isActive: Boolean = true,
    override val updaterId: String = "",
    override val updaterEmail: String = "",
    @ServerTimestamp override val updatedOn: Timestamp = Timestamp.now(),
) : EditableModel()

enum class EventType {
    Job, Club, Seminar, Workshop, Other
}