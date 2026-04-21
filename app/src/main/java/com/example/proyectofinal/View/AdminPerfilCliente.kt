package com.example.proyectofinal.View

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
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
import com.example.proyectofinal.Logic.anios
import com.example.proyectofinal.Logic.aniosMenor
import com.example.proyectofinal.Logic.dayPerMonthFunction
import com.example.proyectofinal.Logic.dias
import com.example.proyectofinal.Logic.listaGimnasios
import com.example.proyectofinal.Logic.mapBeltColor
import com.example.proyectofinal.Logic.mapaProfesores
import com.example.proyectofinal.Logic.meses
import com.example.proyectofinal.ViewModel.StateNavigate
import com.example.proyectofinal.ui.theme.ProyectoFinalTheme
import kotlin.text.ifEmpty

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AdminPerfilCliente(
    paddingValues: PaddingValues = PaddingValues(),
    controller: (String) -> Unit
) {
    // --- ESTADOS SIMULADOS (MOCKS) ---
    var nombre by remember { mutableStateOf("María") }
    var apellidos by remember { mutableStateOf("Gómez") }
    var telefono by remember { mutableStateOf("611223344") }
    var email by remember { mutableStateOf("maria.gomez@email.com") }
    var cinturon by remember { mutableStateOf("Naranja") }

    // --- ESTADOS DE CENTROS Y PROFESORES ---
    var centroSeleccionado by remember { mutableStateOf("Las Rozas") } // Valor simulado
    var profesoresSeleccionados by remember { mutableStateOf(setOf("Ángel Ruiz")) } // Valor simulado
    val profesoresDisponibles = mapaProfesores[centroSeleccionado] ?: emptyList()

    // --- ESTADOS DEL MENOR ---
    var esMenor by remember { mutableStateOf(true) } // Valor simulado en true para probar
    var nombreMenor by remember { mutableStateOf("Hugo") }
    var apellidosMenor by remember { mutableStateOf("Gómez") }
    var diaMenor by remember { mutableStateOf("10") }
    var mesMenor by remember { mutableStateOf("Mayo") }
    var anioMenor by remember { mutableStateOf("2015") }
    var dayListMenor by remember { mutableStateOf(dias) }

    // Estados para los desplegables de Fecha de Nacimiento
    var dia by remember { mutableStateOf("15") }
    var mes by remember { mutableStateOf("Agosto") }
    var anio by remember { mutableStateOf("1995") }

    // Remember de la lista de dias que cambiara en función del mes seleccionado
    var dayList by remember { mutableStateOf(dias) }

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
                // Aquí irá la lógica real de eliminar en BD
                controller(StateNavigate.adminListaClientes.value)
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
                            CampoDesplegableAdmin(
                                label = dia,
                                seleccionado = dia,
                                modifier = Modifier.weight(1f),
                                opciones = dayList,
                                onValueChange = { opcion, index -> dia = opcion }
                            )
                            CampoDesplegableAdmin(
                                label = mes,
                                modifier = Modifier.weight(1f),
                                seleccionado = mes,
                                opciones = meses,
                                onValueChange = { opcion, index ->
                                    val result = dayPerMonthFunction(index)
                                    mes = opcion
                                    if(result.first < dia.toInt()) {
                                        dia = result.first.toString()
                                    }
                                    dayList = result.second
                                }
                            )
                            CampoDesplegableAdmin(
                                label = anio,
                                seleccionado = anio,
                                modifier = Modifier.weight(1f),
                                opciones = anios,
                                onValueChange = { opcion, index -> anio = opcion }
                            )
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

                    // DESPLEGABLES CENTRO Y PROFESORES --- QUITAR PARA HACER LOGICA
                    /*
                    CampoDesplegableGimnasios(
                        label = "Gimnasio",
                        opciones = listaGimnasios,
                        seleccionado = centroSeleccionado,
                        onValueChange = { nuevoCentro ->
                            centroSeleccionado = nuevoCentro
                            profesoresSeleccionados = emptySet()
                        }
                    )

                    CampoDesplegableProfesores(
                        label = "Profesor/es asignado/s",
                        opciones = profesoresDisponibles,
                        seleccionados = profesoresSeleccionados,
                        enabled = centroSeleccionado.isNotEmpty(),
                        onSelectionChange = { nuevaSeleccion ->
                            profesoresSeleccionados = nuevaSeleccion
                        }
                    )
                    */

                    // DESPLEGABLE CINTURÓN
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Cinturón",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp),
                        )
                        CampoDesplegableAdmin(
                            label = cinturon,
                            opciones = mapBeltColor.keys.toList(),
                            seleccionado = cinturon,
                            onValueChange = { opcion, index -> cinturon = opcion }
                        )
                    }

                    // --- CHECKBOX MENOR DE EDAD ---
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                esMenor = !esMenor
                                if (!esMenor) {
                                    nombreMenor = ""
                                    apellidosMenor = ""
                                    diaMenor = ""
                                    mesMenor = ""
                                    anioMenor = ""
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.material3.Checkbox(
                            checked = esMenor,
                            onCheckedChange = { checked ->
                                esMenor = checked
                                if (!checked) {
                                    nombreMenor = ""
                                    apellidosMenor = ""
                                    diaMenor = ""
                                    mesMenor = ""
                                    anioMenor = ""
                                }
                            },
                            colors = androidx.compose.material3.CheckboxDefaults.colors(
                                checkedColor = MaterialTheme.colorScheme.tertiary
                            )
                        )
                        Text(
                            text = "El alumno es menor de 14 años",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    // --- CAMPOS CONDICIONALES DEL MENOR ---
                    if (esMenor) {
                        // Usamos tu componente CampoPerfilAdmin para mantener el diseño
                        CampoPerfilAdmin(
                            label = "Nombre del niño/a",
                            value = nombreMenor,
                            onValueChange = { nombreMenor = it }
                        )

                        CampoPerfilAdmin(
                            label = "Apellidos niño/a",
                            value = apellidosMenor,
                            onValueChange = { apellidosMenor = it }
                        )

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Fecha de nacimiento niño/a",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Usamos tu componente CampoDesplegableAdmin
                                CampoDesplegableAdmin(
                                    label = diaMenor.ifEmpty { "Día" },
                                    seleccionado = diaMenor,
                                    modifier = Modifier.weight(1f),
                                    opciones = dayListMenor,
                                    onValueChange = { opcion, _ -> diaMenor = opcion }
                                )
                                CampoDesplegableAdmin(
                                    label = mesMenor.ifEmpty { "Mes" },
                                    seleccionado = mesMenor,
                                    modifier = Modifier.weight(1f),
                                    opciones = meses,
                                    onValueChange = { opcion, index ->
                                        val result = dayPerMonthFunction(index)
                                        mesMenor = opcion
                                        if (diaMenor.isNotEmpty() && result.first < diaMenor.toInt()) {
                                            diaMenor = result.first.toString()
                                        }
                                        dayListMenor = result.second
                                    }
                                )
                                CampoDesplegableAdmin(
                                    label = anioMenor.ifEmpty { "Año" },
                                    seleccionado = anioMenor,
                                    modifier = Modifier.weight(1f),
                                    opciones = aniosMenor,
                                    onValueChange = { opcion, _ -> anioMenor = opcion }
                                )
                            }
                        }
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoDesplegableAdmin(
    label: String,
    opciones: List<String>,
    seleccionado: String,
    modifier: Modifier = Modifier,
    onValueChange: (String, Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .menuAnchor() // Imprescindible para el menú
                .fillMaxWidth()
                .height(45.dp) // Altura personalizada más baja
                .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = seleccionado.ifEmpty { label },
                maxLines = 1
            )

            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = Color.Gray
            )
        }

        // Menu desplegable
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(120.dp)
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        var index = 0
                        val prueba = label.toIntOrNull()
                        if (prueba == null) {
                            index = opciones.indexOf(opcion) + 1
                        }
                        onValueChange(opcion, index)
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

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun AdminPerfilClientePreview() {
    ProyectoFinalTheme {
        AdminPerfilCliente(controller = {})
    }
}