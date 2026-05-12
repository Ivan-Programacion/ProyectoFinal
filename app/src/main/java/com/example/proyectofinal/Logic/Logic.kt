package com.example.proyectofinal.Logic

import android.annotation.SuppressLint
import com.example.proyectofinal.ViewModel.ScreenTitle
import com.example.proyectofinal.ViewModel.StateNavigate
import com.example.proyectofinal.ui.theme.coloresCinturones
import java.util.Locale
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

// Función que cambia el titulo del TopBar según la pantalla en la que esté
fun tituloTopBar(screen: String?, beltName: String = ""): String {
    when (screen) {
        StateNavigate.contenido.value -> return if (beltName.isNotEmpty()) "Cinturón: $beltName" else ScreenTitle.contenido.value
        StateNavigate.listaCinturones.value -> return ScreenTitle.listaCinturones.value
        StateNavigate.perfil.value -> return ScreenTitle.perfil.value
        StateNavigate.favoritos.value -> return ScreenTitle.favoritos.value
        StateNavigate.listaContenido.value -> return if (beltName.isNotEmpty()) "Cinturón: $beltName" else ScreenTitle.listaContenido.value
        StateNavigate.adminListaClientes.value -> return ScreenTitle.adminListaClientes.value
        StateNavigate.adminGestionExamen.value -> return ScreenTitle.adminGestionExamen.value
        StateNavigate.adminPerfilCliente.value -> return ScreenTitle.adminPerfilCliente.value
    }
    return ""
}

// Lista de los colores pertenecientes a cada cinturón
val belts = listOf(
    "white" to coloresCinturones().Blanco,
    "yellow" to coloresCinturones().Amarillo,
    "orange" to coloresCinturones().Naranja,
    "purple" to coloresCinturones().Purpura,
    "blue" to coloresCinturones().Azul,
    "green" to coloresCinturones().Verde,
    "brown3" to coloresCinturones().Marron,
    "brown2" to coloresCinturones().Marron,
    "brown1" to coloresCinturones().Marron,
    "black" to coloresCinturones().Negro
)

// Función que, dependiendo de la pantalla donde estemos, nos devolverá un valor con el cual utilizaremos
// para lógica de transiciones de cambio entre pantallas o añadir iconos en el TopBar
fun obtenerIndice (ruta: String?): Int {
    return when (ruta) {
        "adminPerfilCliente" -> -7
        "adminGestionExamen" -> -6
        "contenido" -> -5
        "listaContenido" -> -4
        "registroPass" -> -3
        "registro" -> -2
        "login" -> -1
        "adminListaClientes" -> 0
        "favoritos" -> 1
        "listaCinturones" -> 2
        "perfil" -> 3
        else -> 2 // Si es nulo, asumimos la central para evitar saltos raros
    }
}

// Lista que contiene las pantallas iniciales antes de entrar en la aplicación
val pantallasIniciales = listOf(
    StateNavigate.login.value,
    StateNavigate.registro.value,
    StateNavigate.registroPass.value
)

// Lógica para obtener el nombre del UI dependiendo del locale (i18n)
@SuppressLint("ConstantLocale")
val locale = Locale.getDefault().language

// Función que comprueba si hay conexión a internet en el dispositivo
fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // Obtenemos la red activa actual
    val network = connectivityManager.activeNetwork ?: return false

    // Obtenemos las capacidades de esa red
    val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

    // Comprobamos si la red tiene capacidad de acceso a internet
    return activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}

// Función auxiliar para extraer el ID del video de YouTube a partir de la URL compartida
fun extractYouTubeId(url: String): String? {
    val regex = "v=([^&]+)|youtu\\.be/([^?&]+)|embed/([^?&]+)".toRegex()
    val matchResult = regex.find(url)
    return matchResult?.groupValues?.drop(1)?.firstOrNull { it.isNotEmpty() }
}