package com.example.proyectofinal.View

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyectofinal.ui.theme.ProyectoFinalTheme

// 1. DATA CLASS SIMULADA
data class AlumnoExamen(
    val id: Int,
    val nombre: String,
    val apellidos: String,
    val cinturon: String
)

// ENUM PARA SIMULAR LOS ESTADOS DEL EXAMEN (Lógica provisional visual)
enum class EstadoExamen { NO_INICIADO, SOLICITUDES, EXAMINADOS }

@Composable
fun AdminGestionExamen(paddingValues: PaddingValues = PaddingValues()) {

    // --- ESTADOS PROVISIONALES ---
    var estadoExamen by remember { mutableStateOf(EstadoExamen.NO_INICIADO) }
    var searchQuery by remember { mutableStateOf("") }
    var ordenAscendente by remember { mutableStateOf(true) }

    // Estados de los Dialogs
    var showCancelarDialog by remember { mutableStateOf(false) }
    var showRealizarDialog by remember { mutableStateOf(false) }
    var showAprobarDialog by remember { mutableStateOf(false) }

    // Datos de prueba
    val listaAlumnos = remember {
        listOf(
            AlumnoExamen(1, "Juan", "Pérez", "Blanco"),
            AlumnoExamen(2, "María", "Gómez", "Amarillo"),
            AlumnoExamen(3, "Carlos", "López", "Naranja")
        )
    }

    // --- DIALOGS ---
    if (showCancelarDialog) {
        DialogAccionExamen(
            titulo = "Cancelar examen",
            descripcion = "Esta acción eliminará todas las solicitudes actuales y cerrará el periodo de examen. Los alumnos serán notificados. Esta acción no se puede deshacer.",
            textoAceptar = "Cancelar examen",
            textoCancelar = "Seguir examen",
            colorAceptar = MaterialTheme.colorScheme.error,
            colorCancelar = MaterialTheme.colorScheme.primary,
            colorTextoCancelar = MaterialTheme.colorScheme.onPrimary,
            mostrarTextArea = false,
            onAceptar = {
                showCancelarDialog = false
                estadoExamen = EstadoExamen.NO_INICIADO
            },
            onCancelar = { showCancelarDialog = false }
        )
    }

    if (showRealizarDialog) {
        DialogAccionExamen(
            titulo = "Realizar examen",
            descripcion = "Estás a punto de abrir el periodo de examen. Los alumnos podrán empezar a enviar sus solicitudes. Puedes añadir una descripción o indicaciones generales a continuación:",
            textoAceptar = "Aceptar",
            textoCancelar = "Cancelar",
            colorAceptar = MaterialTheme.colorScheme.primary,
            colorCancelar = Color.LightGray,
            colorTextoCancelar = MaterialTheme.colorScheme.primary,
            mostrarTextArea = true,
            onAceptar = {
                showRealizarDialog = false
                estadoExamen = EstadoExamen.SOLICITUDES
            },
            onCancelar = { showRealizarDialog = false }
        )
    }

    if (showAprobarDialog) {
        DialogAccionExamen(
            titulo = if (estadoExamen == EstadoExamen.SOLICITUDES) "Aceptar a todos" else "Aprobar a todos",
            descripcion = "Se va a proceder a aplicar esta acción a toda la lista actual de alumnos mostrada. Puedes añadir una anotación general que se guardará en el registro:",
            textoAceptar = "Aceptar",
            textoCancelar = "Cancelar",
            colorAceptar = MaterialTheme.colorScheme.primary,
            colorCancelar = Color.LightGray,
            colorTextoCancelar = MaterialTheme.colorScheme.primary,
            mostrarTextArea = false,
            onAceptar = {
                showAprobarDialog = false
                estadoExamen = if (estadoExamen == EstadoExamen.SOLICITUDES) EstadoExamen.EXAMINADOS
                else EstadoExamen.NO_INICIADO
            },
            onCancelar = { showAprobarDialog = false }
        )
    }

    // --- CONTENIDO DE LA PANTALLA ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- CARD 1: ACCIONES DE EXAMEN ---
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
                Text(
                    text = "Acciones de examen",
                    style = MaterialTheme.typography.titleMedium,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    // Botón Izquierdo: Cancelar Examen
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { showCancelarDialog = true },
                        enabled = estadoExamen != EstadoExamen.NO_INICIADO,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            disabledContainerColor = Color.LightGray
                        )
                    ) {
                        Text(
                            "Cancelar examen",
                            color = if (estadoExamen != EstadoExamen.NO_INICIADO) Color.White else Color.DarkGray,
                        )
                    }

                    // Botón Derecho: Realizar / Aceptar / Aprobar
                    val textoBtnDerecho = when (estadoExamen) {
                        EstadoExamen.NO_INICIADO -> "Realizar examen"
                        EstadoExamen.SOLICITUDES -> "Aceptar a todos"
                        EstadoExamen.EXAMINADOS -> "Aprobar a todos"
                    }
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            if (estadoExamen == EstadoExamen.NO_INICIADO) showRealizarDialog = true
                            else showAprobarDialog = true
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(textoBtnDerecho)
                    }
                }
            }
        }

        // --- CARD 2: LISTA DE CLIENTES ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Título condicional
                val textoCondicion =
                    if (estadoExamen == EstadoExamen.EXAMINADOS) "Examinados" else "Solicitudes"
                Text(
                    text = "Lista de alumnos: $textoCondicion",
                    style = MaterialTheme.typography.titleMedium,
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Fila del Buscador + Filtro Orden
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.weight(1f),
                        placeholder = {
                            Text(
                                "Buscar alumno...",
                                style = MaterialTheme.typography.bodySmall
                            )
                        },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                    )

                    // Botón Filtro (Alterna ascendente/descendente)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(50.dp),
                        onClick = { ordenAscendente = !ordenAscendente }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = if (ordenAscendente) "Asc." else "Des.",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Lista de alumnos
                if (listaAlumnos.isEmpty() || estadoExamen == EstadoExamen.NO_INICIADO) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "La lista está vacía",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 8.dp)
                    ) {
                        items(listaAlumnos) { alumno ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSecondary)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Datos Alumno
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${alumno.nombre} ${alumno.apellidos}",
                                            fontWeight = FontWeight.Bold,
                                        )
                                        Text(
                                            text = "Cinturón: ${alumno.cinturon}",
                                            style = MaterialTheme.typography.bodySmall,
                                        )
                                    }

                                    // Botones de Acción (Tick verde y X roja)
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color(0xFFE8F5E9), // Fondo verde clarito
                                            modifier = Modifier.size(36.dp),
                                            onClick = { /* Lógica Aprobar Individual */ }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Aprobar",
                                                tint = Color(0xFF4CAF50), // Verde
                                                modifier = Modifier.padding(6.dp)
                                            )
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color(0xFFFFEBEE), // Fondo rojo clarito
                                            modifier = Modifier.size(36.dp),
                                            onClick = { /* Lógica Rechazar Individual */ }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Rechazar",
                                                tint = Color(0xFFF44336), // Rojo
                                                modifier = Modifier.padding(6.dp)
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
    }
}

