package com.example.proyectofinal.View

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.proyectofinal.R
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectofinal.Logic.removeFcmTokenOnLogout
import com.example.proyectofinal.ViewModel.AuthUiState
import com.example.proyectofinal.ViewModel.AuthViewModel
import com.example.proyectofinal.ViewModel.StateNavigate
import com.example.proyectofinal.ui.theme.ProyectoFinalTheme

@SuppressLint("ContextCastToActivity")
@Composable
fun Perfil(
    paddingValues: PaddingValues = PaddingValues(),
    viewModel: AuthViewModel = viewModel(),
    controller: (String) -> Unit
) {
    // 1. Recolectamos el usuario real (Reactivo)
    val currentUser by viewModel.currentUserState.collectAsStateWithLifecycle()
    val currentExam by viewModel.currentExamState.collectAsStateWithLifecycle()
    val authUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoading = authUiState is AuthUiState.Loading

    // 2. Inicializamos los campos con los datos del usuario real
    // Utilizamos `remember(currentUser)` para que, si el flujo de base de datos se actualiza (ej. tras arrancar o modificar),
    // también rellene los campos de texto locales con los datos de Firebase.
    var nombre by remember(currentUser) { mutableStateOf(currentUser?.name ?: "") }
    var apellidos by remember(currentUser) { mutableStateOf(currentUser?.lastName ?: "") }
    var telefono by remember(currentUser) { mutableStateOf(currentUser?.phone ?: "") }
    val email = currentUser?.email ?: ""
    val examStatus = currentUser?.examStatus ?: "NONE"
    val examText = currentUser?.examText ?: ""
    val isActive = currentUser?.isActive ?: false

    // ESTADO PARA EL DIALOG
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    // --- NUEVOS ESTADOS COMPROBANDO EXAMEN REAL ---
    val estadoExamenGlobal = currentExam?.currentStatus ?: "CLOSED"
    var showSolicitarExamenDialog by remember { mutableStateOf(false) }

    // Lógica del Dialog de Confirmación
    if (showConfirmDialog) {
        ConfirmarCambiosDialog(
            isLoading = isLoading,
            onConfirm = {
                // Ejecutamos la lógica de actualización en Firebase
                viewModel.updateUserProfile(nombre, apellidos, telefono) {
                    showConfirmDialog = false
                }
            },
            onDismiss = { if (!isLoading) showConfirmDialog = false }
        )
    }
    // Logicca deñ Dialog de Cerrar Sesión
    if (showLogoutDialog) {
        CerrarSesionDialog(
            onConfirm = {
                showLogoutDialog = false
                // Vaciamos campo fcmToken y cerramos sesión
                removeFcmTokenOnLogout(currentUser?.id) {
                    controller(StateNavigate.login.value)
                    viewModel.logoutUser()
                    viewModel.resetUiState()
                }
            },
            onDismiss = { showLogoutDialog = false }
        )
    }
    // Lógica del Dialog de Solicitar Examen
    if (showSolicitarExamenDialog) {
        SolicitarExamenDialog(
            onConfirm = {
                showSolicitarExamenDialog = false
                viewModel.requestExam()
            },
            onDismiss = { showSolicitarExamenDialog = false }
        )
    }

    // Interceptamos el botón de atrás
    BackHandler {
        // Volvemos a la pantalla principal ListaCinturones
        controller(StateNavigate.listaCinturones.value)
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {
        // --- CARD DATOS PERFIL ---
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
                    Text(text = stringResource(R.string.perfil_datos_titulo), style = MaterialTheme.typography.titleMedium)

                    // AVATAR
                    Surface(
                        modifier = Modifier.size(100.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.onSecondary
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = if (nombre.isNotEmpty()) nombre.take(1).uppercase() else "?",
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    CampoPerfil(label = stringResource(R.string.perfil_nombre_label), value = nombre, onValueChange = { nombre = it })
                    CampoPerfil(
                        label = stringResource(R.string.perfil_apellidos_label),
                        value = apellidos,
                        onValueChange = { apellidos = it })
                    CampoPerfil(
                        label = stringResource(R.string.perfil_telefono_label),
                        value = telefono,
                        onValueChange = { telefono = it },
                        isPhone = true)
                    CampoPerfil(label = stringResource(R.string.perfil_email_label), value = email, onValueChange = {}, enabled = false)

                    Spacer(modifier = Modifier.height(8.dp))

                    // BOTÓN ACTUALIZAR
                    Button(
                        onClick = { showConfirmDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D0C03))
                    ) {
                        Text(stringResource(R.string.perfil_actualizar_btn), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }
            }
        }
        // --- CARD SOLICITAR EXAMEN ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 12.dp,
                        end = 12.dp,
                        bottom = 16.dp
                    ), // Margen inferior para separarlo de Cerrar Sesión
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.perfil_solicitar_examen_titulo),
                        style = MaterialTheme.typography.titleMedium,
                    )

                    // Lógica para el mensaje informativo extraída al ViewModel
                    val mensajeInformativo = viewModel.getMensajeInformativoExamen(
                        context = LocalContext.current, // Para poder traducir los mensajes
                        isActive = isActive,
                        examStatus = examStatus,
                        examText = examText,
                        estadoExamenGlobal = estadoExamenGlobal,
                        infoMessage = currentExam?.infoMessage
                    )

                    // Card interior o recuadro para resaltar el mensaje del profesor
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.onSecondary
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = mensajeInformativo,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Botón para acceder
                    val botonHabilitado =
                        isActive && estadoExamenGlobal == "OPEN_REQUESTS" && examStatus == "NONE"

                    Button(
                        onClick = { showSolicitarExamenDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        enabled = botonHabilitado,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            disabledContainerColor = Color.LightGray
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.perfil_acceder_examen_btn),
                            fontWeight = if (botonHabilitado) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 18.sp,
                            color = if (botonHabilitado) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
        // --- 2. SECCIÓN DE CERRAR SESIÓN ---
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = stringResource(R.string.perfil_salir_desc),
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { }
                )
                Spacer(
                    modifier = Modifier
                        .width(8.dp)
                )
                Text(
                    modifier = Modifier.clickable { showLogoutDialog = true },
                    text = stringResource(R.string.perfil_cerrar_sesion_texto),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun SolicitarExamenDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
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
                    text = stringResource(R.string.perfil_solicitar_acceso_dialog_titulo),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.perfil_solicitar_acceso_dialog_desc),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Botón Aceptar
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text(
                            stringResource(R.string.perfil_aceptar_btn),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    // Botón Cancelar
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            stringResource(R.string.perfil_cancelar_btn),
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

@Composable
fun ConfirmarCambiosDialog(
    isLoading: Boolean = false,
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
                    text = stringResource(R.string.perfil_guardar_cambios_dialog_titulo),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.perfil_guardar_cambios_dialog_desc),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    // Botón Aceptar
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                stringResource(R.string.perfil_aceptar_btn),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    // Botón Cancelar
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            stringResource(R.string.perfil_cancelar_btn),
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

@Composable
fun CampoPerfil(
    label: String, value: String, onValueChange: (String) -> Unit, enabled: Boolean = true, isPhone: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp),
        )
        if (isPhone) {
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
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        } else {
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
                ),
            )
        }
    }
}

@Composable
fun CerrarSesionDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
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
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.perfil_cerrar_sesion_dialog_titulo),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.perfil_cerrar_sesion_dialog_desc),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Botón Salir (Rojo/Error para indicar acción destructiva)
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            stringResource(R.string.perfil_salir_btn),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    // Botón Cancelar (Gris)
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            stringResource(R.string.perfil_cancelar_btn),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Perfilpreview() {
    ProyectoFinalTheme {
        Perfil() {}
    }
}
