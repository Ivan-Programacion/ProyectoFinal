package com.example.proyectofinal.Model

data class Belt(
    val id: String = "",
    // Ej: mapOf("es" to "Amarillo", "en" to "Yellow")
    val name: Map<String, String> = emptyMap(),
    val order: Int = 0, // Número orden de cinturones. Por defecto el primero (blanco)
)