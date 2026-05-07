package com.example.proyectofinal.Logic

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

// Función que actualiza el token del usuario en la BD en caso de que fuera necesario.
fun updateFcmTokenInFirestore(userId: String?) {
    if (userId == null) return
    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val token = task.result
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(userId).update("fcmToken", token)
        }
    }
}

// Función que limpia el campo de fcmToken del dispositivo si se cierra sesión
fun removeFcmTokenOnLogout(userId: String?, onComplete: () -> Unit) {
    if (userId == null) {
        onComplete()
        return
    }

    val db = FirebaseFirestore.getInstance()
    // Ponemos el campo como null o una cadena vacía
    db.collection("users").document(userId)
        .update("fcmToken", null)
        .addOnCompleteListener {
            // Una vez borrado de la base de datos, procedemos al signOut
            onComplete()
        }
}