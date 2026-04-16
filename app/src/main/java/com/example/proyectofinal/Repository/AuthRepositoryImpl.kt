package com.example.proyectofinal.Repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
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
        } catch (e: ConnectException) {
            e.printStackTrace()
            message("Error de conexión. Inténtelo más tarde")
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
}
