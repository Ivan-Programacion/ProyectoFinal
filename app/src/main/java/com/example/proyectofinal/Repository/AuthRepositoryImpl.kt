package com.example.proyectofinal.Repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.tasks.await
import java.net.ConnectException

class AuthRepositoryImpl(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : AuthRepository {

    override suspend fun register(email: String, password: String): String? {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.uid
        } catch (e: FirebaseAuthException) {
            e.printStackTrace()
            null
        } catch (e: Exception) {
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
        } catch (e: ConnectException) {
            e.printStackTrace()
            message("No se pudo conectar con el servidor. Inténtelo más tarde")
            false
        } catch (e: FirebaseAuthException) {
            e.printStackTrace()
            message("Error del servidor. Inténtelo más tarde")
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
}

