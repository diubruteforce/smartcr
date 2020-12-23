package io.github.diubruteforce.smartcr.data.repository

import android.net.Uri
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.github.diubruteforce.smartcr.model.data.Student
import io.github.diubruteforce.smartcr.utils.extension.DiuEmailValidator
import io.github.diubruteforce.smartcr.utils.extension.DiuIdValidator
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val storageRepository: StorageRepository,
    private val authRepository: AuthRepository,
    private val classRepository: ClassRepository
) {
    private val db = Firebase.firestore
    private val profilePath = "students"

    suspend fun uploadProfileImage(imagePath: Uri): String {
        val downloadUri = storageRepository.uploadProfileImage(imagePath)

        val userId = authRepository.userid

        val userProfile = mapOf(
            "profileUrl" to downloadUri.toString()
        )

        db.collection(profilePath).document(userId).set(userProfile, SetOptions.merge()).await()

        return downloadUri.toString()
    }

    suspend fun getUserProfile(): Student {
        val response = db.collection(profilePath).document(authRepository.userid).get().await()

        val student = response.toObject(Student::class.java)?.copy(id = response.id)
        val diuEmail = authRepository.userEmail

        return student?.copy(diuEmail = diuEmail) ?: Student(diuEmail = diuEmail)
    }

    suspend fun saveUserProfile(student: Student) {
        db.collection(profilePath)
            .document(authRepository.userid)
            .set(student.copy(id = authRepository.userid), SetOptions.merge())
            .await()
    }

    suspend fun getAllDepartment() = classRepository.getAllDepartment()

    suspend fun hasProfileData(): Boolean {
        val response = db.collection(profilePath).document(authRepository.userid).get().await()

        val student = response.toObject(Student::class.java)?.copy(id = response.id)

        return if (student == null) false
        else {
            Regex.DiuEmailValidator.matches(student.diuEmail) ||
                    Regex.DiuIdValidator.matches(student.diuId)
        }
    }
}