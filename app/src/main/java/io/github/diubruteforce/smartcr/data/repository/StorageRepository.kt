package io.github.diubruteforce.smartcr.data.repository

import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

class StorageRepository @Inject constructor() {
    private val storage = Firebase.storage
    private val profileFolder = "profile"
    private val resourceFolder = "resource"

    suspend fun uploadProfileImage(filePath: Uri): Uri {
        val imageName = "${UUID.randomUUID()}"

        val reference = storage.reference.child("$profileFolder/$imageName")

        reference.putFile(filePath).await()

        return reference.downloadUrl.await()
    }
}