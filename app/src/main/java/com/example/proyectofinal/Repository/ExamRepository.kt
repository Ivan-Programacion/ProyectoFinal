package com.example.proyectofinal.Repository

import com.example.proyectofinal.Model.Exam

interface ExamRepository {
    // Obtiene la instancia del examen
    suspend fun getExam(): Exam?
}