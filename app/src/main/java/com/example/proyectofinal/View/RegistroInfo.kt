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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectofinal.Logic.anios
import com.example.proyectofinal.Logic.aniosMenor
import com.example.proyectofinal.Logic.dias
import com.example.proyectofinal.Logic.meses
import com.example.proyectofinal.Logic.dayPerMonthFunction
import com.example.proyectofinal.Logic.listaGimnasios
import com.example.proyectofinal.Logic.mapaProfesores
import com.example.proyectofinal.ViewModel.AuthUiState
import com.example.proyectofinal.ViewModel.AuthViewModel
import com.example.proyectofinal.ViewModel.StateNavigate
import com.example.proyectofinal.ui.theme.ProyectoFinalTheme

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RegistroInfo(paddingValues: PaddingValues = PaddingValues(), viewModel: AuthViewModel = viewModel(), controller: (String) -> Unit) {
    // Observar estados del viewModel
    val nombre by viewModel.nombre.collectAsStateWithLifecycle()
    val apellidos by viewModel.apellidos.collectAsStateWithLifecycle()
    val email by viewModel.email.collectAsStateWithLifecycle()
    val telefono by viewModel.telefono.collectAsStateWithLifecycle()

    val dia by viewModel.dia.collectAsStateWithLifecycle()
    val mes by viewModel.mes.collectAsStateWithLifecycle()
    val anio by viewModel.anio.collectAsStateWithLifecycle()

    // Remember de la lista de dias que cambiara en función del mes seleccionado
    var dayList by remember { mutableStateOf(dias) }
    var dayListMenor by remember { mutableStateOf(dias) }

    val esMenor by viewModel.esMenor.collectAsStateWithLifecycle()
    val nombreMenor by viewModel.nombreMenor.collectAsStateWithLifecycle()
    val apellidosMenor by viewModel.apellidosMenor.collectAsStateWithLifecycle()
    val diaMenor by viewModel.diaMenor.collectAsStateWithLifecycle()
    val mesMenor by viewModel.mesMenor.collectAsStateWithLifecycle()
    val anioMenor by viewModel.anioMenor.collectAsStateWithLifecycle()

    val centroSeleccionado by viewModel.centroSeleccionado.collectAsStateWithLifecycle()
    val profesoresSeleccionados by viewModel.profesoresSeleccionados.collectAsStateWithLifecycle()

    val isNextButtonEnabled by viewModel.isNextButtonEnabled.collectAsStateWithLifecycle()

    val profesoresDisponibles = mapaProfesores[centroSeleccionado] ?: emptyList()

    Box(
        modifier = Modifier
            .fillMaxSize()
            // Añade espacio automático para no chocar con el reloj ni los gestos del sistema
            .windowInsetsPadding(WindowInsets.systemBars),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { controller(StateNavigate.login.value) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver atrás",
                            tint = MaterialTheme.colorScheme.primary
                    )
                    }
                    Text(
                        text = "Crear cuenta",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                // --- CAMPOS DE TEXTO ---
                FilaRegistro(label = "Nombre", value = nombre, placeholder = "Ej. Juan") {
                    viewModel.nombre.value = it
                }
                FilaRegistro(
                    label = "Apellidos",
                    value = apellidos,
                    placeholder = "Ej. Pérez"
                ) { viewModel.apellidos.value = it }

                // --- FECHA DE NACIMIENTO ---
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Fecha de nacimiento", fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CampoFecha(
                            label = "Día",
                            opciones = dayList,
                            seleccionado = dia,
                            modifier = Modifier.weight(1f),
                            onValueChange = { opcion, index ->
                                viewModel.dia.value = opcion
                        }
                        )
                        CampoFecha(
                            label = "Mes",
                            opciones = meses,
                            seleccionado = mes,
                            modifier = Modifier.weight(1.2f),
                            onValueChange = { opcion, index ->
                                val result = dayPerMonthFunction(index)
                                viewModel.mes.value = opcion
                                if (dia.isNotEmpty() && result.first < dia.toInt()) {
                                    viewModel.dia.value = result.first.toString()
                                }
                                dayList = result.second
                        }
                        )
                        CampoFecha(
                            label = "Año",
                            opciones = anios,
                            seleccionado = anio,
                            modifier = Modifier.weight(1.3f),
                            onValueChange = { opcion, index -> viewModel.anio.value = opcion }
                        )
                    }
                }

                // --- CAMPOS DE TEXTO ---
                FilaRegistro(
                    label = "Email",
                    value = email,
                    placeholder = "ej. correo@dominio.com"
                ) { viewModel.email.value = it }
                FilaRegistro(
                    label = "Teléfono",
                    value = telefono,
                    placeholder = "ej. 612345678"
                ) { viewModel.telefono.value = it }

                // --- SELECCIÓN DE CENTRO Y PROFESOR ---
                CampoDesplegableGimnasios(
                    label = "Gimnasio",
                    opciones = listaGimnasios,
                    seleccionado = centroSeleccionado,
                    onValueChange = { nuevoCentro ->
                        viewModel.centroSeleccionado.value = nuevoCentro
                        viewModel.profesoresSeleccionados.value = emptySet()
                    }
                )

                CampoDesplegableProfesores(
                    label = "Profesor/es asignado/s",
                    opciones = profesoresDisponibles,
                    seleccionados = profesoresSeleccionados,
                    enabled = centroSeleccionado.isNotEmpty(), // Solo habilitado si hay centro
                    onSelectionChange = { nuevaSeleccion ->
                        viewModel.profesoresSeleccionados.value = nuevaSeleccion
                    }
                )

                // --- CHECKBOX MENOR DE EDAD ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { // Hacemos toda la fila clickable para mayor comodidad
                            viewModel.esMenor.value = !esMenor
                            if (!viewModel.esMenor.value) { // Si se desmarca, limpiamos los datos
                                viewModel.nombreMenor.value = ""
                                viewModel.apellidosMenor.value = ""
                                viewModel.diaMenor.value = ""
                                viewModel.mesMenor.value = ""
                                viewModel.anioMenor.value = ""
                            }
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = esMenor,
                        onCheckedChange = { checked ->
                            viewModel.esMenor.value = checked
                            if (!checked) {
                                viewModel.nombreMenor.value = ""
                                viewModel.apellidosMenor.value = ""
                                viewModel.diaMenor.value = ""
                                viewModel.mesMenor.value = ""
                                viewModel.anioMenor.value = ""
                            }
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.tertiary
                    )
                    )
                    Text(
                        text = "Esta cuenta es para un menor de 14 años",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // --- CAMPOS CONDICIONALES DEL MENOR ---
                if (esMenor) {
                    FilaRegistro(
                        label = "Nombre del niño/a",
                        value = nombreMenor,
                        placeholder = "Ej. Leo"
                    ) { viewModel.nombreMenor.value = it }

                    FilaRegistro(
                        label = "Apellidos niño/a",
                        value = apellidosMenor,
                        placeholder = "Ej. Pérez"
                    ) { viewModel.apellidosMenor.value = it }

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("Fecha de nacimiento niño/a", fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CampoFecha(
                                label = "Día",
                                opciones = dias,
                                seleccionado = diaMenor,
                                modifier = Modifier.weight(1f),
                                onValueChange = { opcion, index -> viewModel.diaMenor.value = opcion }
                            )
                            CampoFecha(
                                label = "Mes",
                                opciones = dayListMenor,
                                seleccionado = mesMenor,
                                modifier = Modifier.weight(1.2f),
                                onValueChange = { opcion, index ->
                                    val result = dayPerMonthFunction(index)
                                    viewModel.mesMenor.value = opcion
                                    if (diaMenor.isNotEmpty() && result.first < diaMenor.toInt()) {
                                        viewModel.diaMenor.value = result.first.toString()
                                    }
                                    dayListMenor = result.second
                                }
                            )
                            CampoFecha(
                                label = "Año",
                                opciones = aniosMenor,
                                seleccionado = anioMenor,
                                modifier = Modifier.weight(1.3f),
                                onValueChange = { opcion, index -> viewModel.anioMenor.value = opcion },
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // --- BOTÓN SIGUIENTE ---
                Button(
                    onClick = { controller(StateNavigate.registroPass.value) },
                    enabled = isNextButtonEnabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Siguiente", fontWeight = FontWeight.Bold)
                }

                // --- LINK A LOGIN ---
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "¿Ya tienes cuenta? ",
                        modifier = Modifier.clickable { controller(StateNavigate.login.value) })
                    Text(
                        text = "Iniciar sesión",
                        color = MaterialTheme.colorScheme.tertiary, // color azul establecido
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { controller(StateNavigate.login.value) }
                    )
                }
            }
        }
    }
}

