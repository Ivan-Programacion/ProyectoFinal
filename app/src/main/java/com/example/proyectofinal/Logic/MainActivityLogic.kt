package com.example.proyectofinal.Logic

import com.example.proyectofinal.ViewModel.ScreenTitle
import com.example.proyectofinal.ViewModel.StateNavigate

fun tituloTopBar(screen: String?): String {
    when (screen) {
        StateNavigate.listaCinturones.value -> return ScreenTitle.listaCinturones.value
        StateNavigate.perfil.value -> return ScreenTitle.perfil.value
        StateNavigate.favoritos.value -> return ScreenTitle.favoritos.value
    }
    return ""
}