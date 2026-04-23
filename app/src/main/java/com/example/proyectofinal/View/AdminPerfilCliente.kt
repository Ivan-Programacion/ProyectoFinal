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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectofinal.Logic.anios
import com.example.proyectofinal.Logic.aniosMenor
import com.example.proyectofinal.Logic.dayPerMonthFunction
import com.example.proyectofinal.Logic.dias
import com.example.proyectofinal.Logic.mapBeltColor
import com.example.proyectofinal.Logic.meses
import com.example.proyectofinal.ViewModel.AdminPerfilClienteViewModel
import com.example.proyectofinal.ViewModel.StateNavigate
import com.example.proyectofinal.ui.theme.ProyectoFinalTheme

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AdminPerfilCliente(
    paddingValues: PaddingValues = PaddingValues(),
    viewModel: AdminPerfilClienteViewModel = viewModel(),
    controller: (String) -> Unit
) {
    // --- ESTADOS DEL VIEWMODEL ---
    val nombre by viewModel.nombre.collectAsStateWithLifecycle()
    val apellidos by viewModel.apellidos.collectAsStateWithLifecycle()
    val telefono by viewModel.telefono.collectAsStateWithLifecycle()
    val email by viewModel.email.collectAsStateWithLifecycle()
    val beltId by viewModel.beltId.collectAsStateWithLifecycle()
    val isActive by viewModel.isActive.collectAsStateWithLifecycle()

    val dia by viewModel.dia.collectAsStateWithLifecycle()
    val mes by viewModel.mes.collectAsStateWithLifecycle()
    val anio by viewModel.anio.collectAsStateWithLifecycle()

    val esMenor by viewModel.esMenor.collectAsStateWithLifecycle()
    val nombreMenor by viewModel.nombreMenor.collectAsStateWithLifecycle()
    val apellidosMenor by viewModel.apellidosMenor.collectAsStateWithLifecycle()
    val diaMenor by viewModel.diaMenor.collectAsStateWithLifecycle()
    val mesMenor by viewModel.mesMenor.collectAsStateWithLifecycle()
    val anioMenor by viewModel.anioMenor.collectAsStateWithLifecycle()

    val centroSeleccionado by viewModel.centroSeleccionado.collectAsStateWithLifecycle()
    val profesoresSeleccionados by viewModel.profesoresSeleccionados.collectAsStateWithLifecycle()
    val listaCentros by viewModel.listaCentros.collectAsStateWithLifecycle()
    val profesoresDisponibles by viewModel.profesoresDisponibles.collectAsStateWithLifecycle()
    val listaCinturones by viewModel.listaCinturones.collectAsStateWithLifecycle()

    // Estados locales para la lógica de días de la UI
    var dayList by remember { mutableStateOf(dias) }
    var dayListMenor by remember { mutableStateOf(dias) }

    // --- ESTADOS DE LOS DIALOGS ---
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showEliminarDialog by remember { mutableStateOf(false) }

    // --- LÓGICA DE DIALOGS ---
    if (showConfirmDialog) {
        ConfirmarCambiosDialogAdmin(
            nombreCompleto = "$nombre $apellidos",
            onConfirm = {
                viewModel.updateStudent {
                    showConfirmDialog = false
                    controller(StateNavigate.adminListaClientes.value)
                }
            },
            onDismiss = { showConfirmDialog = false }
        )
    }

    if (showEliminarDialog) {
        EliminarAlumnoDialog(
            nombreCompleto = "$nombre $apellidos",
            isActive = isActive,
            onConfirm = {
                viewModel.toggleUserActivation {
                    showEliminarDialog = false
                    // Opcionalmente podemos volver a la lista o quedarnos
                    controller(StateNavigate.adminListaClientes.value)
                }
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
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(text = "Datos perfil alumno/tutor", style = MaterialTheme.typography.titleMedium)

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
                        onValueChange = { viewModel.nombre.value = it })
                    CampoPerfilAdmin(
                        label = "Apellidos",
                        value = apellidos,
                        onValueChange = { viewModel.apellidos.value = it })

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
                                onValueChange = { opcion, _ -> viewModel.dia.value = opcion }
                            )
                            CampoDesplegableAdmin(
                                label = mes,
                                modifier = Modifier.weight(1f),
                                seleccionado = mes,
                                opciones = meses.keys.toList(),
                                onValueChange = { opcion, index ->
                                    val result = dayPerMonthFunction(index)
                                    viewModel.mes.value = opcion
                                    if(dia.isNotEmpty() && result.first < dia.toInt()) {
                                        viewModel.dia.value = result.first.toString()
                                    }
                                    dayList = result.second
                                }
                            )
                            CampoDesplegableAdmin(
                                label = anio,
                                seleccionado = anio,
                                modifier = Modifier.weight(1f),
                                opciones = anios,
                                onValueChange = { opcion, _ -> viewModel.anio.value = opcion }
                            )
                        }
                    }

                    CampoPerfilAdmin(
                        label = "Teléfono",
                        value = telefono,
                        onValueChange = { viewModel.telefono.value = it })

                    // EMAIL (NO Editable)
                    CampoPerfilAdmin(
                        label = "Email",
                        value = email,
                        enabled = false,
                        onValueChange = { viewModel.email.value = it })

                    // --- CHECKBOX MENOR DE EDAD ---
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.esMenor.value = !esMenor
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = esMenor,
                            onCheckedChange = { checked ->
                                viewModel.esMenor.value = checked
                            },
                            colors = CheckboxDefaults.colors(
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
                        CampoPerfilAdmin(
                            label = "Nombre del niño/a",
                            value = nombreMenor,
                            onValueChange = { viewModel.nombreMenor.value = it }
                        )

                        CampoPerfilAdmin(
                            label = "Apellidos niño/a",
                            value = apellidosMenor,
                            onValueChange = { viewModel.apellidosMenor.value = it }
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
                                CampoDesplegableAdmin(
                                    label = diaMenor.ifEmpty { "Día" },
                                    seleccionado = diaMenor,
                                    modifier = Modifier.weight(1f),
                                    opciones = dayListMenor,
                                    onValueChange = { opcion, _ -> viewModel.diaMenor.value = opcion }
                                )
                                CampoDesplegableAdmin(
                                    label = mesMenor.ifEmpty { "Mes" },
                                    seleccionado = mesMenor,
                                    modifier = Modifier.weight(1f),
                                    opciones = meses.keys.toList(),
                                    onValueChange = { opcion, index ->
                                        val result = dayPerMonthFunction(index)
                                        viewModel.mesMenor.value = opcion
                                        if (diaMenor.isNotEmpty() && result.first < diaMenor.toInt()) {
                                            viewModel.diaMenor.value = result.first.toString()
                                        }
                                        dayListMenor = result.second
                                    }
                                )
                                CampoDesplegableAdmin(
                                    label = anioMenor.ifEmpty { "Año" },
                                    seleccionado = anioMenor,
                                    modifier = Modifier.weight(1f),
                                    opciones = aniosMenor,
                                    onValueChange = { opcion, _ -> viewModel.anioMenor.value = opcion }
                                )
                            }
                        }
                    }

                    // --- DESPLEGABLES CENTRO Y PROFESORES ---
                    CampoDesplegableGimnasios(
                        label = "Gimnasio",
                        opciones = listaCentros,
                        seleccionadoId = centroSeleccionado,
                        onValueChange = { nuevoCentroId ->
                            viewModel.onCenterSelected(nuevoCentroId)
                        }
                    )

                    CampoDesplegableProfesores(
                        label = "Profesor/es asignado/s",
                        opciones = profesoresDisponibles,
                        seleccionadosIds = profesoresSeleccionados,
                        enabled = centroSeleccionado.isNotEmpty(),
                        onSelectionChange = { nuevaSeleccion ->
                            viewModel.profesoresSeleccionados.value = nuevaSeleccion
                        }
                    )

                    // DESPLEGABLE CINTURÓN
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Cinturón",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp),
                        )
                        
                        val beltNameToShow = listaCinturones.find { it.id == beltId }?.name?.get("es") ?: beltId
                        
                        CampoDesplegableAdmin(
                            label = beltNameToShow,
                            opciones = listaCinturones.map { it.name["es"] ?: it.id },
                            seleccionado = beltNameToShow,
                            onValueChange = { nameSelected, _ -> 
                                val idSelected = listaCinturones.find { (it.name["es"] ?: it.id) == nameSelected }?.id ?: "white"
                                viewModel.beltId.value = idSelected
                            }
                        )
                    }

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

                    // BOTÓN ELIMINAR/DESACTIVAR/ACTIVAR USUARIO
                    Button(
                        onClick = { showEliminarDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isActive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary
                        )
                    ) {
                        Text(
                            text = if (isActive) "Desactivar alumno" else "Activar alumno",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
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
                .menuAnchor()
                .fillMaxWidth()
                .height(45.dp)
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

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(120.dp)
        ) {
            opciones.forEach { opcion ->
                val isMonth = label == "Mes" || meses.containsKey(label)
                DropdownMenuItem(
                    text = {
                        if (isMonth) Text(meses.getValue(opcion)) else Text(opcion)
                    },
                    onClick = {
                        var index = 0
                        if (isMonth) index = opciones.indexOf(opcion) + 1
                        onValueChange(opcion, index)
                        expanded = false
                    }
                )
            }
        }
    }
}

// --- DIALOG: CONFIRMAR ACTUALIZACIÓN ---
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

// --- DIALOG: ELIMINAR/DESACTIVAR ALUMNO ---
@Composable
fun EliminarAlumnoDialog(
    nombreCompleto: String, 
    isActive: Boolean,
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
                    text = if (isActive) "¿Desactivar alumno?" else "¿Activar alumno?",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Estás a punto de modificar el estado del alumno $nombreCompleto. ¿Estás seguro?",
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
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isActive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary
                        ),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text(
                            text = if (isActive) "Desactivar" else "Activar",
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

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun AdminPerfilClientePreview() {
    ProyectoFinalTheme {
        AdminPerfilCliente(controller = {})
    }
}
