package com.example.proyectofinal.Services

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

// Función de seguridad (La puedes dejar aquí mismo debajo, dentro del archivo MainActivity.kt)
@RequiresApi(Build.VERSION_CODES.O)
suspend fun checkIsAppExpiredSecurely(): Boolean {
    return withContext(Dispatchers.IO) {
        try {
            // Conectamos con Google solo para leer la cabecera de la hora
            val url = URL("https://www.google.com")
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 3000
            connection.readTimeout = 3000
            connection.requestMethod = "HEAD"
            connection.connect()

            val serverTimeMillis = connection.date
            connection.disconnect()

            // Si falla la red, asumimos false para no arruinar la prueba en caso de mal WiFi en el gimnasio
            if (serverTimeMillis == 0L) return@withContext false

            val networkDate = Instant.ofEpochMilli(serverTimeMillis)
                .atZone(ZoneId.of("Europe/Madrid"))
                .toLocalDate()

            // AQUI PONES TU FECHA LÍMITE (Año, Mes, Día).
            // A las 00:00 de ese día, la app dejará de abrir.
            val expirationDate = LocalDate.of(2026, 5, 29)

            return@withContext networkDate.isEqual(expirationDate) || networkDate.isAfter(expirationDate)

        } catch (e: Exception) {
            false // Fallback de seguridad por si no hay internet
        }
    }
}