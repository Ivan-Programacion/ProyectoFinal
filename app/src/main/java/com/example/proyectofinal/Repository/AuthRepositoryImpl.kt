package com.example.proyectofinal.Repository

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.net.ConnectException
import com.example.proyectofinal.R

class AuthRepositoryImpl(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : AuthRepository {

    override suspend fun register(
        email: String,
        password: String,
        repeatPassword: String,
        message: (String?, Int?) -> Unit
    ): String? {
        return try {
            val isValidPassword =
                password.length >= 8 && password.any { it.isLetter() } && password.any { it.isDigit() }
            if (password != repeatPassword || !isValidPassword) throw IllegalArgumentException()
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.uid
        } catch (e: FirebaseAuthUserCollisionException) {
            e.printStackTrace()
            message(null, R.string.error_register_email_in_use)
            null
        } catch (e: FirebaseNetworkException) {
            e.printStackTrace()
            message(null, R.string.error_connection)
            null
        } catch (e: ConnectException) {
            e.printStackTrace()
            message(null, R.string.error_connection)
            null
        } catch (e: FirebaseAuthException) {
            message(null, R.string.error_register_invalid_password)
            e.printStackTrace()
            null
        } catch (e: IllegalArgumentException) {
            message(null, R.string.error_register_invalid_password)
            e.printStackTrace()
            null
        } catch (e: Exception) {
            message(null, R.string.error_register_generic)
            e.printStackTrace()
            null
        }
    }

    override suspend fun login(
        email: String,
        password: String,
        message: (String?, Int?) -> Unit
    ): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: FirebaseNetworkException) {
            e.printStackTrace()
            message(null, R.string.error_connection)
            false
        } catch (e: ConnectException) {
            e.printStackTrace()
            message(null, R.string.error_connection)
            false
        } catch (e: FirebaseAuthException) {
            e.printStackTrace()
            message(null, R.string.error_login_invalid_credentials)
            false
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            message(null, R.string.error_login_invalid_credentials)
            false
        } catch (e: Exception) {
            e.printStackTrace()
            message(null, R.string.error_login_invalid_credentials)
            false
        }
    }

    override fun getCurrentUserUid(): String? {
        return auth.currentUser?.uid
    }

    override fun logout() {
        auth.signOut()
    }

    override fun getAuthStateStream(): Flow<String?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser?.uid)
        }
        auth.addAuthStateListener(authStateListener)
        awaitClose { auth.removeAuthStateListener(authStateListener) }
    }

    override suspend fun sendPasswordResetEmail(email: String, message: (String?, Int?) -> Unit): Boolean {
        return try {
            auth.sendPasswordResetEmail(email).await()
            true
        } catch (e: FirebaseNetworkException) {
            e.printStackTrace()
            message(null, R.string.error_connection)
            false
        } catch (e: ConnectException) {
            e.printStackTrace()
            message(null, R.string.error_connection)
            false
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            e.printStackTrace()
            message(null, R.string.error_invalid_email)
            false
        } catch (e: FirebaseTooManyRequestsException) {
            e.printStackTrace()
            message(null, R.string.error_too_many_requests)
            false
        } catch (e: Exception) {
            e.printStackTrace()
            message(null, R.string.error_send_email_generic)
            false
        }
    }
}
