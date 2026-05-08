package com.example.proyectofinal.Services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.app.PendingIntent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.example.proyectofinal.ViewModel.MainActivity
import com.example.proyectofinal.R

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
        // diseños visuales y funcionales de las notificaciones EN PRIMER PLANO
        // Para que, cuando le pulses a la notificación, te abra la app
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.mipmap.logo_vector) // Para sacar el logo de la app
            .setStyle(NotificationCompat.BigTextStyle().bigText(body)) // Para poder leer textos largos
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }
}
