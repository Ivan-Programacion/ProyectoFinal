package com.example.proyectofinal.Logic

import com.example.proyectofinal.ViewModel.ScreenTitle
import com.example.proyectofinal.ViewModel.StateNavigate
import com.example.proyectofinal.ui.theme.coloresCinturones

fun tituloTopBar(screen: String?): String {
    when (screen) {
        StateNavigate.listaCinturones.value -> return ScreenTitle.listaCinturones.value
        StateNavigate.perfil.value -> return ScreenTitle.perfil.value
        StateNavigate.favoritos.value -> return ScreenTitle.favoritos.value
    }
    return ""
}

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