// Creamos composable para cada registro
@Composable
fun FilaRegistro(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        if(label == "Teléfono") {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(placeholder) },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        } else {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(placeholder) },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoFecha(
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
                        if (label == "Mes") {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoDesplegableGimnasios(
    label: String,
    opciones: List<String>,
    seleccionado: String,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 4.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            Row(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .height(55.dp) // Altura estándar para campos de texto
                    .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = seleccionado.ifEmpty { "Selecciona un centro" })
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Gray)
            }

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                opciones.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            onValueChange(opcion)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoDesplegableProfesores(
    label: String,
    opciones: List<String>,
    seleccionados: Set<String>,
    enabled: Boolean,
    onSelectionChange: (Set<String>) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 4.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            // Solo abrimos el menú si está habilitado
            onExpandedChange = { if (enabled) expanded = !expanded }
        ) {
            Row(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .height(55.dp)
                    // Si está deshabilitado, lo ponemos gris clarito para dar feedback visual
                    .border(
                        1.dp,
                        if (enabled) Color.Gray else Color.LightGray,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val textoMostrar =
                    if (seleccionados.isEmpty()) "Selecciona profesor/es" else seleccionados.joinToString(
                        ", "
                    )
                Text(
                    text = textoMostrar,
                    maxLines = 1,
                    color = if (!enabled) Color.LightGray else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f) // Para que el texto largo no empuje a la flecha
                )
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = if (enabled) Color.Gray else Color.LightGray
                )
            }

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                opciones.forEach { opcion ->
                    val isChecked = seleccionados.contains(opcion)
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = null // Lo manejamos en el onClick del menú
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(opcion)
                            }
                        },
                        onClick = {
                            // Si ya estaba, lo quitamos. Si no estaba, lo añadimos.
                            val nuevosSeleccionados = if (isChecked) {
                                seleccionados - opcion
                            } else {
                                seleccionados + opcion
                            }
                            onSelectionChange(nuevosSeleccionados)
                            // NO ponemos expanded = false para que puedan seguir seleccionando varios
                        }
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun RegistroInfopreviw() {
    ProyectoFinalTheme {
        RegistroInfo(controller = {})
    }
}