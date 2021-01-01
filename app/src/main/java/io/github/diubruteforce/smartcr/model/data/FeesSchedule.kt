package io.github.diubruteforce.smartcr.model.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class FeesSchedule(
    val batchCode: String = "",
    val lastDate: String = "",
    val feesFor: String = "",
    val dateTimeMillis: Long = -1,

    override val id: String = "",
    override val isActive: Boolean = true,
    override val updaterId: String = "",
    override val updaterEmail: String = "",
    @ServerTimestamp override val updatedOn: Timestamp = Timestamp.now()
) : EditableModel()

enum class FeesReason(val title: String) {
    Registration("Registration"),
    MidTerm("Mid Term Exam"),
    FinalTerm("Final Term Exam")
}