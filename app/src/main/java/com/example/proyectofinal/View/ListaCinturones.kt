package com.example.proyectofinal.View

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectofinal.Logic.belts
import com.example.proyectofinal.Logic.locale
import com.example.proyectofinal.Logic.mapBeltColor
import com.example.proyectofinal.ViewModel.BeltsViewModel
import com.example.proyectofinal.ViewModel.StateNavigate
import com.example.proyectofinal.ui.theme.ProyectoFinalTheme
import java.util.Locale

@SuppressLint("ContextCastToActivity")
@Composable
fun ListaCinturones(
    paddingValues: PaddingValues = PaddingValues(),
    viewModel: BeltsViewModel = viewModel(),
    controller: (String) -> Unit
) {
    // Para botón atrás del móvil
    val context = LocalContext.current as? Activity
// Sacamos la lista de cinturones reactiva (cambiará si algo cambia)
    val beltsState by viewModel.beltsUiState.collectAsStateWithLifecycle()

    // Interceptamos el botón de atrás
    BackHandler {
        // Cerramos la aplicación por completo
        context?.finish()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues) // Importante para no pisar el Navbar
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                // Añadimos margen lateral y fondo para que "flote"
                .padding(start = 12.dp, end = 12.dp, top = 16.dp, bottom = 16.dp),
            shape = RoundedCornerShape(24.dp), // Redondeado completo para look compacto
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                // Padding interno para que el contenido no toque los bordes de la Card
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp) // Espacio más compacto entre cinturones
            ) {
                items(beltsState) { beltState ->
                    // Con el idioma local, nos traemos el nombre del cinturon correspondiente
                    val beltName = beltState.belt.name[locale]

                    // Comprobamos con el ID de belts
                    if (beltState.belt.id != "white") {
                        BeltItem(
                            id = beltState.belt.id,
                            name = beltName,
                            beltColor = mapBeltColor[beltState.belt.id] ?: Color.White,
                            isEnabled = beltState.isEnabled
                        ) {
                            if (beltState.isEnabled) {
                                controller(StateNavigate.listaContenido.value)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BeltItem(
    id: String,
    name: String?,
    beltColor: Color,
    isEnabled: Boolean = true,
    controller: () -> Unit,
) {
    // Determinamos el color del texto para que siempre sea legible
    val textColor: Color
    if (id != "white" && id != "yellow")
        textColor = MaterialTheme.colorScheme.onPrimary
    else
        textColor = MaterialTheme.colorScheme.primary

    // Reducimos la opacidad si el nivel no está desbloqueado
    val backgroundColor = if (isEnabled) beltColor else beltColor.copy(alpha = 0.4f)
    val contentColor = if (isEnabled) textColor else textColor.copy(alpha = 0.4f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            // HABRA QUE VER EN LÓGICA BACKEND CÓMO PASAR EL CINTURÓN AQUÍ
            .clickable(enabled = isEnabled) { controller() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(if (isEnabled) 4.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (name != null) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = contentColor
                )
            }

            // Pequeño botón de flecha dentro de la subcard
            if (isEnabled) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White.copy(alpha = 0.3f),
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            } else {
                // Opcional: mostrar un icono de candado si está disabled
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ListaCinturonespreview() {
    ProyectoFinalTheme {
        ListaCinturones(controller = {})
    }
}