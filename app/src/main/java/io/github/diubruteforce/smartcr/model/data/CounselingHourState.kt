package io.github.diubruteforce.smartcr.model.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class CounselingHourState(
    override val id: String = "",
    override val isActive: Boolean = true,
    @ServerTimestamp override val updatedOn: Timestamp = Timestamp.now(),
    override val updaterId: String = "admin",
    override val updaterEmail: String = "admin",
    val startTime: String = "8:00 AM",
    val endTime: String = "11:00 AM",
    val day: String = "Saturday"
) : EditableModel()