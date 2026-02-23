package com.example.proyectofinal.ViewModel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.proyectofinal.Logic.obtenerIndice
import com.example.proyectofinal.Logic.tituloTopBar
import com.example.proyectofinal.View.Favoritos
import com.example.proyectofinal.View.ListaCinturones
import com.example.proyectofinal.View.Login
import com.example.proyectofinal.View.Perfil
import com.example.proyectofinal.ui.theme.ProyectoFinalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Para la pantalal de carga
        val splasScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProyectoFinalTheme(dynamicColor = false) {
                App()
            }
        }
    }
}

@Composable
fun App() {
    // controller --> para poder navegar entre pantallas (remember)
    val controller = rememberNavController()
    // para saber en que pantalla estamos exactamente
    val currentRoute = controller.currentBackStackEntryAsState().value?.destination?.route
    // AÑADIR ESTO PARA LAS TRANSICIONES
    var beforeRoute by remember { mutableStateOf("listaCinturones") }
    Scaffold(
        topBar = {
            if (currentRoute != "login") TopBar(currentRoute)
            else Spacer(Modifier.padding(bottom = 104.dp))
        },
        bottomBar = {
            if (currentRoute != "login") NavBar({
                beforeRoute = it
                controller.navigate(it)
            })
            else Spacer(Modifier.padding(bottom = 104.dp))
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            controller,
            startDestination = "login",
            enterTransition = {
                // Pantalla actual
                val rutaInicial = initialState.destination.route
                // Pantalla destino
                val rutaDestino = targetState.destination.route
                // Las pasamos a la función para obtener su índice
                // login -> -1
                // favoritos -> 0
                // listaCinturones -> 1
                // perfil -> 2
                val inicial = obtenerIndice(rutaInicial)
                val destino = obtenerIndice(rutaDestino)

                // Si es la pantalal de inicio (índice -1) hacemos transicion normal
                if (inicial == -1) {
                    fadeIn(animationSpec = tween(700))
                    // Si no, hacemos transicion horizontal según la posición
                } else {
                    if (destino > inicial) {
                        // AVANCE (Ej: Favoritos -> Cinturones): Entra desde la DERECHA
                        slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(500))
                    } else {
                        // RETROCESO (Ej: Perfil -> Cinturones): Entra desde la IZQUIERDA
                        slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(500))
                    }
                }
            },
            exitTransition = {
                val inicial = obtenerIndice(initialState.destination.route)
                val destino = obtenerIndice(targetState.destination.route)

                if (destino > inicial) {
                    // AVANCE: La pantalla actual sale por la IZQUIERDA para dar con la nueva pantalla
                    slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(500))
                } else {
                    // RETROCESO: La pantalla actual sale por la DERECHA para dar con la nueva pantalla
                    slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(500))
                }
            }
        ) {
            composable(StateNavigate.login.value) { Login(innerPadding) { controller.navigate("listaCinturones") } }
            composable(StateNavigate.listaCinturones.value) { ListaCinturones(innerPadding) }
            composable(StateNavigate.perfil.value) { Perfil(innerPadding) }
            composable(StateNavigate.favoritos.value) { Favoritos(innerPadding) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class) // Está en fase de prueba
@Composable
fun TopBar(currentRoute: String?) {
    TopAppBar(
        {
            Text(
                tituloTopBar(currentRoute),
                style = MaterialTheme.typography.titleMedium
            )
        },
        actions = {
            // REALIZAR LOGICA PARA FAV
            // REALIZAR LOGICA PARA VOLVER ATRÁS EN OTRAS PANTALLAS
        })
}

@Composable
fun NavBar(controller: (route: String) -> Unit) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        var state by remember { mutableStateOf(StateNavigate.listaCinturones) }
        // onClick de cada item --> se identifica la pantalla (state); después se cambia a dicha pantalla (controller([pantalla])
        NavigationBarItem(
            selected = state == StateNavigate.favoritos,
            {
                state = StateNavigate.favoritos
                controller("favoritos")
            },
            icon = { Icon(Icons.Default.Star, contentDescription = "Favoritos") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.onSecondary
            ),
            label = { Text("Favoritos") })
        NavigationBarItem(
            state == StateNavigate.listaCinturones,
            {
                state = StateNavigate.listaCinturones
                controller("listaCinturones")
            },
            { Icon(Icons.Default.MilitaryTech, contentDescription = "Cinturones") },
            // Colores de los items del NavBar
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.onSecondary
            ),
            label = { Text("Cinturones") })
        NavigationBarItem(
            state == StateNavigate.perfil,
            {
                state = StateNavigate.perfil
                controller("perfil")
            },
            { Icon(Icons.Default.AccountCircle, contentDescription = "Perfil") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.onSecondary
            ),
            label = { Text("Perfil") })
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ProyectoFinalTheme {
        App()
    }
}