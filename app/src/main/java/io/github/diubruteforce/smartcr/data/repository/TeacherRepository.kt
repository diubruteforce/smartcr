package io.github.diubruteforce.smartcr.data.repository

import android.net.Uri
import com.google.firebase.Timestamp
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.github.diubruteforce.smartcr.model.data.Teacher
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TeacherRepository @Inject constructor(
    private val storageRepository: StorageRepository,
    private val profileRepository: ProfileRepository,
    private val classRepository: ClassRepository
) {
    private val db = Firebase.firestore
    private val profilePath = "teachers"
    private val historyPath = "history"

    private var teacherId: String? = null

    suspend fun getAllDepartment() = classRepository.getAllDepartment()

    /*
    * TODO: If a user just add an image but don't add
    *  other description this would create an exception
    * */
    suspend fun uploadProfileImage(imagePath: Uri): String {
        val downloadUri = storageRepository.uploadProfileImage(imagePath)

        val userProfile = mapOf(
            "profileUrl" to downloadUri.toString(),
            "imageUpdatedOn" to Timestamp.now(),
            "imageUpdaterId" to profileRepository.userid,
            "imageUpdaterEmail" to profileRepository.userEmail
        )

        val userId = teacherId

        if (userId != null) {
            db.collection(profilePath).document(userId).set(userProfile, SetOptions.merge()).await()
        } else {
            teacherId = db.collection(profilePath).add(userProfile).await().id
        }

        return downloadUri.toString()
    }

    suspend fun getTeacherProfile(teacherId: String?): Teacher {
        if (teacherId == null) return Teacher()

        this.teacherId = teacherId

        val response = db.collection(profilePath).document(teacherId).get().await()

        val teacher = response.toObject(Teacher::class.java)?.copy(id = response.id)

        return teacher ?: Teacher()
    }

    suspend fun saveTeacherProfile(teacher: Teacher) {
        val teacherId = this.teacherId

        val newTeacher = teacher.copy(
            updaterId = profileRepository.userid,
            updaterEmail = profileRepository.userEmail
        )

        if (teacherId != null) {
            db.collection(profilePath).document(teacherId).set(newTeacher, SetOptions.merge())
                .await()
        } else {
            this.teacherId = db.collection(profilePath).add(newTeacher).await().id
        }

        db.collection(profilePath)
            .document(this.teacherId!!)
            .collection(historyPath)
            .add(newTeacher)
            .await()
    }
}