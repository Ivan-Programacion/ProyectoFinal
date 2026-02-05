package com.example.proyectofinal.ViewModel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.example.proyectofinal.View.ListaCinturones
import com.example.proyectofinal.View.Login
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
        bottomBar = {
            if (currentRoute != "login") NavBar({
                beforeRoute = it
                controller.navigate(it)
            })
            else Spacer(Modifier.padding(bottom = 104.dp))
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(controller, startDestination = "login") {
            composable("login") { Login(innerPadding) { controller.navigate("listaCinturones") } }
            composable("listaCinturones") { ListaCinturones(innerPadding) }
        }
    }
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