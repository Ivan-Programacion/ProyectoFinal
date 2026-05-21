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

class AuthRepositoryImpl(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : AuthRepository {

    override suspend fun register(
        email: String,
        password: String,
        repeatPassword: String,
        message: (String) -> Unit
    ): String? {
        return try {
            val isValidPassword =
                password.length >= 8 && password.any { it.isLetter() } && password.any { it.isDigit() }
            if (password != repeatPassword || !isValidPassword) throw IllegalArgumentException()
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.uid
        } catch (e: FirebaseAuthUserCollisionException) {
            e.printStackTrace()
            message("Correo electrónico no válido o ya registrado")
            null
        } catch (e: FirebaseNetworkException) {
            e.printStackTrace()
            message("Error de conexión. No se pudo registrar el usuario")
            null
        } catch (e: ConnectException) {
            e.printStackTrace()
            message("Error de conexión. No se pudo registrar el usuario")
            null
        } catch (e: FirebaseAuthException) {
            message("Las contraseñas no coinciden o no es válida")
            e.printStackTrace()
            null
        } catch (e: IllegalArgumentException) {
            message("Las contraseñas no coinciden o no es válida")
            e.printStackTrace()
            null
        } catch (e: Exception) {
            message("Otra cosa")
            e.printStackTrace()
            null
        }
    }

    override suspend fun login(
        email: String,
        password: String,
        message: (String) -> Unit
    ): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: FirebaseNetworkException) {
            e.printStackTrace()
            message("Error de conexión. Inténtelo más tarde")
            false
        } catch (e: ConnectException) {
            e.printStackTrace()
            message("Error de conexión. Inténtelo más tarde")
            false
        } catch (e: FirebaseAuthException) {
            e.printStackTrace()
            message("Usuario o contraseña no válidos")
            false
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            message("Usuario o contraseña no válidos")
            false
        } catch (e: Exception) {
            e.printStackTrace()
            message("Usuario o contraseña no válidos")
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

    override suspend fun sendPasswordResetEmail(email: String, message: (String) -> Unit): Boolean {
        return try {
            auth.sendPasswordResetEmail(email).await()
            true
        } catch (e: FirebaseNetworkException) {
            e.printStackTrace()
            message("Error de conexión. Inténtelo más tarde")
            false
        } catch (e: ConnectException) {
            e.printStackTrace()
            message("Error de conexión. Inténtelo más tarde")
            false
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            e.printStackTrace()
            message("Correo electrónico no válido")
            false
        } catch (e: FirebaseTooManyRequestsException) {
            e.printStackTrace()
            message("Demasiados intentos para restablecer la contraseña. Inténtalo más tarde.")
            false
        } catch (e: Exception) {
            e.printStackTrace()
            message("Error al enviar el correo. Verifique la dirección.")
            false
        }
    }
}
