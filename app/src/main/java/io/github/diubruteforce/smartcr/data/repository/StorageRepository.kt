package io.github.diubruteforce.smartcr.data.repository

import android.annotation.TargetApi
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.diubruteforce.smartcr.model.data.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val storage = Firebase.storage
    private val profileFolder = "profile"
    private val resourceFolder = "resource"

    suspend fun uploadProfileImage(filePath: Uri): Uri {
        val imageName = "${UUID.randomUUID()}"

        val reference = storage.reference.child("$profileFolder/$imageName")

        reference.putFile(filePath).await()

        return reference.downloadUrl.await()
    }

    suspend fun listTitles() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            listTitlesQ()
        } else {
            listTitlesLegacy()
        }


    @TargetApi(29)
    private suspend fun listTitlesQ(): List<String> {
        val projection = arrayOf(MediaStore.Downloads.TITLE)
        val sortOrder = MediaStore.Downloads.DATE_ADDED

        return withContext(Dispatchers.IO) {
            val resolver = context.contentResolver

            resolver.query(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
            )?.use { cursor ->
                cursor.mapToList { it.getString(0) }
            } ?: emptyList()
        }
    }

    @Suppress("DEPRECATION")
    private suspend fun listTitlesLegacy(): List<String> = withContext(Dispatchers.IO) {
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            ?.listFiles()
            ?.map { it.name } ?: emptyList()
    }

    suspend fun uploadResource(resource: Resource, uri: Uri) {
        val reference = storage.reference.child("$resourceFolder/${resource.path}")

        reference.putFile(uri).await()
    }

    suspend fun download(resource: Resource) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            downloadQ(resource)
        } else {
            downloadLegacy(resource)
        }
    }

    @TargetApi(29)
    private suspend fun downloadQ(
        resource: Resource
    ) = withContext(Dispatchers.IO) {
        val reference = storage.reference.child("$resourceFolder/${resource.path}")

        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, resource.fileName)
            put(MediaStore.Downloads.MIME_TYPE, resource.mimeType)
            put(MediaStore.Downloads.IS_PENDING, 1)
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)

        uri?.let {
            reference.getFile(uri).await()

            values.clear()
            values.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(uri, values, null, null)
        } ?: throw RuntimeException("MediaStore failed for some reason")
    }

    @Suppress("DEPRECATION")
    private suspend fun downloadLegacy(
        resource: Resource
    ) = withContext(Dispatchers.IO) {
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            resource.fileName
        )

        val reference = storage.reference.child("$resourceFolder/${resource.path}")

        reference.getFile(file).await()

        MediaScannerConnection.scanFile(
            context,
            arrayOf(file.absolutePath),
            arrayOf(resource.mimeType),
            null
        )
    }
}

private fun <T : Any> Cursor.mapToList(predicate: (Cursor) -> T): List<T> =
    generateSequence { if (moveToNext()) predicate(this) else null }
        .toList()