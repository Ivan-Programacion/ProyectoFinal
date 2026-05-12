package com.example.proyectofinal.View

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectofinal.Logic.extractYouTubeId
import com.example.proyectofinal.Logic.mapBeltColor
import com.example.proyectofinal.Logic.locale
import com.example.proyectofinal.ViewModel.ContentViewModel
import com.example.proyectofinal.ui.theme.ProyectoFinalTheme

@Composable
fun Contenido(
    paddingValues: PaddingValues = PaddingValues(),
    viewModel: ContentViewModel = viewModel()
) {
    // Recogemos el cinturon seleccionado
    val selectedBeltId by viewModel.selectedBeltId.collectAsStateWithLifecycle()
    // Recogemos el contenido seleccionado
    val selectedContent by viewModel.selectedContent.collectAsStateWithLifecycle()

    val contentName = selectedContent?.name?.get(locale) ?: ""
    val contentDescription = selectedContent?.description?.get(locale) ?: ""
    val contentNumber = selectedContent?.number ?: 1
    val contentTitle = if(selectedContent?.contentType != "TECH")contentName else "$contentNumber. $contentName"
    val contentUrl = selectedContent?.url ?: ""

    // isFavorite -> Logica provisional para hacer cambiar el icono de favorito de gris a amarillo
    var isFavorite by remember { mutableStateOf(false) }
    // Contenedor principal con el MISMO gradiente que ListaContenido
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        mapBeltColor[selectedBeltId] ?: Color.White, // Mismo color dinámico arriba
                        MaterialTheme.colorScheme.background // Tu color base abajo
                    ),
                    startY = 0f,
                    endY = 500f
                )
            )
    ) {
        // Tarjeta blanca/clara gigante que contiene la info
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp), // Padding interno de la tarjeta
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Título de la Técnica / Kata / Set
                    Text(
                        text = contentTitle,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    // Si es la pantalla contenido (-5 en función obtenerIndice(route))
                    IconButton(onClick = { isFavorite = !isFavorite }) {
                        if (isFavorite) {
                            // Si ES favorito: Apilamos relleno amarillo + borde marrón
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    modifier = Modifier.size(32.dp), // Tamaño de estrella más grande
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null, // null porque la descripción la lleva el contenedor o el otro icono
                                    tint = mapBeltColor.getValue("yellow") // Relleno amarillo
                                )
                                Icon(
                                    modifier = Modifier.size(32.dp), // Tamaño de estrella más grande
                                    imageVector = Icons.Default.StarBorder,
                                    contentDescription = "Desmarcar como favorito",
                                    tint = MaterialTheme.colorScheme.primary // Borde marrón de tu tema
                                )
                            }
                        } else {
                            // Si NO es favorito: Solo mostramos el borde marrón
                            Icon(
                                modifier = Modifier.size(32.dp), // Tamaño de estrella más grande
                                imageVector = Icons.Default.StarBorder,
                                contentDescription = "Marcar como favorito",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                // Descripción
                Text(
                    text = contentDescription,
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Reproductor del Vídeo o Placeholder si no hay URL
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f), // Esto empuja la caja para que ocupe todo el espacio sobrante hasta abajo
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        // Usamos onSecondary para mantener la estética de las tarjetas de ListaContenido
                        containerColor = MaterialTheme.colorScheme.onSecondary
                    )
                ) {
                    if (contentUrl.isNotEmpty()) {
                        val videoId = extractYouTubeId(contentUrl)
                        if (videoId != null) {
                            // AndroidView -> Compose aún no tiene un "YouTube Player" nativo, he utilizado un AndroidView.
                            // Este componente permite incrustar vistas nativas antiguas de Android en un entorno Jetpack Compose
                            // Con ello, inyecto un WebView (el equivalente a un navegador pequeñito) que carga un código <html> simple
                            // con un reproductor IFrame de YouTube.
                            AndroidView(
                                modifier = Modifier.fillMaxSize(),
                                factory = { context ->
                                    WebView(context).apply {
                                        // Configuración necesaria para reproducir video de YouTube sin romper la app
                                        settings.javaScriptEnabled = true
                                        // MUY IMPORTANTE para Youtube en WebViews (Evita el Error 152):
                                        // El error 152 es debido a que AndroidView necesita acceder al almacenamiento local del navegador
                                        // (LocalStorage del DOM) para guardar el estado de sus preferencias (volumen, tokens de sesión anónimos, tracking, etc.),
                                        // y los WebViews de Android por defecto tienen esta característica desactivada
                                        // Asi que -> domStroageEnabled = true
                                        settings.domStorageEnabled = true
                                        settings.mediaPlaybackRequiresUserGesture = false // Permite que opciones como el mute forzado no pidan interacción humana obligatoria
                                        settings.cacheMode = WebSettings.LOAD_NO_CACHE
                                        webChromeClient = WebChromeClient()
                                        webViewClient = WebViewClient()

                                        // HTML para incrustar el reproductor IFrame de YouTube con mute=1 para que no tenga audio por defecto
                                        // controls=1 (muestra controles), playsinline=1 (para q no salte a pantalla completa dándole al play), modestbranding=1
                                        // origin=https://www.youtube.com (Muy importante también para evitar el Error 152 cuando bloquea dominios extraños)
                                        // Utilizamos youtube-nocookie.com que suele saltarse mejor las restricciones de WebViews para videos ocultos
                                        val iframeHtml = """
                                            <!DOCTYPE html>
                                            <html>
                                                <body style="margin:0;padding:0;">
                                                    <iframe width="100%" height="100%" 
                                                            src="https://www.youtube-nocookie.com/embed/$videoId?mute=1&playsinline=1&modestbranding=1" 
                                                            frameborder="0" 
                                                            allow="autoplay; encrypted-media" 
                                                            allowfullscreen>
                                                    </iframe>
                                                </body>
                                            </html>
                                        """.trimIndent()
                                        
                                        loadDataWithBaseURL("https://www.youtube-nocookie.com", iframeHtml, "text/html", "utf-8", null)
                                    }
                                },
                                update = { webView ->
                                    // Si el iframeHtml cambiase al cambiar el videoId, se debería recargar aquí.
                                    // Pero al estar en un contenedor reactivo si cambia la variable url se recargará la vista
                                }
                            )
                        } else {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(text = "Formato de URL no válido")
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Video no disponible",
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Contenidodopreview() {
    ProyectoFinalTheme {
        Contenido()
    }
}

// Función auxiliar para extraer el ID del video de YouTube a partir de la URL compartida
fun extractYouTubeId(url: String): String? {
    val regex = "v=([^&]+)|youtu\\.be/([^?&]+)|embed/([^?&]+)|shorts/([^?&]+)".toRegex()
    val matchResult = regex.find(url)
    return matchResult?.groupValues?.drop(1)?.firstOrNull { it.isNotEmpty() }
}
