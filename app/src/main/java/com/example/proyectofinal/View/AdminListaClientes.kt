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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyectofinal.Logic.AlumnoEjemplo
import com.example.proyectofinal.ui.theme.ProyectoFinalTheme

@Composable
fun AdminListaClientes(
    paddingValues: PaddingValues = PaddingValues(),
) {
    // ESTADOS
    var searchQuery by remember { mutableStateOf("") }

    // DATOS DE PRUEBA
    val listaAlumnos = remember {
        listOf(
            AlumnoEjemplo(1, "Juan", "Pérez"),
            AlumnoEjemplo(2, "María", "Gómez"),
            AlumnoEjemplo(3, "Carlos", "López"),
            AlumnoEjemplo(4, "Ana", "Martínez"),
            AlumnoEjemplo(5, "Luis", "García"),
            AlumnoEjemplo(6, "Elena", "Rodríguez")
        )
    }

    // LÓGICA DE FILTRADO
    val alumnosFiltrados = listaAlumnos.filter {
        it.nombre.contains(searchQuery, ignoreCase = true) ||
                it.apellidos.contains(searchQuery, ignoreCase = true)
    }

    // CONTENEDOR PRINCIPAL
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp), // Margen general de la pantalla
        verticalArrangement = Arrangement.spacedBy(16.dp) // Separación entre las dos tarjetas
    ) {

        // --- CARD 1: GESTIÓN DE EXÁMENES ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Título de la Card
                Text(
                    text = "Gestión de exámenes",
                    style = MaterialTheme.typography.titleMedium,
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Subcontenedor navegable
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { }, // Navegación
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.onSecondary // Fondo sutil
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween, // Texto izq, Icono der
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Gestionar exámenes",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2D0C03)
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Ir a gestionar exámenes",
                            tint = Color(0xFF2D0C03)
                        )
                    }
                }
            }
        }

        // --- CARD 2: LISTA DE CLIENTES ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), // Clave: ocupa todo el espacio sobrante en la pantalla
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Título de la Card
                Text(
                    text = "Lista de alumnos",
                    style = MaterialTheme.typography.titleMedium,
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Buscador (TextArea)
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            "Buscar por nombre o apellidos...",
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Buscar")
                    },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Lista de alumnos navegable
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp), // Separación entre alumnos
                    contentPadding = PaddingValues(bottom = 8.dp)
                ) {
                    items(alumnosFiltrados) { alumno ->
                        // Subcontenedor navegable para cada alumno
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.onSecondary
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${alumno.nombre} ${alumno.apellidos}",
                                    fontWeight = FontWeight.Bold,
                                )
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowRight,
                                    contentDescription = "Ver perfil de ${alumno.nombre}",
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminListaClientespreview() {
    ProyectoFinalTheme {
        AdminListaClientes()
    }
}