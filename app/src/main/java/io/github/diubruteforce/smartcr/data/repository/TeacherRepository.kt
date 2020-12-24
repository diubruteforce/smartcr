package io.github.diubruteforce.smartcr.data.repository

import android.net.Uri
import com.google.firebase.Timestamp
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.github.diubruteforce.smartcr.model.data.CounselingHourState
import io.github.diubruteforce.smartcr.model.data.Teacher
import io.github.diubruteforce.smartcr.utils.extension.whereActiveData
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TeacherRepository @Inject constructor(
    private val storageRepository: StorageRepository,
    private val profileRepository: ProfileRepository,
    private val classRepository: ClassRepository
) {
    private val db = Firebase.firestore
    private val teacherProfilePath = "teachers"
    private val historyPath = "history"
    private val counselingPath = "counseling"

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
            db.collection(teacherProfilePath).document(userId).set(userProfile, SetOptions.merge())
                .await()
        } else {
            teacherId = db.collection(teacherProfilePath).add(userProfile).await().id
        }

        return downloadUri.toString()
    }

    suspend fun getTeacherProfile(teacherId: String?): Teacher {
        if (teacherId == null) return Teacher()

        this.teacherId = teacherId

        val response = db.collection(teacherProfilePath).document(teacherId).get().await()

        val teacher = response.toObject(Teacher::class.java)?.copy(id = response.id)

        return teacher ?: Teacher()
    }

    suspend fun canSave(diuEmail: String): Boolean {
        val response = db.collection(teacherProfilePath)
            .whereEqualTo("diuEmail", diuEmail)
            .get()
            .await()


        return if (response.isEmpty) true
        else teacherId == response.first().id
    }

    suspend fun saveTeacherProfile(teacher: Teacher) {
        val teacherId = this.teacherId

        val newTeacher = teacher.copy(
            updaterId = profileRepository.userid,
            updaterEmail = profileRepository.userEmail
        )

        if (teacherId != null) {
            db.collection(teacherProfilePath)
                .document(teacherId)
                .set(newTeacher, SetOptions.merge())
                .await()
        } else {
            this.teacherId = db.collection(teacherProfilePath).add(newTeacher).await().id
        }

        // Keeping the history
        db.collection(teacherProfilePath)
            .document(this.teacherId!!)
            .collection(historyPath)
            .add(newTeacher)
            .await()
    }

    suspend fun deleteTeacherProfile(teacher: Teacher) {
        val newTeacher = teacher.copy(
            isActive = false,
            updaterId = profileRepository.userid,
            updaterEmail = profileRepository.userEmail
        )

        db.collection(teacherProfilePath)
            .document(newTeacher.id)
            .set(newTeacher)
            .await()

        // Keeping the history
        db.collection(teacherProfilePath)
            .document(newTeacher.id)
            .collection(historyPath)
            .add(newTeacher)
            .await()
    }

    suspend fun getCounselingHours(teacherId: String): List<CounselingHourState> {
        val result = db.collection(teacherProfilePath)
            .document(teacherId)
            .collection(counselingPath)
            .whereActiveData()
            .get()
            .await()

        return result.map { it.toObject(CounselingHourState::class.java).copy(id = it.id) }
    }

    suspend fun saveCounselingHour(counselingHour: CounselingHourState) {
        val teacherId = teacherId
        require(teacherId != null)

        val newCounselingHour = counselingHour.copy(
            updaterId = profileRepository.userid,
            updaterEmail = profileRepository.userEmail,
            updatedOn = Timestamp.now()
        )
        var counselingHourId = newCounselingHour.id

        if (counselingHour.id.isEmpty()) {
            val response = db.collection(teacherProfilePath)
                .document(teacherId)
                .collection(counselingPath)
                .add(newCounselingHour)
                .await()

            counselingHourId = response.id
        } else {
            db.collection(teacherProfilePath)
                .document(teacherId)
                .collection(counselingPath)
                .document(newCounselingHour.id)
                .set(newCounselingHour, SetOptions.merge())
                .await()
        }

        // Keeping the history
        db.collection(teacherProfilePath)
            .document(teacherId)
            .collection(counselingPath)
            .document(counselingHourId)
            .collection(historyPath)
            .add(newCounselingHour.copy(id = counselingHourId))

    }

    suspend fun deleteCounselingHour(counselingHour: CounselingHourState) {
        val teacherId = teacherId
        require(teacherId != null)

        val newCounselingHour = counselingHour.copy(isActive = false)

        db.collection(teacherProfilePath)
            .document(teacherId)
            .collection(counselingPath)
            .document(counselingHour.id)
            .set(newCounselingHour)
            .await()

        // Keeping the history
        db.collection(teacherProfilePath)
            .document(teacherId)
            .collection(counselingPath)
            .document(counselingHour.id)
            .collection(historyPath)
            .add(newCounselingHour)
            .await()
    }
}