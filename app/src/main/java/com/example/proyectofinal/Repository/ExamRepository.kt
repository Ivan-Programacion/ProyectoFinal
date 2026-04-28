package com.example.proyectofinal.Repository

import com.example.proyectofinal.Model.Exam
import kotlinx.coroutines.flow.Flow

interface ExamRepository {
    // Obtiene la instancia del examen
    suspend fun getExam(centerId: String): Exam?
    fun observeExam(centerId: String): Flow<Exam?>
    suspend fun updateExamStatus(centerId: String, currentStatus: String)
}