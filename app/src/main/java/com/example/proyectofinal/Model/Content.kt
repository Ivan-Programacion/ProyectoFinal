package com.example.proyectofinal.Model

data class Content(
    val id: String = "",
    val number: Int = 0,
    val name: Map<String, String> = emptyMap(), // LocalizedString {es, en}
    val description: Map<String, String> = emptyMap(), // LocalizedString {es, en}
    val url: String = "",
    val beltId: String = "" // ID del cinturón al que pertenece (ej: "belt_yellow")
)