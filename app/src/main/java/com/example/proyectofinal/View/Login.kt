package com.example.proyectofinal.View

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyectofinal.ViewModel.App
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
        Text("TÍTULO")
        Card {
            Text("Probando", Modifier.padding(8.dp))
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