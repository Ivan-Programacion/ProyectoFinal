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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectofinal.ViewModel.AuthUiState
import com.example.proyectofinal.ViewModel.AuthViewModel
import com.example.proyectofinal.ViewModel.StateNavigate
import com.example.proyectofinal.ui.theme.ProyectoFinalTheme

@Composable
fun RegistroPass(
    paddingValues: PaddingValues = PaddingValues(),
    viewModel: AuthViewModel = viewModel(),
    onBack: () -> Unit,
    controller: (String) -> Unit
) {
    // Estados para las contraseñas
    val password by viewModel.registroPassword.collectAsStateWithLifecycle()
    val repeatPassword by viewModel.registroRepeatPassword.collectAsStateWithLifecycle()
    val aceptoTerminos by viewModel.registroAceptoTerminos.collectAsStateWithLifecycle()

    val passwordVisible by viewModel.registroPasswordVisible.collectAsStateWithLifecycle()
    val repeatPasswordVisible by viewModel.registroRepeatPasswordVisible.collectAsStateWithLifecycle()

    // Para poder ver el estado de la petición de registro y saber si pasar o no a la siguiene pantalla
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            viewModel.resetUiState()
            controller(StateNavigate.listaCinturones.value)
        }
    }

    // ESTADO PARA EL DIALOG
    val showDialog by viewModel.showDialogTerminos.collectAsStateWithLifecycle()

    // Lógica del Dialog
    if (showDialog) {
        TerminosCondiciones(onDismiss = { viewModel.showDialogTerminos.value = false })
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
                    IconButton(onClick = {
                        onBack()
                        viewModel.resetRegisterPassViewModelsStates()
                    }) {
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
                        onValueChange = { viewModel.registroPassword.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Contraseña") },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            val image =
                                if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = {
                                viewModel.registroPasswordVisible.value = !passwordVisible
                            }) {
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
                    onToggleVisibility = {
                        viewModel.registroRepeatPasswordVisible.value = !repeatPasswordVisible
                    },
                ) { viewModel.registroRepeatPassword.value = it }

                // --- CHECKBOX TÉRMINOS Y CONDICIONES ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = aceptoTerminos,
                        onCheckedChange = { viewModel.registroAceptoTerminos.value = it },
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF2D0C03))
                    )
                    Text(text = "Aceptar ", style = MaterialTheme.typography.bodySmall)
                    Text(
                        text = "Términos Condiciones",
                        color = MaterialTheme.colorScheme.tertiary,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        // Si quiere ver los terminos, salta el Dialog
                        modifier = Modifier.clickable { viewModel.showDialogTerminos.value = true }
                    )
                }

                // --- BOTÓN CREAR CUENTA ---
                Button(
                    onClick = {
                        viewModel.registerWithCurrentForm()
                    },
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
                        modifier = Modifier.clickable {
                            controller(StateNavigate.login.value)
                            viewModel.resetRegisterPassViewModelsStates()
                        })
                    Text(
                        text = "Iniciar sesión",
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            controller(StateNavigate.login.value)
                            viewModel.resetRegisterPassViewModelsStates()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TerminosCondiciones(onDismiss: () -> Unit) {
    val termsText = "1. NATURALEZA DEL PROYECTO\n\n" +
            "Esta aplicación, Arkenpo App, ha sido desarrollada exclusivamente con fines educativos " +
            "y como parte del Trabajo Final del Ciclo Superior de Desarrollode Aplicaciones Multiplataforma. " +
            "No tiene fines comerciales ni carácter de empresa constituida. El contenido y las funcionalidades " +
            "son una simulación de un entorno real de gestión de escuelas de artes marciales.\n\n" +
            "2. USO DE LOS DATOS\n\n" +
            "Toda la información personal recopilada (nombre, email, teléfono, datos de menores) será utilizada " +
            "estrictamente para el funcionamiento interno de la aplicación en el marco del proyecto académico. " +
            "Nos comprometemos a:\n" +
            "- No ceder ni vender tus datos a terceros.\n" +
            "- Utilizar la información únicamente para la evaluación de las capacidades técnicas del software.\n" +
            "- Eliminar la base de datos una vez finalizada la etapa de evaluación del proyecto si así se requiere.\n\n" +
            "3. PROTECCIÓN DE DATOS Y FIREBASE\n\n" +
            "Los datos son almacenados en la infraestructura de Google Firebase, cumpliendo con los estándares de " +
            "seguridad de dicha plataforma. Sin embargo, al ser una versión de desarrollo (Beta), el usuario reconoce " +
            "que no se garantiza la seguridad infalible contra brechas de datos de nivel profesional. Se recomienda no " +
            "utilizar contraseñas que el usuario emplee en servicios sensibles (banca, correo personal principal, etc.).\n\n" +
            "4. REQUSITO DE EDAD\n\n" +
            "Al aceptar estos términos, confirmas que tienes al menos 14 años de edad (o 13 según la legislación " +
            "aplicable) o que, en su defecto, estás realizando este registro bajo la supervisión directa de un " +
            "tutor legal. En el caso de registrar a un menor de 14 años, el usuario garantiza que posee la patria " +
            "potestad o tutoría legal necesaria para ceder dichos datos con fines educativos.\n\n" +
            "5. LIMITACIONES DE RESPONSABILIDAD\n\n" +
            "Dado que es un software en desarrollo, el autor no se hace responsable de fallos técnicos, pérdida " +
            "de datos o malentendidos derivados del contenido pedagógico mostrado en la aplicación."
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
                    text = termsText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
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
        RegistroPass(onBack = {}, controller = {})
    }
}