// 2. COMPONENTE REUTILIZABLE PARA LOS DIALOGS DE EXAMEN
@Composable
fun DialogAccionExamen(
    titulo: String,
    descripcion: String,
    textoAceptar: String,
    textoCancelar: String,
    colorAceptar: Color,
    colorCancelar: Color,
    colorTextoCancelar: Color,
    mostrarTextArea: Boolean,
    onAceptar: () -> Unit,
    onCancelar: () -> Unit
) {
    var comentario by remember { mutableStateOf("") }

    androidx.compose.ui.window.Dialog(onDismissRequest = onCancelar) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = descripcion,
                    textAlign = TextAlign.Center
                )

                if (mostrarTextArea) {
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = comentario,
                        onValueChange = { comentario = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp), // TextArea más grande
                        placeholder = {
                            Text(
                                "Añadir notas (opcional)...",
                                style = MaterialTheme.typography.bodySmall
                            )
                        },
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Botón Aceptar (Izquierda - Acción principal)
                    Button(
                        onClick = onAceptar,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = colorAceptar),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            textoAceptar,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    // Botón Cancelar (Derecha - Auxiliar)
                    Button(
                        onClick = onCancelar,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = colorCancelar),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            textoCancelar,
                            color = colorTextoCancelar,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminGestionExamenpreview() {
    ProyectoFinalTheme {
        AdminGestionExamen()
    }
}