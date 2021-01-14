package io.github.diubruteforce.smartcr.model.data

import android.webkit.MimeTypeMap
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

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
) : EditableModel() {
    val extension: String? get() = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)

    val fileName: String get() = "$name - ${course.courseCode} ($sectionName).$extension"

    val sizeString: String
        get() {
            val kb = size.toDouble() / 1024

            return if (kb < 1024) "%.2f KB".format(Locale.getDefault(), kb)
            else "%.2f MB".format(Locale.getDefault(), kb / 1024)
        }
}