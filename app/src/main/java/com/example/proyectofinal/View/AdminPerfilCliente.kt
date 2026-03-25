package com.example.proyectofinal.View

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.proyectofinal.ui.theme.ProyectoFinalTheme

@Composable
fun AdminPerfilCliente(
    paddingValues: PaddingValues = PaddingValues(),
    controller: () -> Unit
) {
    // --- ESTADOS SIMULADOS (MOCKS) ---
    var nombre by remember { mutableStateOf("María") }
    var apellidos by remember { mutableStateOf("Gómez") }
    var telefono by remember { mutableStateOf("611223344") }
    var email by remember { mutableStateOf("maria.gomez@email.com") }
    var cinturon by remember { mutableStateOf("Naranja") }

    // Estados para los desplegables de Fecha de Nacimiento
    var dia by remember { mutableStateOf("15") }
    var mes by remember { mutableStateOf("08") }
    var anio by remember { mutableStateOf("1995") }

    // --- ESTADOS DE LOS DIALOGS ---
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showEliminarDialog by remember { mutableStateOf(false) }

    // --- LÓGICA DE DIALOGS ---
    if (showConfirmDialog) {
        ConfirmarCambiosDialogAdmin(
            nombreCompleto = "$nombre $apellidos",
            onConfirm = { showConfirmDialog = false },
            onDismiss = { showConfirmDialog = false }
        )
    }

    if (showEliminarDialog) {
        EliminarAlumnoDialog(
            nombreCompleto = "$nombre $apellidos",
            onConfirm = {
                showEliminarDialog = false
                // Aquí iría la lógica real de eliminar en BD
                controller() // Regresa a la pantalla Admin: Lista clientes
            },
            onDismiss = { showEliminarDialog = false }
        )
    }

    // --- CONTENIDO PRINCIPAL ---
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, top = 16.dp, bottom = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp) // SpaceEvenly aproximado con espaciado constante
                ) {
                    Text(text = "Datos perfil", style = MaterialTheme.typography.titleMedium)

                    // AVATAR
                    Surface(
                        modifier = Modifier.size(100.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.onSecondary
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = nombre.take(1).uppercase(),
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // CAMPOS DE TEXTO NORMALES
                    CampoPerfilAdmin(
                        label = "Nombre",
                        value = nombre,
                        onValueChange = { nombre = it })
                    CampoPerfilAdmin(
                        label = "Apellidos",
                        value = apellidos,
                        onValueChange = { apellidos = it })

                    // DESPLEGABLES FECHA NACIMIENTO
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Fecha de nacimiento",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp),
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                CampoDesplegableAdmin(
                                    value = dia,
                                    opciones = (1..31).map { it.toString().padStart(2, '0') },
                                    onSelectionChange = { dia = it }
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                CampoDesplegableAdmin(
                                    value = mes,
                                    opciones = (1..12).map { it.toString().padStart(2, '0') },
                                    onSelectionChange = { mes = it }
                                )
                            }
                            Box(modifier = Modifier.weight(1.5f)) {
                                CampoDesplegableAdmin(
                                    value = anio,
                                    opciones = (1950..2024).map { it.toString() }.reversed(),
                                    onSelectionChange = { anio = it }
                                )
                            }
                        }
                    }

                    CampoPerfilAdmin(
                        label = "Teléfono",
                        value = telefono,
                        onValueChange = { telefono = it })

                    // EMAIL (NO Editable POR AHORA)
                    CampoPerfilAdmin(
                        label = "Email",
                        value = email,
                        enabled = false,
                        onValueChange = { email = it })

                    // DESPLEGABLE CINTURÓN
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Cinturón",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp),
                        )
                        CampoDesplegableAdmin(
                            value = cinturon,
                            opciones = listOf(
                                "Blanco",
                                "Amarillo",
                                "Naranja",
                                "Verde",
                                "Azul",
                                "Marrón",
                                "Negro"
                            ),
                            onSelectionChange = { cinturon = it }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // BOTÓN ACTUALIZAR
                    Button(
                        onClick = { showConfirmDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text("Actualizar", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }

                    // BOTÓN ELIMINAR USUARIO
                    Button(
                        onClick = { showEliminarDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Eliminar alumno", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

// --- COMPONENTE: CAMPO DE PERFIL REUTILIZABLE ---
@Composable
fun CampoPerfilAdmin(
    label: String, value: String, onValueChange: (String) -> Unit, enabled: Boolean = true
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp),
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                disabledContainerColor = Color.LightGray,
                disabledTextColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

// --- COMPONENTE: CAMPO DESPLEGABLE REUTILIZABLE ---
@Composable
fun CampoDesplegableAdmin(
    value: String,
    opciones: List<String>,
    onSelectionChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }, // Abre el menú al tocar la caja
            enabled = false, // Lo deshabilitamos visualmente para que actúe 100% como botón clickeable
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Desplegar",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledTrailingIconColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = Color.Transparent
            )
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(text = opcion) },
                    onClick = {
                        onSelectionChange(opcion)
                        expanded = false
                    }
                )
            }
        }
    }
}

// --- DIALOG: CONFIRMAR ACTUALIZACIÓN (Reutilizado del diseño previo) ---
@Composable
fun ConfirmarCambiosDialogAdmin(
    nombreCompleto: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
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
                    text = "¿Guardar cambios?",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Se van a modificar los datos de $nombreCompleto. ¿Estás seguro de querer modificar los datos?",
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text(
                            "Aceptar",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "Cancelar",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

// --- DIALOG: ELIMINAR ALUMNO ---
@Composable
fun EliminarAlumnoDialog(nombreCompleto: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
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
                    text = "¿Eliminar alumno?",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Estás a punto de eliminar al alumno $nombreCompleto permanentemente de la aplicación. ¿Estás seguro de querer eliminar al alumno?",
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Botón Eliminar (Izquierda - Error)
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text(
                            "Eliminar",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    // Botón Cancelar (Derecha - Auxiliar LightGray)
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "Cancelar",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
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
fun AdminPerfilClientePreview() {
    ProyectoFinalTheme {
        AdminPerfilCliente(controller = {})
    }
}