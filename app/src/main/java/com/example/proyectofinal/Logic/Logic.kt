package com.example.proyectofinal.Logic

import com.example.proyectofinal.ViewModel.ScreenTitle
import com.example.proyectofinal.ViewModel.StateNavigate
import com.example.proyectofinal.ui.theme.coloresCinturones

// Función que cambia el titulo del TopBar según la pantalla en la que esté
fun tituloTopBar(screen: String?): String {
    when (screen) {
        StateNavigate.listaCinturones.value -> return ScreenTitle.listaCinturones.value
        StateNavigate.perfil.value -> return ScreenTitle.perfil.value
        StateNavigate.favoritos.value -> return ScreenTitle.favoritos.value
    }
    return ""
}

// Lista de los colores pertenecientes a cada cinturón
val belts = listOf(
    "Blanco" to coloresCinturones().Blanco,
    "Amarillo" to coloresCinturones().Amarillo,
    "Naranja" to coloresCinturones().Naranja,
    "Púrpura" to coloresCinturones().Purpura,
    "Azul" to coloresCinturones().Azul,
    "Verde" to coloresCinturones().Verde,
    "Marrón" to coloresCinturones().Marron,
    "Negro" to coloresCinturones().Negro
)

// Función que, dependiendo de la pantalla donde estemos, realizará una transición u otra al cambiar de pantalla
fun obtenerIndice (ruta: String?): Int {
    return when (ruta) {
        "listaContenido" -> -4
        "registroPass" -> -3
        "registro" -> -2
        "login" -> -1
        "favoritos" -> 0
        "listaCinturones" -> 1
        "perfil" -> 2
        else -> 1 // Si es nulo, asumimos la central para evitar saltos raros
    }
}

// Lista que contiene las pantallas iniciales antes de entrar en la aplicación
val pantallasIniciales = listOf(
    StateNavigate.login.value,
    StateNavigate.registro.value,
    StateNavigate.registroPass.value
)