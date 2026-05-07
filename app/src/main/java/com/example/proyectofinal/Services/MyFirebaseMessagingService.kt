package com.example.proyectofinal.Services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    // se ejecuta cuando el token cambia o se añade de primeras
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // El token se actualizará automáticamente desde Compose
    }

    // Se ejecuta cuando llega la notificación en primer plano (para permitir la notifiación en primer plano)
    // Por defecto solo se muestran en segundo plano. Asi que necesitamos esto para que funcione en primer plano
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        val title = remoteMessage.notification?.title ?: "Nueva notificación"
        val body = remoteMessage.notification?.body ?: ""

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "fcm_default_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Notificaciones de la App", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Usa un ícono por defecto genérico de Android por ahora
            .setAutoCancel(true)

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }
}

