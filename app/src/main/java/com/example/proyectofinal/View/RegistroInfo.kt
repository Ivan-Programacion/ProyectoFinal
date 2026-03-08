package com.example.proyectofinal.View

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectofinal.Logic.anios
import com.example.proyectofinal.Logic.dias
import com.example.proyectofinal.Logic.meses
import com.example.proyectofinal.ViewModel.App
import com.example.proyectofinal.ViewModel.StateNavigate
import com.example.proyectofinal.ui.theme.ProyectoFinalTheme

@Composable
fun RegistroInfo(paddingValues: PaddingValues = PaddingValues(), controller: (String) -> Unit) {
    // Estados para el formulario provisional
    var nombre by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }

    // Estados para la fecha de nacimiento
    var dia by remember { mutableStateOf("") }
    var mes by remember { mutableStateOf("") }
    var anio by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize(),
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
                Text(
                    text = "Crear cuenta",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.Start)
                )

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
                            opciones = dias,
                            seleccionado = dia,
                            modifier = Modifier.weight(1f),
                            onValueChange = { dia = it }
                        )
                        CampoFecha(
                            label = "Mes",
                            opciones = meses,
                            seleccionado = mes,
                            modifier = Modifier.weight(1.2f),
                            onValueChange = { mes = it }
                        )
                        CampoFecha(
                            label = "Año",
                            opciones = anios,
                            seleccionado = anio,
                            modifier = Modifier.weight(1.3f),
                            onValueChange = { anio = it }
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

                Spacer(modifier = Modifier.height(8.dp))

                // --- BOTÓN SIGUIENTE ---
                Button(
                    onClick = { controller(StateNavigate.registroPass.value) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D0C03))
                ) {
                    Text("Siguiente", fontWeight = FontWeight.Bold, fontSize = 18.sp)
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
    onValueChange: (String) -> Unit
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
                .background(Color.White)
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

@Preview(showBackground = true)
@Composable
fun RegistroInfopreviw() {
    ProyectoFinalTheme {
        RegistroInfo(controller = {})
    }
}