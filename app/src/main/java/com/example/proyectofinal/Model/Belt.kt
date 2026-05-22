package com.example.proyectofinal.Model

data class Belt(
    val id: String = "",
    val name: Map<String, String> = emptyMap(), // Según idioma {es, en}
    val order: Int = 0, // Número orden de cinturones. Por defecto el primero (blanco)
)