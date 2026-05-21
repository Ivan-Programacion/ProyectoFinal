package com.example.proyectofinal.Repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    // Registra al usuario y devuelve su UID único de Firebase si tiene éxito. Si falla, devuelve null.
    suspend fun register(email: String, password: String, repeatPassword: String, message: (String) -> Unit): String?

    // Intenta iniciar sesión y devuelve true si es correcto.
    suspend fun login(email: String, password: String, message: (String) -> Unit): Boolean

    // Devuelve el UID del usuario actual si está logueado, o null si no hay sesión activa.
    fun getCurrentUserUid(): String?

    // Cierra la sesión de Firebase Auth.
    fun logout()

    // Emite el UID actual cada vez que cambia el estado de la sesión
    fun getAuthStateStream(): Flow<String?>

    // Enviar correo de recuperación de contraseña
    suspend fun sendPasswordResetEmail(email: String, message: (String) -> Unit): Boolean
}
