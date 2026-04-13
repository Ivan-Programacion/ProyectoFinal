package com.example.proyectofinal.Model

// Entidad que maneja el estado del examen
data class GlobalConfig(
    val id: String = "current_exam",
    val currentStatus: String = "CLOSED" // "CLOSED", "OPEN_REQUESTS", "IN_PROGRESS"
)