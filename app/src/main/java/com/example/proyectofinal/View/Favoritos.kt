package com.example.proyectofinal.View

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyectofinal.Logic.belts
import com.example.proyectofinal.ViewModel.StateNavigate
import com.example.proyectofinal.ui.theme.ProyectoFinalTheme
import com.example.proyectofinal.ui.theme.coloresCinturones
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectofinal.ViewModel.AuthViewModel
import com.example.proyectofinal.ViewModel.ContentViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import com.example.proyectofinal.Logic.locale
import com.example.proyectofinal.ViewModel.BeltsViewModel
import com.example.proyectofinal.Logic.mapBeltColor
import com.example.proyectofinal.Model.Belt
import com.example.proyectofinal.Model.Content

@SuppressLint("ContextCastToActivity")
@Composable
fun Favoritos(
    paddingValues: PaddingValues = PaddingValues(),
    authViewModel: AuthViewModel = viewModel(),
    contentViewModel: ContentViewModel = viewModel(),
    beltsViewModel: BeltsViewModel = viewModel(),
    controller: (String) -> Unit
) {
    val currentUser by authViewModel.currentUserState.collectAsStateWithLifecycle()
    val allContentList by contentViewModel.allContentList.collectAsStateWithLifecycle()
    val allBelts by beltsViewModel.beltsUiState.collectAsStateWithLifecycle()

    // Recogemos todos los contenidos que coincidan con los IDs de favoritos del usuario
    val userFavoritesTech = allContentList.filter { currentUser?.favoritesTech?.contains(it.id) == true }
    val userFavoritesForms = allContentList.filter { currentUser?.favoritesForms?.contains(it.id) == true }
    val userFavoritesSets = allContentList.filter { currentUser?.favoritesSets?.contains(it.id) == true }

    // Función para obtener el orden de un cinturón dado su ID
    fun getBeltOrder(beltId: String): Int {
        return allBelts.find { it.belt.id == beltId }?.belt?.order ?: 0
    }

    // Ordenamos por cinturón (order) y luego por número
    val sortedTech = userFavoritesTech.sortedWith(compareBy({ getBeltOrder(it.beltId) }, { it.number }))
    val sortedForms = userFavoritesForms.sortedWith(compareBy({ getBeltOrder(it.beltId) }, { it.number }))
    val sortedSets = userFavoritesSets.sortedWith(compareBy({ getBeltOrder(it.beltId) }, { it.number }))

    // Para botón atrás del móvil
    val context = LocalContext.current as? Activity

    // Interceptamos el botón de atrás
    BackHandler {
        // Cerramos la aplicación por completo
        controller(StateNavigate.listaCinturones.value)
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // SECCIÓN TÉCNICAS
        item {
            SeccionFavoritos(
                titulo = "Técnicas",
                items = sortedTech,
                beltsList = allBelts.map { it.belt }
            ) { content ->
                contentViewModel.setSelectedBeltId(content.beltId)
                contentViewModel.setSelectedContentId(content.id)
                controller(StateNavigate.contenido.value)
            }
        }

        // SECCIÓN FORMA (KATA)
        item {
            SeccionFavoritos(
                titulo = "Formas (Kata)",
                items = sortedForms,
                beltsList = allBelts.map { it.belt }
            ) { content ->
                contentViewModel.setSelectedBeltId(content.beltId)
                contentViewModel.setSelectedContentId(content.id)
                controller(StateNavigate.contenido.value)
            }
        }

        // SECCIÓN SET
        item {
            SeccionFavoritos(
                titulo = "Sets",
                items = sortedSets,
                beltsList = allBelts.map { it.belt }
            ) { content ->
                contentViewModel.setSelectedBeltId(content.beltId)
                contentViewModel.setSelectedContentId(content.id)
                controller(StateNavigate.contenido.value)
            }
        }
    }
}

@Composable
fun SeccionFavoritos(
    titulo: String,
    items: List<Content>,
    beltsList: List<Belt>,
    controller: (Content) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 12.dp, top = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
            )

            if (items.isEmpty()) {
                Text(
                    text = "No tienes favoritos en esta categoría",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 4.dp)
                )
            } else {
                items.forEach { content ->
                    val beltName = beltsList.find { it.id == content.beltId }?.name?.get(locale) ?: "Desconocido"
                    val contentName = content.name[locale] ?: ""
                    val title = if (content.contentType == "TECH") "${content.number}. $contentName" else contentName
                    val beltColor = mapBeltColor[content.beltId] ?: Color.Gray

                    ItemFavorito(
                        nombreTecnica = title,
                        nombreCinturon = beltName,
                        colorCinturon = beltColor
                    ) { controller(content) }
                }
            }
        }
    }
}

@Composable
fun ItemFavorito(
    nombreTecnica: String,
    nombreCinturon: String,
    colorCinturon: Color,
    controller: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 80.dp)
            .clickable { controller() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSecondary)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Indicador circular del color del cinturón
                Surface(
                    modifier = Modifier.size(16.dp),
                    shape = CircleShape,
                    color = colorCinturon,
                ) {}

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = nombreTecnica,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(
                        text = nombreCinturon,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            // Botón de flecha característico de tu diseño
            Surface(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.padding(4.dp),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Favoritospreview() {
    ProyectoFinalTheme {
        Favoritos() {}
    }
}