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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectofinal.Model.User
import com.example.proyectofinal.R
import com.example.proyectofinal.ViewModel.AdminGestionExamenViewModel
import com.example.proyectofinal.ViewModel.AdminPerfilClienteViewModel
import com.example.proyectofinal.ViewModel.AuthUiState
import com.example.proyectofinal.ViewModel.StateNavigate
import com.example.proyectofinal.ui.theme.ProyectoFinalTheme

@Composable
fun AdminGestionExamen(
    paddingValues: PaddingValues = PaddingValues(),
    viewModel: AdminGestionExamenViewModel = viewModel(),
    adminPerfilViewModel: AdminPerfilClienteViewModel = viewModel(),
    controller: (String) -> Unit
) {
    // Contexto de la aplicacion necesaria para comprobar el estado de conexión del dispositivo
    val context = LocalContext.current
    // --- ESTADOS DEL VIEWMODEL ---
    val authUiState by viewModel.authUiState.collectAsStateWithLifecycle()
    val isLoading = authUiState is AuthUiState.Loading

    // --- ESTADOS REACTIVOS DEL VIEWMODEL ---
    val currentExam by viewModel.currentExam.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val ordenAscendente by viewModel.ordenAscendente.collectAsStateWithLifecycle()
    val listaAlumnos by viewModel.filteredStudents.collectAsStateWithLifecycle()
    val listaCinturones by viewModel.listaCinturones.collectAsStateWithLifecycle()
    val estadoExamen = currentExam.currentStatus

    // --- ESTADOS DE LA UI ---
    // Estados para los Dialogs Individuales
    var showAprobarIndividualDialog by remember { mutableStateOf(false) }
    var showRechazarIndividualDialog by remember { mutableStateOf(false) }

    // Estado para guardar a qué alumno le hemos pulsado el botón
    var alumnoSeleccionado by remember { mutableStateOf<User?>(null) }

    // Estado para controlar si el menú desplegable está abierto o cerrado
    var expandedMenuFiltro by remember { mutableStateOf(false) }

    // Estados de los Dialogs Globales
    var showCancelarDialog by remember { mutableStateOf(false) }
    var showRealizarDialog by remember { mutableStateOf(false) }
    var showAprobarDialog by remember { mutableStateOf(false) }

    // Para vaciar el campo de busqueda y filtro de orden al volver a la pantalla.
    // Se necesita hacer con LifecycleOwner ya que utilizamos datos reactivos
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                viewModel.onSearchQueryChanged("")
                viewModel.toggleOrdenAscendente(true)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // --- DIALOGS ---
    if (showCancelarDialog) {
        DialogAccionExamen(
            titulo = stringResource(R.string.admin_exam_cancel_title),
            descripcion = stringResource(R.string.admin_exam_cancel_desc),
            textoAceptar = stringResource(R.string.admin_exam_cancel_btn),
            textoCancelar = stringResource(R.string.admin_exam_continue_btn),
            colorAceptar = MaterialTheme.colorScheme.error,
            colorCancelar = Color.LightGray,
            colorTextoCancelar = MaterialTheme.colorScheme.primary,
            mostrarTextArea = false,
            onAceptar = { _ ->
                showCancelarDialog = false
                viewModel.cancelExam()
            },
            onCancelar = { showCancelarDialog = false },
        )
    }

    if (showRealizarDialog) {
        DialogAccionExamen(
            titulo = stringResource(R.string.admin_exam_start_requests_title),
            descripcion = stringResource(R.string.admin_exam_start_requests_desc),
            textoAceptar = stringResource(R.string.perfil_aceptar_btn),
            textoCancelar = stringResource(R.string.perfil_cancelar_btn),
            colorAceptar = MaterialTheme.colorScheme.tertiary,
            colorCancelar = Color.LightGray,
            colorTextoCancelar = MaterialTheme.colorScheme.primary,
            mostrarTextArea = true,
            onAceptar = { mensajeOpcional ->
                showRealizarDialog = false
                viewModel.startOpenRequests(mensajeOpcional)
            },
            onCancelar = { showRealizarDialog = false }
        )
    }

    if (showAprobarDialog) {
        DialogAccionExamen(
            titulo = if (estadoExamen == "OPEN_REQUESTS") stringResource(R.string.admin_exam_start_exam_title) else stringResource(R.string.admin_exam_finish_exam_title),
            descripcion =
                if (estadoExamen == "OPEN_REQUESTS")
                    stringResource(R.string.admin_exam_start_exam_desc)
                else stringResource(R.string.admin_exam_finish_exam_desc),
            textoAceptar = stringResource(R.string.perfil_aceptar_btn),
            textoCancelar = stringResource(R.string.perfil_cancelar_btn),
            isLoading = isLoading,
            colorAceptar = MaterialTheme.colorScheme.tertiary,
            colorCancelar = Color.LightGray,
            colorTextoCancelar = MaterialTheme.colorScheme.primary,
            mostrarTextArea = false,
            onAceptar = { _ ->
                if (estadoExamen == "OPEN_REQUESTS") {
                    viewModel.startInProgress(context) { showAprobarDialog = false }
                } else {
                    viewModel.finishExam(context) { showAprobarDialog = false }
                }
            },
            onCancelar = { showAprobarDialog = false },
        )
    }

    // --- DIALOGS INDIVIDUALES ---
    if (showAprobarIndividualDialog && alumnoSeleccionado != null) {
        val accionText =
            if (estadoExamen == "OPEN_REQUESTS") stringResource(R.string.admin_exam_accept_request_title) else stringResource(R.string.admin_exam_approve_exam_title)
        val descripcionText = if (estadoExamen == "OPEN_REQUESTS")
            stringResource(R.string.admin_exam_accept_request_desc, alumnoSeleccionado?.name ?: "", alumnoSeleccionado?.lastName ?: "")
        else
            stringResource(R.string.admin_exam_approve_exam_desc, alumnoSeleccionado?.name ?: "", alumnoSeleccionado?.lastName ?: "")

        DialogAccionExamen(
            titulo = accionText,
            descripcion = descripcionText,
            textoAceptar = stringResource(R.string.perfil_aceptar_btn),
            textoCancelar = stringResource(R.string.perfil_cancelar_btn),
            colorAceptar = MaterialTheme.colorScheme.tertiary,
            colorCancelar = Color.LightGray,
            colorTextoCancelar = MaterialTheme.colorScheme.primary,
            mostrarTextArea = false,
            onAceptar = { _ ->
                if (estadoExamen == "OPEN_REQUESTS") {
                    viewModel.approveStudentRequest(alumnoSeleccionado!!.id)
                } else {
                    viewModel.passStudentExam(alumnoSeleccionado!!)
                }
                showAprobarIndividualDialog = false
                alumnoSeleccionado = null
            },
            onCancelar = {
                showAprobarIndividualDialog = false
                alumnoSeleccionado = null
            },
        )
    }

    if (showRechazarIndividualDialog && alumnoSeleccionado != null) {
        val accionText =
            if (estadoExamen == "OPEN_REQUESTS") stringResource(R.string.admin_exam_refuse_request_title) else stringResource(R.string.admin_exam_fail_exam_title)
        val descripcionText = if (estadoExamen == "OPEN_REQUESTS")
            stringResource(R.string.admin_exam_refuse_request_desc, alumnoSeleccionado?.name ?: "", alumnoSeleccionado?.lastName ?: "")
        else
            stringResource(R.string.admin_exam_fail_exam_desc, alumnoSeleccionado?.name ?: "", alumnoSeleccionado?.lastName ?: "")

        DialogAccionExamen(
            titulo = accionText,
            descripcion = descripcionText,
            textoAceptar =
                if (estadoExamen == "OPEN_REQUESTS") stringResource(R.string.admin_exam_deny_btn) else stringResource(R.string.admin_exam_yes_btn),
            textoCancelar = stringResource(R.string.perfil_cancelar_btn),
            colorAceptar = MaterialTheme.colorScheme.error, // Rojo porque es una acción destructiva/negativa
            colorCancelar = Color.LightGray,
            colorTextoCancelar = MaterialTheme.colorScheme.primary,
            mostrarTextArea = false,
            onAceptar = { _ ->
                if (estadoExamen == "OPEN_REQUESTS") {
                    viewModel.refuseStudentRequest(alumnoSeleccionado!!.id)
                } else {
                    viewModel.failStudentExam(alumnoSeleccionado!!.id)
                }
                showRechazarIndividualDialog = false
                alumnoSeleccionado = null
            },
            onCancelar = {
                showRechazarIndividualDialog = false
                alumnoSeleccionado = null
            },
        )
    }

    // --- CONTENIDO DE LA PANTALLA ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- CARD 1: ACCIONES DE EXAMEN ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = stringResource(R.string.admin_exam_actions_title),
                    style = MaterialTheme.typography.titleMedium,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    // Botón Derecho: Cancelar Examen
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { showCancelarDialog = true },
                        enabled = estadoExamen != "CLOSED",
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            disabledContainerColor = Color.LightGray
                        )
                    ) {
                        Text(
                            stringResource(R.string.admin_exam_cancel_btn),
                            color = if (estadoExamen != "CLOSED") Color.White else Color.DarkGray,
                        )
                    }

                    // Botón Izquierdo: Realizar / Aceptar / Aprobar
                    val textoBtnDerecho = when (estadoExamen) {
                        "CLOSED" -> stringResource(R.string.admin_exam_start_requests_title)
                        "OPEN_REQUESTS" -> stringResource(R.string.admin_exam_start_exam_title)
                        "IN_PROGRESS" -> stringResource(R.string.admin_exam_finish_exam_title)
                        else -> stringResource(R.string.admin_exam_start_requests_title)
                    }
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            if (estadoExamen == "CLOSED") showRealizarDialog = true
                            else showAprobarDialog = true
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(textoBtnDerecho)
                    }
                }
            }
        }

        // --- CARD 2: LISTA DE CLIENTES ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Título condicional
                val textoCondicion =
                    if (estadoExamen == "IN_PROGRESS") stringResource(R.string.admin_exam_examined_tab) else stringResource(R.string.admin_exam_requests_tab)
                Text(
                    text = textoCondicion,
                    style = MaterialTheme.typography.titleMedium,
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Fila del Buscador + Filtro Orden
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.onSearchQueryChanged(it) },
                        modifier = Modifier.weight(1f),
                        placeholder = {
                            Text(
                                stringResource(R.string.admin_exam_search_placeholder),
                                style = MaterialTheme.typography.bodySmall
                            )
                        },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = stringResource(R.string.admin_list_search_desc)) },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                    )

                    // Box actúa como ancla para que el DropdownMenu sepa dónde aparecer
                    Box {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(50.dp),
                            onClick = { expandedMenuFiltro = true }
                        ) {
                            Icon(
                                // Puedes usar Icons.Default.Menu, Icons.Default.MoreVert o Icons.Default.FilterList
                                imageVector = Icons.Default.FilterList,
                                contentDescription = stringResource(R.string.admin_exam_sort_options_desc),
                                tint = Color.White,
                                modifier = Modifier.padding(12.dp) // Ajusta el tamaño del icono dentro del recuadro
                            )
                        }

                        // El menú desplegable
                        DropdownMenu(
                            expanded = expandedMenuFiltro,
                            onDismissRequest = {
                                expandedMenuFiltro = false
                            } // Se cierra al tocar fuera
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        stringResource(R.string.admin_exam_sort_asc),
                                        // Ponemos en negrita el que esté seleccionado actualmente
                                        fontWeight = if (ordenAscendente) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    viewModel.toggleOrdenAscendente(true)
                                    expandedMenuFiltro = false // Cerramos el menú tras elegir
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        stringResource(R.string.admin_exam_sort_desc),
                                        fontWeight = if (!ordenAscendente) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    viewModel.toggleOrdenAscendente(false)
                                    expandedMenuFiltro = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Lista de alumnos
                if (listaAlumnos.isEmpty() || estadoExamen == "CLOSED") {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = stringResource(R.string.admin_exam_empty_list),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 8.dp)
                    ) {
                        // ALUMNO
                        items(listaAlumnos) { alumno ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        adminPerfilViewModel.setStudentId(alumno.id)
                                        controller(StateNavigate.adminPerfilCliente.value)
                                    },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSecondary)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Datos Alumno
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${alumno.name} ${alumno.lastName}",
                                            fontWeight = FontWeight.Bold,
                                        )
                                        val beltName =
                                            (listaCinturones.find { it.id == alumno.beltId }?.name?.get(
                                                stringResource(R.string.language_code)
                                            ) as? String) ?: stringResource(R.string.admin_exam_unknown_belt)
                                        Text(
                                            text = beltName,
                                            style = MaterialTheme.typography.bodySmall,
                                        )
                                    }

                                    // Botones de Acción
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color(0xFFC5E7FF), // Fondo azul claro
                                            modifier = Modifier.size(36.dp),
                                            onClick = {
                                                alumnoSeleccionado = alumno // Guardamos quien es
                                                showAprobarIndividualDialog =
                                                    true // Mostramos el dialog
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = stringResource(R.string.admin_exam_approve_desc),
                                                tint = MaterialTheme.colorScheme.tertiary,
                                                modifier = Modifier.padding(6.dp)
                                            )
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.surface,
                                            modifier = Modifier.size(36.dp),
                                            onClick = {
                                                alumnoSeleccionado = alumno // Guardamos quién es
                                                showRechazarIndividualDialog =
                                                    true // Mostramos el dialog
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = stringResource(R.string.admin_exam_reject_desc),
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.padding(6.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// 2. COMPONENTE REUTILIZABLE PARA LOS DIALOGS DE EXAMEN
@Composable
fun DialogAccionExamen(
    titulo: String,
    descripcion: String,
    textoAceptar: String,
    textoCancelar: String,
    isLoading: Boolean = false,
    colorAceptar: Color,
    colorCancelar: Color,
    colorTextoCancelar: Color,
    mostrarTextArea: Boolean,
    onAceptar: (String) -> Unit,
    onCancelar: () -> Unit,
) {
    var comentario by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onCancelar) {
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
                    text = titulo,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = descripcion,
                    textAlign = TextAlign.Center
                )

                if (mostrarTextArea) {
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = comentario,
                        onValueChange = { comentario = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp), // TextArea más grande
                        placeholder = {
                            Text(
                                stringResource(R.string.admin_exam_optional_message),
                                style = MaterialTheme.typography.bodySmall
                            )
                        },
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Botón Aceptar (Izquierda - Acción principal)
                    Button(
                        onClick = { onAceptar(comentario) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = colorAceptar),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                textoAceptar,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    // Botón Cancelar (Derecha - Auxiliar)
                    Button(
                        onClick = onCancelar,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = colorCancelar),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            textoCancelar,
                            color = colorTextoCancelar,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
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
fun AdminGestionExamenpreview() {
    ProyectoFinalTheme {
        AdminGestionExamen() {}
    }
}