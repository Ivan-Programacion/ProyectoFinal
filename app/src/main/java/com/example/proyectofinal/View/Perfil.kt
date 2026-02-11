package com.example.proyectofinal.View

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Perfil(paddingValues: PaddingValues = PaddingValues()) {
    // Estados para los campos editables
    // ESTO SE CAMBIARÁ POR UNA DATA CLASS CON LA API
    var nombre by remember { mutableStateOf("Juan") }
    var apellidos by remember { mutableStateOf("Pérez") }
    var telefono by remember { mutableStateOf("600000000") }
    val email = "juan.perez@email.com" // Fijo siempre

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF8D342A))
            .padding(paddingValues)
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 12.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()) // Por si hay pantallas pequeñas
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // --- AVATAR ---
                Surface(
                    modifier = Modifier.size(100.dp),
                    shape = CircleShape,
                    color = Color(0xFFE6CCCA) // Tono rosáceo de tu imagen
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = nombre.take(1).uppercase(),
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2D0C03)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // --- FORMULARIO ---
                CampoPerfil(label = "Nombre", value = nombre, onValueChange = { nombre = it })
                CampoPerfil(label = "Apellidos", value = apellidos, onValueChange = { apellidos = it })
                CampoPerfil(label = "Teléfono", value = telefono, onValueChange = { telefono = it })

                // Email deshabilitado
                CampoPerfil(
                    label = "Email",
                    value = email,
                    onValueChange = {},
                    enabled = false
                )

                Spacer(modifier = Modifier.height(8.dp))

                // --- BOTÓN ACTUALIZAR ---
                Button(
                    onClick = { /* Acción actualizar */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2D0C03) // Marrón oscuro
                    )
                ) {
                    Text("Actualizar", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }

                // --- INFO PAGOS ---
                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = "Último pago: 79€", fontWeight = FontWeight.Medium)
                    Text(text = "Tipo de pago: Mensual", fontWeight = FontWeight.Medium)
                    Text(
                        text = "Próximo pago: 10/03/2026",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8D342A) // Resaltamos la fecha con tu rojo
                    )
                }
            }
        }
    }
}

@Composable
fun CampoPerfil(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp),
            color = if (enabled) Color.Unspecified else Color.Gray
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                disabledContainerColor = Color(0xFFE9E9E9), // Gris claro para el email
                disabledTextColor = Color.DarkGray,
                focusedBorderColor = Color(0xFF8D342A),
                unfocusedBorderColor = Color.LightGray
            )
        )
    }
}