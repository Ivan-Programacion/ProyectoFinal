package com.example.proyectofinal.View

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyectofinal.Logic.anios
import com.example.proyectofinal.Logic.dias
import com.example.proyectofinal.Logic.meses
import com.example.proyectofinal.Logic.dayPerMonthFunction
import com.example.proyectofinal.ViewModel.StateNavigate
import com.example.proyectofinal.ui.theme.ProyectoFinalTheme

@Composable
fun RegistroInfo(paddingValues: PaddingValues = PaddingValues(), controller: (String) -> Unit) {
    // ********************************* LÓGICA PROVISIONA ********************************* //
    // Estados para el formulario provisional
    var nombre by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }

    // Estados para la fecha de nacimiento
    var dia by remember { mutableStateOf("") }
    var mes by remember { mutableStateOf("") }
    var anio by remember { mutableStateOf("") }

    // Remember de la lista de dias que cambiara en función del mes seleccionado
    var dayList by remember { mutableStateOf(dias) }
    var dayListMenor by remember { mutableStateOf(dias) }

    // Estados para la cuenta del menor
    var esMenor by remember { mutableStateOf(false) }
    var nombreMenor by remember { mutableStateOf("") }
    var apellidosMenor by remember { mutableStateOf("") }
    var diaMenor by remember { mutableStateOf("") }
    var mesMenor by remember { mutableStateOf("") }
    var anioMenor by remember { mutableStateOf("") }
// ********************************* LÓGICA PROVISIONA ********************************* //
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
                    nombre = it
                }
                FilaRegistro(
                    label = "Apellidos",
                    value = apellidos,
                    placeholder = "Ej. Pérez"
                ) { apellidos = it }

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
                                dia = opcion
                            }
                        )
                        CampoFecha(
                            label = "Mes",
                            opciones = meses,
                            seleccionado = mes,
                            modifier = Modifier.weight(1.2f),
                            onValueChange = { opcion, index ->
                                val result = dayPerMonthFunction(index)
                                mes = opcion
                                if(result.first < dia.toInt()) {
                                    dia = result.first.toString()
                                }
                                dayList = result.second
                            }
                        )
                        CampoFecha(
                            label = "Año",
                            opciones = anios,
                            seleccionado = anio,
                            modifier = Modifier.weight(1.3f),
                            onValueChange = { opcion, index -> anio = opcion }
                        )
                    }
                }

                // --- CAMPOS DE TEXTO ---
                FilaRegistro(
                    label = "Email",
                    value = email,
                    placeholder = "ej. correo@dominio.com"
                ) { email = it }
                FilaRegistro(
                    label = "Teléfono",
                    value = telefono,
                    placeholder = "612345678"
                ) { telefono = it }

                // --- CHECKBOX MENOR DE EDAD ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { // Hacemos toda la fila clickable para mayor comodidad
                            esMenor = !esMenor
                            if (!esMenor) { // Si se desmarca, limpiamos los datos
                                nombreMenor = ""
                                apellidosMenor = ""
                                diaMenor = ""
                                mesMenor = ""
                                anioMenor = ""
                            }
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
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
                    ) { nombreMenor = it }

                    FilaRegistro(
                        label = "Apellidos niño/a",
                        value = apellidosMenor,
                        placeholder = "Ej. Pérez"
                    ) { apellidosMenor = it }

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
                                onValueChange = { opcion, index -> diaMenor = opcion }
                            )
                            CampoFecha(
                                label = "Mes",
                                opciones = dayListMenor,
                                seleccionado = mesMenor,
                                modifier = Modifier.weight(1.2f),
                                onValueChange = { opcion, index ->
                                    val result = dayPerMonthFunction(index)
                                    mesMenor = opcion
                                    if(result.first < diaMenor.toInt()) {
                                        diaMenor = result.first.toString()
                                    }
                                    dayListMenor = result.second
                                }
                            )
                            CampoFecha(
                                label = "Año",
                                opciones = anios,
                                seleccionado = anioMenor,
                                modifier = Modifier.weight(1.3f),
                                onValueChange = { opcion, index -> anioMenor = opcion },
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // --- BOTÓN SIGUIENTE ---
                Button(
                    onClick = { controller(StateNavigate.registroPass.value) },
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

@Preview(showBackground = true)
@Composable
fun RegistroInfopreviw() {
    ProyectoFinalTheme {
        RegistroInfo(controller = {})
    }
}