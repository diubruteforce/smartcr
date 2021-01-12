package io.github.diubruteforce.smartcr.model.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class Resource(
    val course: Course = Course(),
    val instructor: Instructor = Instructor(),
    val sectionId: String = "",
    val sectionName: String = "",
    val path: String = "", // storage download path
    val name: String = "",
    val mimeType: String = "",
    val size: Long = -1,
    val uploadedBy: String = "",

    override val id: String = "",
    override val isActive: Boolean = true,
    override val updaterId: String = "",
    override val updaterEmail: String = "",
    @ServerTimestamp override val updatedOn: Timestamp = Timestamp.now()
) : EditableModel()