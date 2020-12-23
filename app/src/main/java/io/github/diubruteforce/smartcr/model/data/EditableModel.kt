package io.github.diubruteforce.smartcr.model.data

import com.google.firebase.Timestamp

abstract class EditableModel {
    abstract val id: String
    abstract val isActive: Boolean
    abstract val updatedOn: Timestamp
    abstract val updaterId: String
    abstract val updaterEmail: String
}