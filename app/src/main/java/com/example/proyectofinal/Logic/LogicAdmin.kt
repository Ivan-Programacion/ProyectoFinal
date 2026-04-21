package com.example.proyectofinal.Logic

// LOGICA PROVISIONAL PARA PERFILES ALUMNOS EJEMPLO:
// 1. DATA CLASS SIMULADA (Para que puedas probar el buscador)
// Esto irá en tu carpeta Logic o Model más adelante
data class AlumnoEjemplo(val id: Int, val nombre: String, val apellidos: String)

// ENUM PARA SIMULAR LOS ESTADOS DEL EXAMEN (Lógica provisional visual)
enum class EstadoExamen { CLOSED, OPEN_REQUESTS, IN_PROGRESS }

// 1. DATA CLASS SIMULADA
data class AlumnoExamen(
    val id: Int,
    val nombre: String,
    val apellidos: String,
    val cinturon: String
)