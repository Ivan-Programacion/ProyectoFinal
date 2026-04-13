package com.example.proyectofinal.Model

data class User(
    val id: String = "", // UID de Firebase Auth
    val email: String = "",
    val role: String = "STUDENT", // "STUDENT", "TEACHER", "SUPERADMIN"
    val isClientApproved: Boolean = true, // -------------------- POR AHORA TRUE, HASTA QUE VEAMOS SU IMPLEMENTACIÓN
    val centerId: String = "",
    val teacherIds: List<String> = emptyList(),
    val name: String = "",
    val lastName: String = "",
    val birthDate: String = "", // Formato "YYYY-MM-DD"
    val phone: String = "",
    val beltId: String = "", // -------------------- REVISAR ID BELT
    val isMinor: Boolean = false,

    // Datos del tutor (Opcionales, por eso llevan "?" y valor por defecto null)
    val tutorName: String? = null,
    val tutorLastName: String? = null,
    val tutorBirthDate: String? = null,

    // Gestión de exámenes
    val examStatus: String = "NONE", // "NONE", "APPLICANT", "REFUSED", "CANDIDATE", "APPROVED", "FAILED"
    val examText: String = "",

    // Listas de favoritos (IDs de los elementos)
    val favoritesTech: List<String> = emptyList(),
    val favoritesForms: List<String> = emptyList(),
    val favoritesSets: List<String> = emptyList()
)