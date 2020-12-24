package io.github.diubruteforce.smartcr.model.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class Teacher(
    override val id: String = "",
    override val isActive: Boolean = true,
    @ServerTimestamp override val updatedOn: Timestamp = Timestamp.now(),
    override val updaterId: String = "admin",
    override val updaterEmail: String = "admin",
    val fullName: String = "",
    val profileUrl: String = "https://firebasestorage.googleapis.com/v0/b/smartcr-aa8e8.appspot.com/o/profile%2Fc0055a46-df2f-4b6b-a46f-b8ac7865dd20?alt=media&token=5414dacc-2425-4c12-b66f-a2c867967263",
    val initial: String = "",
    val diuEmail: String = "",
    val gender: String = "",
    val phone: String = "",
    val departmentId: String = "",
    val departmentCode: String = "",
    val departmentName: String = "",
    val room: String = "",
    val designation: String = "",
    val link: String = ""
) : EditableModel()