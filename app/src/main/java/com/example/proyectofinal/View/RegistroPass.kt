package com.example.proyectofinal.View

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectofinal.ViewModel.StateNavigate

@Composable
fun RegistroPass(paddingValues: PaddingValues = PaddingValues(), controller: (String) -> Unit) {
    // Estados para las contraseñas
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    var aceptoTerminos by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize(),
            //.background(Color(0xFF8D342A)), // Mismo fondo que RegistroInfo
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Título alineado a la izquierda para mantener simetría
                Text(
                    text = "Crear cuenta",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.Start)
                )

                // --- CAMPO CONTRASEÑA ---
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Contraseña",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Introduce una contraseña") },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                    // Texto de condiciones (basado en tu imagen)
                    Text(
                        text = "Debe tener al menos 8 caracteres, combinando letras y números.",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // --- CAMPO REPETIR CONTRASEÑA ---
                FilaRegistroPassword(
                    label = "Confirmar contraseña",
                    value = repeatPassword,
                    placeholder = "Repite la contraseña"
                ) { repeatPassword = it }

                // --- CHECKBOX TÉRMINOS Y CONDICIONES ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = aceptoTerminos,
                        onCheckedChange = { aceptoTerminos = it },
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF2D0C03))
                    )
                    Text(text = "Acepto los ", fontSize = 14.sp)
                    Text(
                        text = "Términos y Condiciones",
                        fontSize = 14.sp,
                        color = Color.Blue, // O el color tertiary que uses
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { /* Abrir TyC */ }
                    )
                }

                // --- BOTÓN CREAR CUENTA ---
                Button(
                    onClick = { /* Lógica final de registro */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D0C03)),
                    enabled = aceptoTerminos && password.isNotEmpty() // Validación básica
                ) {
                    Text("Crear cuenta", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }

                // --- LINK A LOGIN ---
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("¿Ya tienes cuenta? ", fontSize = 14.sp)
                    Text(
                        text = "Iniciar sesión",
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { controller(StateNavigate.login.value) }
                    )
                }
            }
        }
    }
}

@Composable
fun FilaRegistroPassword(
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
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
    }
}