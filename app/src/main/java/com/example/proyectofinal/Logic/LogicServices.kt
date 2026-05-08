package com.example.proyectofinal.Logic

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.messaging.FirebaseMessaging

// Función que actualiza el token del usuario en la BD en caso de que fuera necesario.
fun updateFcmTokenInFirestore(userId: String?) {
    if (userId == null) return
    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val token = task.result
            val db = FirebaseFirestore.getInstance()
            // Guardamos el token en la BD y lo añadimos al array de tokens (dispositivos iniciados)
            db.collection("users").document(userId).update("fcmTokens", FieldValue.arrayUnion(token))
        }
    }
}

// Función que limpia el campo de fcmToken del dispositivo si se cierra sesión
fun removeFcmTokenOnLogout(userId: String?, onComplete: () -> Unit) {
    if (userId == null) {
        onComplete()
        return
    }

    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val token = task.result
            val db = FirebaseFirestore.getInstance()
            // Eliminamos solo el token del dispositivo actual de la lista de tokens
            db.collection("users").document(userId)
                .update("fcmTokens", FieldValue.arrayRemove(token))
                .addOnCompleteListener {
                    // Una vez borrado de la base de datos, procedemos al signOut
                    onComplete()
                }
        } else {
            // Si falla obtener el token, continuamos con el logout
            onComplete()
        }
    }
}