package com.example.proyectofinal.View

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectofinal.Logic.locale
import com.example.proyectofinal.Logic.mapBeltColor
import com.example.proyectofinal.ViewModel.ContentViewModel
import com.example.proyectofinal.ViewModel.StateNavigate
import com.example.proyectofinal.ui.theme.ProyectoFinalTheme
import com.example.proyectofinal.Model.Content

@Composable
fun ListaContenido(
    paddingValues: PaddingValues = PaddingValues(),
    viewModel: ContentViewModel = viewModel(),
    controller: (String) -> Unit
) {
    val contentList by viewModel.contentList.collectAsStateWithLifecycle()
    val selectedBeltId by viewModel.selectedBeltId.collectAsStateWithLifecycle()

    // Filtramos los contenidos por tipo
    val tecnicas = contentList.filter { it.contentType == "TECH" }
    val formas = contentList.filter { it.contentType == "FORM" }
    val sets = contentList.filter { it.contentType == "SET" }

    // Contenedor principal que usa el color del cinturón como fondo
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            // Probando color de fondo del cinturón
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        mapBeltColor[selectedBeltId]
                            ?: MaterialTheme.colorScheme.background, // Color cinturón (si no se lee, color de fondo normal)
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
            if (tecnicas.isNotEmpty()) {
                item {
                    ContenedorContenido(
                        titulo = "Técnicas",
                        items = tecnicas
                    ) {
                        // Cambiamos el valor del id del contenido según el que se seleccione, y se navega a la pantalla después
                        viewModel.setSelectedContentId(it.id)
                        controller(StateNavigate.contenido.value)
                    }
                }
            }

            // SECCIÓN FORMA (KATA)
            if (formas.isNotEmpty()) {
                item {
                    ContenedorContenido(
                        titulo = "Forma (Kata)",
                        items = formas
                    ) {
                        // Cambiamos el valor del id del contenido según el que se seleccione, y se navega a la pantalla después
                        viewModel.setSelectedContentId(it.id)
                        controller(StateNavigate.contenido.value)
                    }
                }
            }

            // SECCIÓN SET
            if (sets.isNotEmpty()) {
                item {
                    ContenedorContenido(
                        titulo = "Set",
                        items = sets
                    ) {
                        // Cambiamos el valor del id del contenido según el que se seleccione, y se navega a la pantalla después
                        viewModel.setSelectedContentId(it.id)
                        controller(StateNavigate.contenido.value)
                    }
                }
            }
        }
    }
}

@Composable
fun ContenedorContenido(titulo: String, items: List<Content>, controller: (Content) -> Unit) {
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
            items.forEach { content ->
                // Ponemos name según el idioma del dispositov (locale)
                val nombreItem = if (content.contentType != "TECH") content.name[locale]
                    ?: "" else "${content.number}. ${content.name[locale] ?: ""}"
                SubcontenedorContenido(
                    nombre = nombreItem
                ) { controller(content) }
            }
        }
    }
}

@Composable
fun SubcontenedorContenido(nombre: String, controller: () -> Unit) {
    // Cada técnica individual dentro de la sección
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 80.dp)
            .clickable { controller() },
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
                modifier = Modifier.weight(1f)
            )

            // Botón de flecha a la derecha
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.White.copy(alpha = 0.3f),
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
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
        ListaContenido() {}
    }
}