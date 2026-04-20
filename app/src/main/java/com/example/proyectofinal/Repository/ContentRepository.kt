package com.example.proyectofinal.Repository

import com.example.proyectofinal.Model.Belt
import com.example.proyectofinal.Model.Center
import com.example.proyectofinal.Model.Content
import kotlinx.coroutines.flow.Flow

interface ContentRepository {
    // Obtiene todos los gimnasios
    suspend fun getCenters(): List<Center>

    // Obtiene todos los cinturones
    suspend fun getBelts(): List<Belt>

    // Obtiene todos los cinturones en tiempo real (Stream) ordenados
    fun getBeltsStream(): Flow<List<Belt>>

    // Obtiene el contenido según lo que nos interesa: técnica, forma o set
    suspend fun getContentByBelt(beltId: String, collectionName: String): List<Content>
}