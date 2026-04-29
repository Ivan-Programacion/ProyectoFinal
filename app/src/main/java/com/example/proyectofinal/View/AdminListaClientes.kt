package com.example.proyectofinal.View

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectofinal.ViewModel.AdminListaClientesViewModel
import com.example.proyectofinal.ViewModel.AdminPerfilClienteViewModel
import com.example.proyectofinal.ViewModel.StateNavigate
import com.example.proyectofinal.ui.theme.ProyectoFinalTheme

@SuppressLint("ContextCastToActivity")
@Composable
fun AdminListaClientes(
    paddingValues: PaddingValues = PaddingValues(),
    viewModel: AdminListaClientesViewModel = viewModel(),
    adminPerfilViewModel: AdminPerfilClienteViewModel = viewModel(),
    controller: (String) -> Unit,
) {
    // ESTADOS DEL VIEWMODEL RECOLECTADOS (REACTIVOS)
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val alumnosFiltrados by viewModel.filteredStudents.collectAsStateWithLifecycle()
    
    // Para botón atrás del móvil
    val context = LocalContext.current as? Activity

    // Para vaciar el campo de busqueda al volver a la pantalla
    // Se necesita hacer con LifecycleOwner ya que utilizamos datos reactivos
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onSearchQueryChanged("")
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Interceptamos el botón de atrás
    BackHandler {
        // Cerramos la aplicación por completo
        context?.finish()
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
                        .clickable { controller(StateNavigate.adminGestionExamen.value) }, // Navegación
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
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White.copy(alpha = 0.3f),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowRight,
                                contentDescription = "Ir a gestión de exámenes",
                                modifier = Modifier.padding(4.dp)
                            )
                        }
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
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            "Buscar nombre o apellidos...",
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
                                .clickable {
                                    adminPerfilViewModel.setStudentId(alumno.id)
                                    controller(StateNavigate.adminPerfilCliente.value)
                                },
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
                                    text = "${alumno.name} ${alumno.lastName}",
                                    fontWeight = FontWeight.Bold,
                                )
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.White.copy(alpha = 0.3f),
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowRight,
                                        contentDescription = "Ver perfil de ${alumno.name}",
                                        modifier = Modifier.padding(4.dp)
                                    )
                                }
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
        AdminListaClientes(controller = { })
    }
}
