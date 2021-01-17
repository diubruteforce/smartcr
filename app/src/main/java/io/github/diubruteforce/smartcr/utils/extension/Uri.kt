package io.github.diubruteforce.smartcr.utils.extension

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns

fun Uri.getMimeType(contentResolver: ContentResolver): String? = contentResolver.getType(this)

fun Uri.getName(contentResolver: ContentResolver): String? {
    contentResolver.query(this, null, null, null, null)?.run {
        val nameIndex = getColumnIndex(OpenableColumns.DISPLAY_NAME)

        moveToFirst()

        return getString(nameIndex)
    }

    return null
}

fun Uri.getSize(contentResolver: ContentResolver): Long? {
    contentResolver.query(this, null, null, null, null)?.run {
        val sizeIndex = getColumnIndex(OpenableColumns.SIZE)

        moveToFirst()

        return getLong(sizeIndex)
    }

    return null
}

fun Uri.isUpLoadable(contentResolver: ContentResolver): Boolean {
    val size = getSize(contentResolver) ?: return false

    return size <= 5_242_880
}