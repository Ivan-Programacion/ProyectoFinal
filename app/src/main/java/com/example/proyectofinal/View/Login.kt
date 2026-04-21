package com.example.proyectofinal.View

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectofinal.R
import com.example.proyectofinal.ViewModel.App
import com.example.proyectofinal.ViewModel.AuthUiState
import com.example.proyectofinal.ViewModel.AuthViewModel
import com.example.proyectofinal.ViewModel.StateNavigate
import com.example.proyectofinal.ui.theme.ProyectoFinalTheme
import androidx.activity.compose.BackHandler
import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.platform.LocalContext


/*
FALTA POR HACER:
- En caso de que haya errores
- Toast emergente para avisar
 */
@SuppressLint("ContextCastToActivity")
@Composable
fun Login(
    paddingValues: PaddingValues = PaddingValues(),
    viewModel: AuthViewModel = viewModel(),
    controller: (String) -> Unit
) {
    val userValue by viewModel.loginEmail.collectAsStateWithLifecycle()
    val passValue by viewModel.loginPassword.collectAsStateWithLifecycle()
    val passwordVisible by viewModel.loginPasswordVisible.collectAsStateWithLifecycle()
    // Para coger el contexto actual como una actividad y poder enlazarlo con el botón de "atras"
    // del propio móvil
    val context = LocalContext.current as? Activity

    // Para poder ver el estado de la petición de login y saber si pasar o no a la siguiene pantalla
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            // Si el login es un éxito, navegamos
            viewModel.loginEmail.value = ""
            viewModel.loginPassword.value = ""
            viewModel.resetUiState()
            controller(StateNavigate.listaCinturones.value)
        }
    }

    // Interceptamos el botón de atrás
    BackHandler {
        // Cerramos la aplicación por completo
        context?.finish()
    }
    Column(
        Modifier
            .padding(paddingValues)
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_arkenpoapp),
            contentDescription = "Logo",
            modifier = Modifier.size(150.dp) // Ajustado para que no ocupe todo el ancho
        )

        Card(
            shape = RoundedCornerShape(28.dp), // Esquinas más redondeadas como en tu imagen
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            Column(
                Modifier
                    .padding(20.dp),
                horizontalAlignment = Alignment.Start // Alineación a la izquierda para las etiquetas
            ) {
                Text(
                    "Correo electrónico",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = userValue,
                    onValueChange = { viewModel.loginEmail.value = it },
                    placeholder = { Text("ej. carlos@gmail.com") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    "Contraseña",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = passValue,
                    onValueChange = { viewModel.loginPassword.value = it },
                    placeholder = { Text("Contraseña") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image =
                            if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = {
                            viewModel.loginPasswordVisible.value = !passwordVisible
                        }) {
                            Icon(imageVector = image, contentDescription = null)
                        }
                    },
                )
                Text(
                    text = "¿Olvidaste la contraseña?",
                    color = MaterialTheme.colorScheme.tertiary, // Validaciones
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .clickable {},
                    textAlign = TextAlign.End
                )

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        viewModel.loginUser(userValue, passValue)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text("Acceder", fontWeight = FontWeight.Bold)
                }
            }
        }
        Row(
            modifier = Modifier.padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "¿No tienes cuenta? ",
                modifier = Modifier.clickable {
                    viewModel.checkConnectionAndNavigate {
                        controller(StateNavigate.registro.value)
                    }
                    viewModel.resetUiState()
                } // Para crear cuenta probando conexión primero
            )
            Text(
                "Crear cuenta",
                fontWeight = FontWeight.Bold, // Bold para que se vea
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.clickable {
                    viewModel.checkConnectionAndNavigate {
                        controller(StateNavigate.registro.value)
                    }
                    viewModel.resetUiState()
                } // Para crear cuenta probando conexión primero
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun Loginpreview() {
    ProyectoFinalTheme {
        App()
    }
}