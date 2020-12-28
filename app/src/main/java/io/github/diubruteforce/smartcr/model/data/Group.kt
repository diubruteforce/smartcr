package io.github.diubruteforce.smartcr.model.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class Group(
    val name: String = "",
    val detail: String = "",
    val members: List<String> = emptyList(),

    override val id: String = "",
    override val isActive: Boolean = true,
    override val updaterId: String = "",
    override val updaterEmail: String = "",
    @ServerTimestamp override val updatedOn: Timestamp = Timestamp.now()
) : EditableModel()