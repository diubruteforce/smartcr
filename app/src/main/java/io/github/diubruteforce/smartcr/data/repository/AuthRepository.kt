package io.github.diubruteforce.smartcr.data.repository

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.Reusable
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@Reusable
class AuthRepository @Inject constructor() {
    private val firebaseAuth = Firebase.auth
    private val user get() = firebaseAuth.currentUser

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
}