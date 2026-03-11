package com.example.proyectofinal.View

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyectofinal.Logic.belts
import com.example.proyectofinal.Logic.mapBeltColor
import com.example.proyectofinal.Logic.provisionalContentFormBeltList
import com.example.proyectofinal.Logic.provisionalContentSetBeltList
import com.example.proyectofinal.Logic.provisionalContentTecBeltList
import com.example.proyectofinal.ui.theme.ProyectoFinalTheme

@Composable
fun ListaContenido(paddingValues: PaddingValues = PaddingValues()) {
    // Contenedor principal que usa el color del cinturón como fondo
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            // Probando color de fondo del cinturón
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        mapBeltColor.getValue("Negro"), // Color cinturón
                        MaterialTheme.colorScheme.background // Color de fondo predeterminado
                    ),
                    startY = 0f,
                    endY = 500f // Ajustar esto para ajustar el tamaño del degradado
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
        ) {
            // SECCIÓN TÉCNICAS
            item {
                ContenedorContenido(titulo = "Técnicas", items = provisionalContentTecBeltList)
            }

            // SECCIÓN FORMA (KATA)
            item {
                ContenedorContenido(titulo = "Forma (Kata)", items = provisionalContentFormBeltList)
            }

            // SECCIÓN SET
            item {
                ContenedorContenido(titulo = "Set", items = provisionalContentSetBeltList)
            }
        }
    }
}

@Composable
fun ContenedorContenido(titulo: String, items: List<String>) {
    // Tarjeta clara que agrupa los elementos
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
            )

            // Generamos una fila por cada elemento de la lista
            items.forEach { nombreItem ->
                SubcontenedorContenido(nombre = nombreItem)
            }
        }
    }
}

@Composable
fun SubcontenedorContenido(nombre: String) {
    // Cada técnica individual dentro de la sección
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable { /* Navegar al detalle del contenido */ },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSecondary)
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = nombre,
                fontWeight = FontWeight.Bold,
            )

            // Botón de flecha a la derecha
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.White.copy(alpha = 0.3f),
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ListaContenidopreview() {
    ProyectoFinalTheme {
        ListaContenido()
    }
}