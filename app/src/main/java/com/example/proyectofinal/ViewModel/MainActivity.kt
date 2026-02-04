package com.example.proyectofinal.ViewModel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
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
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        ListaCinturones(innerPadding)
        //Login(innerPadding)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ProyectoFinalTheme {
        App()
    }
}