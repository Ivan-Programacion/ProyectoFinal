package com.example.proyectofinal.View

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyectofinal.ViewModel.App
import com.example.proyectofinal.ui.theme.CardsRosa
import com.example.proyectofinal.ui.theme.ProyectoFinalTheme

@Composable
fun Login(paddingValues: PaddingValues = PaddingValues()) {
    Column(
        Modifier
            .padding(paddingValues)
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Título g y u", style = MaterialTheme.typography.headlineLarge)
        ElevatedCard(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
        Text("Usuario g y u j", Modifier.padding(16.dp))
        OutlinedTextField(modifier = Modifier.padding(16.dp), value = "Hola", onValueChange = {})
        Text("Contraseña g y", Modifier.padding(16.dp))
        OutlinedTextField(modifier = Modifier.padding(16.dp), value = "Hola", onValueChange = {})
    }

        Button({}) { Text("Login") }
    }
}

@Preview(showBackground = true)
@Composable
fun Loginpewview() {
    ProyectoFinalTheme {
        App()
    }
}