package com.example.proyectofinal.View

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import com.example.proyectofinal.Logic.belts
import com.example.proyectofinal.ui.theme.coloresCinturones

@Composable
fun Favoritos(paddingValues: PaddingValues = PaddingValues()) {
    // Datos ejemplos que luego vendrán de la API
    val tecnicasFavoritas = listOf(
        "Técnica 3" to coloresCinturones().Amarillo,
        "Técnica 1" to coloresCinturones().Marron
    )
    val formasFavoritas = listOf(
        "Forma cinturón" to coloresCinturones().Azul
    )
    val setsFavoritos = listOf(
        "Set cinturón" to coloresCinturones().Verde
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // SECCIÓN TÉCNICAS
        item {
            SeccionFavoritos(
                titulo = "Técnicas",
                items = tecnicasFavoritas
            )
        }

        // SECCIÓN FORMA (KATA)
        item {
            SeccionFavoritos(
                titulo = "Forma (Kata)",
                items = formasFavoritas
            )
        }

        // SECCIÓN SET
        item {
            SeccionFavoritos(
                titulo = "Set",
                items = setsFavoritos
            )
        }
    }
}

@Composable
// En el parametro esta puesto andropidx.compuse.ui.graphics.Color para poder acceder a la lista de colores de cinturones
// Esto al final se hará con la api
// Añadimos la lista de cinturones como Map para poder trabajar con el de forma temporal hasta hacer la api
fun SeccionFavoritos(titulo: String, items: List<Pair<String, androidx.compose.ui.graphics.Color>>) {
    val cinturonesMap = belts.toMap()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 12.dp, top = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
            )

            if (items.isEmpty()) {
                Text(
                    text = "No tienes favoritos en esta categoría",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 4.dp)
                )
            } else {
                items.forEach { (nombreTecnica, colorCinturon) ->
                    // Para añadir el nombre del cinturón (momentaneo hasta que tengamos la api)
                    // con .find para encontrar la priemra coincidencia
                    // Es como utilizar un forMap de Java (foreach y entryset)
                    val nombreCinturon = cinturonesMap.entries.find { it.value == colorCinturon }?.key ?: "Desconocido"
                    ItemFavorito(nombre = nombreTecnica, nombreCinturon = nombreCinturon , colorCinturon = colorCinturon)
                }
            }
        }
    }
}

@Composable
fun ItemFavorito(nombre: String, nombreCinturon: String ,colorCinturon: Color) {
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
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Indicador circular del color del cinturón
                Surface(
                    modifier = Modifier.size(16.dp),
                    shape = CircleShape,
                    color = colorCinturon,
                ) {}

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = nombre,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(
                        text = nombreCinturon,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            // Botón de flecha característico de tu diseño
            Surface(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.padding(4.dp),
                )
            }
        }
    }
}