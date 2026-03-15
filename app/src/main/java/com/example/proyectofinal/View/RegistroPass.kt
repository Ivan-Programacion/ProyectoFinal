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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.proyectofinal.ViewModel.StateNavigate
import com.example.proyectofinal.ui.theme.ProyectoFinalTheme

@Composable
fun RegistroPass(paddingValues: PaddingValues = PaddingValues(), controller: (String) -> Unit) {
    // Estados para las contraseñas
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    var aceptoTerminos by remember { mutableStateOf(false) }

    var passwordVisible by remember { mutableStateOf(false) }
    var repeatPasswordVisible by remember { mutableStateOf(false) }

    // ESTADO PARA EL DIALOG
    var showDialog by remember { mutableStateOf(false) }

    // Lógica del Dialog
    if (showDialog) {
        TerminosCondiciones(onDismiss = { showDialog = false })
    }
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
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { controller(StateNavigate.registro.value) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver atrás",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    // Título alineado a la izquierda para mantener simetría
                    Text(
                        text = "Crear cuenta",
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
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
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            val image =
                                if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = image,
                                    contentDescription = "Toggle password visibility"
                                )
                            }
                        }
                    )
                    // Texto de condiciones (basado en tu imagen)
                    Text(
                        text = "Debe tener al menos 8 caracteres, combinando letras y números.",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // --- CAMPO REPETIR CONTRASEÑA ---
                FilaRegistroPassword(
                    label = "Confirmar contraseña",
                    value = repeatPassword,
                    placeholder = "Repite la contraseña",
                    isVisible = repeatPasswordVisible,
                    onToggleVisibility = { repeatPasswordVisible = !repeatPasswordVisible },
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
                    Text(text = "Acepto los ", style = MaterialTheme.typography.bodySmall)
                    Text(
                        text = "Términos y Condiciones",
                        color = MaterialTheme.colorScheme.tertiary,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        // Si quiere ver los terminos, salta el Dialog
                        modifier = Modifier.clickable { showDialog = true }
                    )
                }

                // --- BOTÓN CREAR CUENTA ---
                Button(
                    onClick = { /* Lógica final de registro */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = aceptoTerminos && password.isNotEmpty() // Validación básica
                ) {
                    Text("Crear cuenta", fontWeight = FontWeight.Bold)
                }

                // --- LINK A LOGIN ---
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "¿Ya tienes cuenta? ",
                        modifier = Modifier.clickable { controller(StateNavigate.login.value) })
                    Text(
                        text = "Iniciar sesión",
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { controller(StateNavigate.login.value) }
                    )
                }
            }
        }
    }
}

@Composable
fun TerminosCondiciones(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // --- CABECERA MEJORADA ---
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Términos y Condiciones",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2D0C03),
                        // Añadimos padding al final para que el texto nunca toque la 'x'
                        modifier = Modifier
                            .padding(end = 40.dp)
                            .align(Alignment.CenterStart)
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            // Ajustamos un poco la posición para que no esté tan pegada al borde
                            .offset(x = 12.dp, y = (-12).dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = Color(0xFF2D0C03),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- CUERPO DEL TEXTO ---
                Text(
                    text = "Este es un texto de ejemplo para mostrar cómo se verían los términos y " +
                            "condiciones en un modal flotante sobre la pantalla de Contraseña. " +
                            "Aquí se incluirá toda la información legal completa.\n\nEl usuario " +
                            "podrá leer, hacer scroll y cerrar el modal sin salir de la pantalla " +
                            "principal de registro, manteniendo los datos ya introducidos.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Texto adicional para probar el scroll
                Text(
                    text = "Al aceptar estos términos, el usuario confirma que es mayor de edad y " +
                            "que los datos proporcionados son verídicos. Esta aplicación se reserva " +
                            "el derecho de modificar estas condiciones en cualquier momento previo aviso.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun FilaRegistroPassword(
    label: String,
    value: String,
    placeholder: String,
    isVisible: Boolean,
    onToggleVisibility: () -> Unit,
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
            visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (isVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = onToggleVisibility) {
                    Icon(imageVector = image, contentDescription = "Toggle password visibility")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RegistroPasspreview() {
    ProyectoFinalTheme {
        RegistroPass(controller = {})
    }
}