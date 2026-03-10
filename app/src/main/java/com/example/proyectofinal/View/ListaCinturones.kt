package com.example.proyectofinal.View

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyectofinal.Logic.belts
import com.example.proyectofinal.ViewModel.App
import com.example.proyectofinal.ViewModel.StateNavigate
import com.example.proyectofinal.ui.theme.ProyectoFinalTheme

@Composable
fun ListaCinturones(paddingValues: PaddingValues = PaddingValues(), controller: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues) // Importante para no pisar el Navbar
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                // Añadimos margen lateral y fondo para que "flote"
                .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 12.dp),
            shape = RoundedCornerShape(24.dp), // Redondeado completo para look compacto
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                // Padding interno para que el contenido no toque los bordes de la Card
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp) // Espacio más compacto entre cinturones
            ) {
                items(belts) { (name, color) ->
                    BeltItem(name, color) { controller(StateNavigate.listaContenido.value) }
                }
            }
        }
    }
}

@Composable
fun BeltItem(name: String, beltColor: Color, controller: () -> Unit) {
    // Determinamos el color del texto para que siempre sea legible
    val textColor: Color
    if (name != "Blanco" && name != "Amarillo")
        textColor = MaterialTheme.colorScheme.onPrimary
    else
        textColor = MaterialTheme.colorScheme.primary

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            // HABRA QUE VER EN LÓGICA BACKEND CÓMO PASAR EL CINTURÓN AQUÍ
            .clickable { controller() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = beltColor),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = textColor
            )

            // Pequeño botón de flecha dentro de la subcard
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.White.copy(alpha = 0.3f),
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ListaCinturonespreview() {
    ProyectoFinalTheme {
        App()
    }
}