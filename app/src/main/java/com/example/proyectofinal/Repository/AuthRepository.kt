package com.example.proyectofinal.Repository

interface AuthRepository {
    // Registra al usuario y devuelve su UID único de Firebase si tiene éxito. Si falla, devuelve null.
    suspend fun register(email: String, password: String): String?

    // Intenta iniciar sesión y devuelve true si es correcto.
    suspend fun login(email: String, password: String): Boolean

    // Devuelve el UID del usuario actual si está logueado, o null si no hay sesión activa.
    fun getCurrentUserUid(): String?

    // Cierra la sesión de Firebase Auth.
    fun logout()
}
