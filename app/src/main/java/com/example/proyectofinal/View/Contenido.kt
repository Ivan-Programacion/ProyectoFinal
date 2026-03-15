package com.example.proyectofinal.View

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyectofinal.Logic.mapBeltColor
import com.example.proyectofinal.Logic.obtenerIndice
import com.example.proyectofinal.ui.theme.ProyectoFinalTheme

@Composable
fun Contenido(paddingValues: PaddingValues = PaddingValues()) {
    // isFavorite -> Logica provisional para hacer cambiar el icono de favorito de gris a amarillo
    var isFavorite by remember { mutableStateOf(false) }
    // Contenedor principal con el MISMO gradiente que ListaContenido
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        mapBeltColor.getValue("Marrón"), // Mismo color dinámico arriba
                        MaterialTheme.colorScheme.background // Tu color base abajo
                    ),
                    startY = 0f,
                    endY = 500f
                )
            )
    ) {
        // Tarjeta blanca/clara gigante que contiene la info
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp), // Margen exterior para que se vea el fondo
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp), // Padding interno de la tarjeta
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Título de la Técnica / Kata / Set
                    Text(
                        text = "Técnica 1",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    // Si es la pantalla contenido (-5 en función obtenerIndice(route))
                    IconButton(onClick = { isFavorite = !isFavorite }) {
                        if (isFavorite) {
                            // Si ES favorito: Apilamos relleno amarillo + borde marrón
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    modifier = Modifier.size(32.dp), // Tamaño de estrella más grande
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null, // null porque la descripción la lleva el contenedor o el otro icono
                                    tint = mapBeltColor.getValue("Amarillo") // Relleno amarillo
                                )
                                Icon(
                                    modifier = Modifier.size(32.dp), // Tamaño de estrella más grande
                                    imageVector = Icons.Default.StarBorder,
                                    contentDescription = "Desmarcar como favorito",
                                    tint = MaterialTheme.colorScheme.primary // Borde marrón de tu tema
                                )
                            }
                        } else {
                            // Si NO es favorito: Solo mostramos el borde marrón
                            Icon(
                                modifier = Modifier.size(32.dp), // Tamaño de estrella más grande
                                imageVector = Icons.Default.StarBorder,
                                contentDescription = "Marcar como favorito",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                // Descripción
                Text(
                    text = "Descripción completa de la técnica 1, explicando movimientos, detalles y " +
                            "consejos de ejecución. Texto de ejemplo que ayuda a comprender la " +
                            "intención y la mecánica general del movimiento antes de ver el vídeo.",
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Placeholder del Vídeo
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f), // Esto empuja la caja para que ocupe todo el espacio sobrante hasta abajo
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        // Usamos onSecondary para mantener la estética de las tarjetas de ListaContenido
                        containerColor = MaterialTheme.colorScheme.onSecondary
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Video Placeholder",
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Contenidodopreview() {
    ProyectoFinalTheme {
        Contenido()
    }
}