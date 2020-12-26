package io.github.diubruteforce.smartcr.model.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class Routine(
    override val id: String = "",
    override val isActive: Boolean = true,
    override val updaterId: String = "",
    override val updaterEmail: String = "",
    @ServerTimestamp override val updatedOn: Timestamp = Timestamp.now(),

    val day: String = "",
    val room: String = "",
    val startTime: String = "8:00 AM",
    val endTime: String = "11:00 AM",
    val sectionId: String = ""
) : EditableModel()