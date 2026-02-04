package com.example.proyectofinal.View

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyectofinal.ViewModel.App
import com.example.proyectofinal.ui.theme.ProyectoFinalTheme
import com.example.proyectofinal.ui.theme.coloresCinturones

@Composable
fun ListaCinturones(paddingValues: PaddingValues = PaddingValues()) {
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF8D342A))
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
            )
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                // Padding interno para que el contenido no toque los bordes de la Card
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp) // Espacio más compacto entre cinturones
            ) {
                item {
                    Text(
                        text = "Cinturones",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                    )
                }

                items(belts) { (name, color) ->
                    BeltItem(name = name, beltColor = color)
                }
            }
        }
    }
}

@Composable
fun BeltItem(name: String, beltColor: Color) {
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
            .clickable { /* Navegar al detalle */ },
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