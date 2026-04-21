package com.example.proyectofinal.Repository

import com.example.proyectofinal.Model.Center
import com.example.proyectofinal.Model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    // Obtiene un usuario por su ID. Devuelve el User, o null si falla o no existe.
    suspend fun getUser(userId: String): User?

    // Obtiene un usuario en tiempo real (Stream)
    fun getUserStream(userId: String): Flow<User?>

    // Obtiene todos los usuarios
    suspend fun getAllUsers(): List<User>

    // Crea un nuevo usuario en la base de datos (Ej: al registrarse). Devuelve true si hay éxito.
    suspend fun createUser(user: User): Boolean

    // Actualiza los datos de un usuario existente. Devuelve true si hay éxito.
    suspend fun updateUser(user: User): Boolean

    // Eliminar un usuario por ID
    suspend fun deleteUser(userId: String): Boolean

    // Obtener todos los centros disponibles
    suspend fun getCenters(): List<Center>

    // Obtener los profesores de un centro específico
    suspend fun getTeachersByCenter(centerId: String): List<User>
}