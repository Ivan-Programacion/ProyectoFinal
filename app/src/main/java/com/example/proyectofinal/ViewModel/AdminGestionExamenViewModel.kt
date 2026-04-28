package com.example.proyectofinal.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.Model.Belt
import com.example.proyectofinal.Model.Exam
import com.example.proyectofinal.Model.User
import com.example.proyectofinal.Repository.AuthRepository
import com.example.proyectofinal.Repository.ContentRepository
import com.example.proyectofinal.Repository.ExamRepository
import com.example.proyectofinal.Repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.Normalizer

class AdminGestionExamenViewModel(
    private val examRepository: ExamRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val contentRepository: ContentRepository
) : ViewModel() {

    // Flujo Reactivo del Usuario Actual (Profesor) para obtener su centerId
    // Ahora reacciona al stream de autenticación para evitar IDs vacíos al arrancar
    @OptIn(ExperimentalCoroutinesApi::class)
    private val currentTeacher: StateFlow<User?> = authRepository.getAuthStateStream()
        .flatMapLatest { uid ->
            if (!uid.isNullOrBlank()) {
                userRepository.getUserStream(uid)
            } else {
                flowOf(null)
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    // Flujo Reactivo de Estado del Exam basado en el centerId del profe
    @OptIn(ExperimentalCoroutinesApi::class)
    val currentExam: StateFlow<Exam> = currentTeacher
        .filterNotNull()
        .flatMapLatest { teacher ->
            if (teacher.centerId.isNotEmpty()) {
                examRepository.observeExam(teacher.centerId).filterNotNull()
            } else {
                flowOf(Exam(currentStatus = "CLOSED"))
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, Exam())

    // Buscador
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Orden Ascendente / Descendente
    private val _ordenAscendente = MutableStateFlow(true)
    val ordenAscendente: StateFlow<Boolean> = _ordenAscendente.asStateFlow()

    // Flujo Reactivo de Cinturones de Base de Datos
    val listaCinturones: StateFlow<List<Belt>> = contentRepository.getBeltsStream()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Flujo de alumnos del profesor
    @OptIn(ExperimentalCoroutinesApi::class)
    private val studentsInCenter: StateFlow<List<User>> = currentTeacher
        .filterNotNull()
        .flatMapLatest { teacher ->
            if (teacher.centerId.isNotEmpty()) {
                // Aquí filtra alumnos de SU centro y SU id (podría ser teacherIds en list)
                userRepository.getStudentsByTeacherStream(teacher.id)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Filtros base para los alumnos que se van a mostrar
    val filteredStudents: StateFlow<List<User>> = combine(
        studentsInCenter,
        _searchQuery,
        _ordenAscendente,
        currentExam,
        listaCinturones
    ) { students, query, asc, exam, belts ->
        // 1. Filtrado por el estado actual del examen
        val validStatus = when (exam.currentStatus) {
            "OPEN_REQUESTS" -> listOf("APPLICANT")
            "IN_PROGRESS" -> listOf("CANDIDATE")
            else -> emptyList()
        }

        var list = students.filter { it.examStatus in validStatus }

        // 2. Normalización del query manual
        if (query.isNotBlank()) {
            val normalizedQuery = normalizeString(query)
            list = list.filter {
                val fullName = normalizeString("${it.name} ${it.lastName}")
                fullName.contains(normalizedQuery)
            }
        }

        // 3. Ordenamiento por cinturones
        list.sortedWith(compareBy { user ->
            val order = belts.find { it.id == user.beltId }?.order ?: 0
            if (asc) order else -order
        })
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun toggleOrdenAscendente(isAscendente: Boolean) {
        _ordenAscendente.value = isAscendente
    }

    // ACCIONES DE PROFESOR SOBRE EL EXAMEN GLOBALES
    fun startOpenRequests(globalMessage: String) = viewModelScope.launch {
        val centerId = currentTeacher.value?.centerId ?: return@launch
        examRepository.updateExamStatus(centerId, "OPEN_REQUESTS", globalMessage)
    }

    fun startInProgress() = viewModelScope.launch {
        val centerId = currentTeacher.value?.centerId ?: return@launch
        userRepository.updateAllStudentsExamStatusByCenter(
            centerId = centerId,
            oldStatus = "APPLICANT",
            newStatus = "CANDIDATE",
            text = "¡El proceso de examen ha comenzado!"
        )
        examRepository.updateExamStatus(centerId, "IN_PROGRESS", currentExam.value.infoMessage)
    }

    fun finishExam() = viewModelScope.launch {
        val centerId = currentTeacher.value?.centerId ?: return@launch
        examRepository.updateExamStatus(centerId, "CLOSED", "")
        
        userRepository.updateAllStudentsExamStatusByCenter(centerId, "CANDIDATE", "NONE", "")
        userRepository.updateAllStudentsExamStatusByCenter(centerId, "APPROVED", "NONE", "")
        userRepository.updateAllStudentsExamStatusByCenter(centerId, "FAILED", "NONE", "")
        userRepository.updateAllStudentsExamStatusByCenter(centerId, "REFUSED", "NONE", "")
    }

    fun cancelExam() = viewModelScope.launch {
        finishExam()
    }

    // ACCIONES SOBRE EL ALUMNO INDIVIDUAL
    fun approveStudentRequest(userId: String) = viewModelScope.launch {
        userRepository.updateStudentExamStatus(userId, "CANDIDATE", "¡Tu solicitud ha sido aprobada! El examen está a punto de empezar.")
    }

    fun refuseStudentRequest(userId: String) = viewModelScope.launch {
        userRepository.updateStudentExamStatus(userId, "REFUSED", "Tu solicitud ha sido denegada.")
    }

    fun passStudentExam(user: User) = viewModelScope.launch {
        val currentOrder = listaCinturones.value.find { it.id == user.beltId }?.order ?: 0
        val nextBelt = listaCinturones.value.find { it.order == currentOrder + 1 }
        val finalBeltId = nextBelt?.id ?: user.beltId

        userRepository.passExamAndUpgradeBelt(user.id, finalBeltId, "APPROVED", "¡Enhorabuena, has aprobado el examen!")
    }

    fun failStudentExam(userId: String) = viewModelScope.launch {
        userRepository.updateStudentExamStatus(userId, "FAILED", "Lo siento, no has superado el examen.")
    }

    private fun normalizeString(input: String): String {
        val normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
        val regex = Regex("[\\p{InCombiningDiacriticalMarks}]")
        return regex.replace(normalized, "").lowercase()
    }
}

class AdminGestionExamenViewModelFactory(
    private val examRepository: ExamRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val contentRepository: ContentRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminGestionExamenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AdminGestionExamenViewModel(examRepository, userRepository, authRepository, contentRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
