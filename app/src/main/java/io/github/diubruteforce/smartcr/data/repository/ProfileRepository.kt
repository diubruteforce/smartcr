package io.github.diubruteforce.smartcr.data.repository

import android.net.Uri
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Reusable
import io.github.diubruteforce.smartcr.model.data.Student
import io.github.diubruteforce.smartcr.utils.extension.DiuEmailValidator
import io.github.diubruteforce.smartcr.utils.extension.DiuIdValidator
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@Reusable
class ProfileRepository @Inject constructor(
    private val storageRepository: StorageRepository,
    private val classRepository: ClassRepository
) {
    private val firebaseAuth = Firebase.auth
    private val user get() = firebaseAuth.currentUser

    private val db = Firebase.firestore
    private val profilePath = "students"

    val isAuthenticated: Boolean get() = user != null
    val userEmail: String get() = user?.email ?: ""
    val isEmailVerified: Boolean get() = user?.isEmailVerified ?: false
    val userid: String get() = user?.uid ?: ""

    suspend fun sendVerificationEmail(): Void? =
        user!!.sendEmailVerification().await()

    suspend fun requestPasswordReset(email: String): Void? =
        firebaseAuth.sendPasswordResetEmail(email).await()

    suspend fun createNewUser(email: String, password: String): AuthResult {
        val response = firebaseAuth.createUserWithEmailAndPassword(email, password).await()

        response.user!!.sendEmailVerification().await()

        return response
    }

    suspend fun signIn(email: String, password: String): AuthResult =
        firebaseAuth.signInWithEmailAndPassword(email, password).await()

    fun signOut(): Unit = firebaseAuth.signOut()

    suspend fun uploadProfileImage(imagePath: Uri): String {
        val downloadUri = storageRepository.uploadProfileImage(imagePath)

        val userProfile = mapOf(
            "profileUrl" to downloadUri.toString()
        )

        db.collection(profilePath).document(userid).set(userProfile, SetOptions.merge()).await()

        return downloadUri.toString()
    }

    suspend fun getUserProfile(): Student {
        val response = db.collection(profilePath).document(userid).get().await()

        val student = response.toObject(Student::class.java)?.copy(id = response.id)

        return student?.copy(diuEmail = userEmail) ?: Student(diuEmail = userEmail)
    }

    suspend fun saveUserProfile(student: Student) {
        db.collection(profilePath)
            .document(userid)
            .set(student.copy(id = userid), SetOptions.merge())
            .await()
    }

    suspend fun getAllDepartment() = classRepository.getAllDepartment()

    suspend fun hasProfileData(): Boolean {
        val response = db.collection(profilePath).document(userid).get().await()

        val student = response.toObject(Student::class.java)?.copy(id = response.id)

        return if (student == null) false
        else {
            Regex.DiuEmailValidator.matches(student.diuEmail) ||
                    Regex.DiuIdValidator.matches(student.diuId)
        }
    }

    suspend fun deleteProfile() {
        firebaseAuth.currentUser?.delete()?.await()
    }
}