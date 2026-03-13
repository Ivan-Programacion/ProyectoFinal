package com.example.proyectofinal.ViewModel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.proyectofinal.Logic.obtenerIndice
import com.example.proyectofinal.Logic.pantallasIniciales
import com.example.proyectofinal.Logic.tituloTopBar
import com.example.proyectofinal.View.Favoritos
import com.example.proyectofinal.View.ListaCinturones
import com.example.proyectofinal.View.ListaContenido
import com.example.proyectofinal.View.Login
import com.example.proyectofinal.View.Perfil
import com.example.proyectofinal.View.RegistroInfo
import com.example.proyectofinal.View.RegistroPass
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
            // Si NO estamos navegando antes de entrar en la aplicación por las pantalals login, registro, etc
            // controller.popBackStack() --> Para volver atrás en caso de que la pantalla tenga flecha hacia atrás
            if (currentRoute !in pantallasIniciales) TopBar(currentRoute) { controller.popBackStack() }
            // Si lo estamos, no añadimos el TopBar
            else Spacer(Modifier.padding(bottom = 104.dp))
        },
        bottomBar = {
            // Si NO estamos navegando antes de entrar en la aplicación por las pantalals login, registro, etc
            if (currentRoute !in pantallasIniciales) NavBar({
                beforeRoute = it
                controller.navigate(it)
            })
            // Si lo estamos, no añadimos el NavBar
            else Spacer(Modifier.padding(bottom = 104.dp))
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            controller,
            startDestination = "login",
            enterTransition = {
                // Las pasamos a la función para obtener su índice
                val inicial = obtenerIndice(initialState.destination.route)
                val destino = obtenerIndice(targetState.destination.route)
                // Si la pantalla inicial y la de destino son iguales, no hacemos nada
                if(destino == inicial) {
                    EnterTransition.None

                // Si las pantallas son las correspondientes a antes de iniciar sesión (login, registro, olvido contraseña, etc), hacemos fadeIn
                } else if (destino < 0 || inicial < 0) {
                    fadeIn(animationSpec = tween(100))

                    // Si no, hacemos transicion horizontal según la posición
                } else {
                    if (destino > inicial) {
                        // AVANCE (Ej: Favoritos -> Cinturones): Entra desde la DERECHA
                        slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(400))
                    } else {
                        // RETROCESO (Ej: Perfil -> Cinturones): Entra desde la IZQUIERDA
                        slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(400))
                    }
                }
            },
            exitTransition = {

                val inicial = obtenerIndice(initialState.destination.route)
                val destino = obtenerIndice(targetState.destination.route)
                // Si la pantalla inicial y la de destino son iguales, no hacemos nada
                if(destino == inicial) {
                    ExitTransition.None

                    // Si las pantallas son las correspondientes a antes de iniciar sesión (login, registro, olvido contraseña, etc), hacemos fadeOut
                } else if (inicial < 0 || destino < 0) {
                    fadeOut(animationSpec = tween(100))

                    // Si no, hacemos transicion horizontal según la posición
                } else {
                    if (destino > inicial) {
                        // AVANCE: La pantalla actual sale por la IZQUIERDA para dar con la nueva pantalla
                        slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(400))
                    } else {
                        // RETROCESO: La pantalla actual sale por la DERECHA para dar con la nueva pantalla
                        slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(400))
                    }
                }
            }
        ) {
            composable(StateNavigate.login.value) { Login(innerPadding) { controller.navigate(it) } }
            composable(StateNavigate.registro.value) { RegistroInfo(innerPadding) { controller.navigate(it) } }
            composable(StateNavigate.listaCinturones.value) { ListaCinturones(innerPadding) { controller.navigate(it) } }
            composable(StateNavigate.perfil.value) { Perfil(innerPadding) {controller.navigate(it)} }
            composable(StateNavigate.favoritos.value) { Favoritos(innerPadding) }
            composable(StateNavigate.registroPass.value) { RegistroPass(innerPadding) { controller.navigate(it) } }
            composable(StateNavigate.listaContenido.value) { ListaContenido(innerPadding) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class) // Está en fase de prueba
@Composable
fun TopBar(currentRoute: String?, controller: () -> Unit = {}) {
    TopAppBar(
        {
            Text(
                tituloTopBar(currentRoute),
                style = MaterialTheme.typography.titleMedium
            )
        },
        navigationIcon = {
            // Si es una pantalla secundaría que proviene de una principal:
            /*
            Funciíón "obtenerIndice(route)":
            - Principales de 0 a 3
            - Inciales de -1 a -3
            - Auxiliares de -4 paraa abajo
             */
            if (obtenerIndice(currentRoute) < -3) {
                // Flecha para atrás de navegación
                IconButton(onClick = { controller() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver atrás",
                        tint = Color(0xFF2D0C03) // Marrón oscuro de la identidad de tu App
                    )
                }
            }
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