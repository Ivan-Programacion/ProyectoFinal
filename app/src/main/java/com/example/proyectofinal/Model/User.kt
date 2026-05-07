package com.example.proyectofinal.Model

import com.google.firebase.firestore.PropertyName

data class User(
    val id: String = "", // UID de Firebase Auth
    val email: String = "",
    val role: String = "STUDENT", // "STUDENT", "TEACHER", "SUPERADMIN"
    @get:PropertyName("clientApproved")
    @set:PropertyName("clientApproved")
    var isClientApproved: Boolean = true,
    @get:PropertyName("active")
    @set:PropertyName("active")
    var isActive: Boolean = true, // Para activar o desactivar alumno
    val centerId: String = "",
    val teacherIds: List<String> = emptyList(),
    val name: String = "",
    val lastName: String = "",
    val birthDate: String = "", // Formato "YYYY-MM-DD"
    val phone: String = "",
    val beltId: String = "white",
    @get:PropertyName("minor")
    @set:PropertyName("minor")
    var isMinor: Boolean = false,

    // Datos del tutor (Opcionales)
    val tutorName: String? = null,
    val tutorLastName: String? = null,
    val tutorBirthDate: String? = null,

    // Gestión de exámenes
    val examStatus: String = "NONE", // "NONE", "APPLICANT", "REFUSED", "CANDIDATE", "APPROVED", "FAILED"
    val examText: String = "",

    // Gestion de notificaciones
    val fcmToken: String? = null,

    // Listas de favoritos (IDs de los elementos)
    val favoritesTech: List<String> = emptyList(),
    val favoritesForms: List<String> = emptyList(),
    val favoritesSets: List<String> = emptyList()
)