package com.example.proyectofinal.View

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
                            value = dia,
                            placeholder = "Día",
                            modifier = Modifier.weight(1f)
                        ) { dia = it }
                        CampoFecha(
                            value = mes,
                            placeholder = "Mes",
                            modifier = Modifier.weight(1f)
                        ) { mes = it }
                        CampoFecha(
                            value = anio,
                            placeholder = "Año",
                            modifier = Modifier.weight(1.2f)
                        ) { anio = it }
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
                    onClick = { /* Lógica de registro */ },
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
                        modifier = Modifier.clickable { controller("login") })
                    Text(
                        text = "Iniciar sesión",
                        color = MaterialTheme.colorScheme.tertiary, // color azul establecido
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { controller("login") }
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

// Creamos composable para cada valor del campo fecha
@Composable
fun CampoFecha(
    value: String,
    placeholder: String,
    modifier: Modifier,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = { if (it.length <= 4) onValueChange(it) },
        modifier = modifier,
        placeholder = { Text(placeholder) },
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}