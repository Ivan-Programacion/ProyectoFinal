package com.example.proyectofinal.View

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.proyectofinal.Logic.mapBeltColor

@Composable
fun Contenido(paddingValues: PaddingValues = PaddingValues()) {
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
                // Título de la Técnica / Kata / Set
                Text(
                    text = "Técnica 1",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D0C03) // Marrón oscuro coherente con tu app
                )

                // Descripción
                Text(
                    text = "Descripción completa de la técnica 1, explicando movimientos, detalles y " +
                            "consejos de ejecución. Texto de ejemplo que ayuda a comprender la " +
                            "intención y la mecánica general del movimiento antes de ver el vídeo.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f) // Texto un pelín más suave
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
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF2D0C03)
                        )
                    }
                }
            }
        }
    }
}