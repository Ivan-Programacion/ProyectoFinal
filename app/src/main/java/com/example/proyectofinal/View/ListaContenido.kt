package com.example.proyectofinal.View

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.proyectofinal.Logic.provisionalContentFormBeltList
import com.example.proyectofinal.Logic.provisionalContentSetBeltList
import com.example.proyectofinal.Logic.provisionalContentTecBeltList

@Composable
fun ListaContenido(paddingValues: PaddingValues = PaddingValues()) {
    // Contenedor principal que usa el color del cinturón como fondo
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
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
                SeccionContenedora(titulo = "Técnicas", items = provisionalContentTecBeltList)
            }

            // SECCIÓN FORMA (KATA)
            item {
                SeccionContenedora(titulo = "Forma (Kata)", items = provisionalContentFormBeltList)
            }

            // SECCIÓN SET
            item {
                SeccionContenedora(titulo = "Set", items = provisionalContentSetBeltList)
            }
        }
    }
}

@Composable
fun SeccionContenedora(titulo: String, items: List<String>) {
    // Tarjeta clara que agrupa los elementos
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        // AÑADIR FONDO COLOR AQUI PARA PROBAR
        //colors = CardDefaults.cardColors(
        //    containerColor = Color(0xFFF3E5E5).copy(alpha = 0.95f)
        //)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
            )

            // Generamos una fila por cada elemento de la lista
            items.forEach { nombreItem ->
                ItemFilaInteractivo(nombre = nombreItem)
            }
        }
    }
}

@Composable
fun ItemFilaInteractivo(nombre: String) {
    // Cada técnica individual dentro de la sección
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Navegación al detalle */ },
        shape = RoundedCornerShape(18.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = nombre,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
            )

            // Botón de flecha a la derecha
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.3f),
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