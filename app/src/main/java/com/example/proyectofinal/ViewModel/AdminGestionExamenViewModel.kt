package com.example.proyectofinal.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.Logic.isNetworkAvailable
import com.example.proyectofinal.Model.Belt
import com.example.proyectofinal.Model.Exam
import com.example.proyectofinal.Model.User
import com.example.proyectofinal.R
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
import kotlinx.coroutines.withTimeoutOrNull
import java.text.Normalizer

class AdminGestionExamenViewModel(
    private val examRepository: ExamRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val contentRepository: ContentRepository,
    private val authViewModel: AuthViewModel
) : ViewModel() {

    // Exponemos el estado de AuthViewModel para controlar loadings y snackbars globales
    val authUiState: StateFlow<AuthUiState> = authViewModel.uiState

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
    fun startOpenRequests(globalMessage: String) {
        viewModelScope.launch {
            val centerId = currentTeacher.value?.centerId ?: return@launch
            authViewModel.setUiState(AuthUiState.Success(messageRes = R.string.success_admin_exam_requests_started))
            examRepository.updateExamStatus(centerId, "OPEN_REQUESTS", globalMessage)
        }
    }

    fun startInProgress(context: Context, onUpdate: () -> Unit) {
        viewModelScope.launch {
            authViewModel.setUiState(AuthUiState.Loading)

            // Comprobación de conexión real antes de proceder
            val hasConnection = isNetworkAvailable(context)
            if (!hasConnection) {
                authViewModel.setUiState(AuthUiState.Error(messageRes = R.string.error_admin_exam_connection))
                onUpdate()
                return@launch
            }

            val centerId = currentTeacher.value?.centerId ?: return@launch
            userRepository.updateAllStudentsExamStatusByCenter(
                centerId = centerId,
                oldStatus = "APPLICANT",
                newStatus = "CANDIDATE",
                text = "¡El proceso de examen ha comenzado!"
            )
            // Actualizamos también a los que ya estaban como CANDIDATE (aprobados individualmente)
            // para que se les refresque el examText correctamente
            userRepository.updateAllStudentsExamStatusByCenter(
                centerId = centerId,
                oldStatus = "CANDIDATE",
                newStatus = "CANDIDATE",
                text = "¡El proceso de examen ha comenzado!"
            )
            onUpdate()
            examRepository.updateExamStatus(centerId, "IN_PROGRESS", currentExam.value.infoMessage)
            authViewModel.setUiState(AuthUiState.Success(messageRes = R.string.success_admin_exam_started))
        }
    }

    fun finishExam(context: Context, onUpdate: () -> Unit) {
        viewModelScope.launch {
            authViewModel.setUiState(AuthUiState.Loading)

            // Comprobación de conexión real antes de proceder
            val hasConnection = isNetworkAvailable(context)

            if (!hasConnection) {
                authViewModel.setUiState(AuthUiState.Error(messageRes = R.string.error_admin_exam_connection))
                onUpdate()
                return@launch
            }

            val centerId = currentTeacher.value?.centerId ?: return@launch

            // 1. Aprobar y subir cinturón a todos los CANDIDATE del centro y los que ya estaban APPROVED
            val allUsers = userRepository.getAllUsers()
            // Buscamos tanto a los CANDIDATE (que se aprueban todos por cierre masivo) como a los APPROVED (aprobados individualmente)
            val candidatesAndApproved =
                allUsers.filter { it.centerId == centerId && (it.examStatus == "CANDIDATE" || it.examStatus == "APPROVED") }

            val belts = listaCinturones.value
            candidatesAndApproved.forEach { user ->
                val currentOrder = belts.find { it.id == user.beltId }?.order ?: 0
                val nextBelt = belts.find { it.order == currentOrder + 1 }
                val finalBeltId = nextBelt?.id ?: user.beltId
                // Como luego se van a pasar a NONE, solo actualizamos el cinturón directamente
                // usando la función pero dejándolo en APPROVED temporalmente
                userRepository.passExamAndUpgradeBelt(
                    user.id,
                    finalBeltId,
                    "APPROVED",
                    ""
                )
            }
            // 2. Cerrar el examen y resetear estados
            examRepository.updateExamStatus(centerId, "CLOSED", "")
            //kotlinx.coroutines.delay(2000)
            userRepository.updateAllStudentsExamStatusByCenter(centerId, "CANDIDATE", "NONE", "")
            userRepository.updateAllStudentsExamStatusByCenter(centerId, "APPROVED", "NONE", "")
            userRepository.updateAllStudentsExamStatusByCenter(centerId, "FAILED", "NONE", "")
            userRepository.updateAllStudentsExamStatusByCenter(centerId, "REFUSED", "NONE", "")
            userRepository.updateAllStudentsExamStatusByCenter(centerId, "APPLICANT", "NONE", "")
            onUpdate()
            authViewModel.setUiState(AuthUiState.Success(messageRes = R.string.success_admin_exam_finished))
        }
    }

    fun cancelExam() = viewModelScope.launch {
        val centerId = currentTeacher.value?.centerId ?: return@launch
        authViewModel.setUiState(AuthUiState.Success(messageRes = R.string.success_admin_exam_cancelled))
        examRepository.updateExamStatus(centerId, "CLOSED", "CANCELLED")
        
        userRepository.updateAllStudentsExamStatusByCenter(centerId, "CANDIDATE", "NONE", "")
        userRepository.updateAllStudentsExamStatusByCenter(centerId, "APPROVED", "NONE", "")
        userRepository.updateAllStudentsExamStatusByCenter(centerId, "FAILED", "NONE", "")
        userRepository.updateAllStudentsExamStatusByCenter(centerId, "REFUSED", "NONE", "")
        userRepository.updateAllStudentsExamStatusByCenter(centerId, "APPLICANT", "NONE", "")
    }

    // ACCIONES SOBRE EL ALUMNO INDIVIDUAL
    fun approveStudentRequest(userId: String) = viewModelScope.launch {
        authViewModel.setUiState(AuthUiState.Success(messageRes = R.string.success_admin_exam_request_approved))
        userRepository.updateStudentExamStatus(userId, "CANDIDATE", "¡Tu solicitud ha sido aprobada! El examen está a punto de empezar.")
    }

    fun refuseStudentRequest(userId: String) = viewModelScope.launch {
        authViewModel.setUiState(AuthUiState.Success(messageRes = R.string.success_admin_exam_request_refused))
        userRepository.updateStudentExamStatus(userId, "REFUSED", "Tu solicitud ha sido denegada.")
    }

    fun passStudentExam(user: User) = viewModelScope.launch {
        authViewModel.setUiState(AuthUiState.Success(messageRes = R.string.success_admin_exam_student_passed))
        userRepository.updateStudentExamStatus(user.id, "APPROVED", "¡Has aprobado el examen! Recibirás tu nuevo cinturón al finalizar.")
    }

    fun failStudentExam(userId: String) = viewModelScope.launch {
        authViewModel.setUiState(AuthUiState.Success(messageRes = R.string.success_admin_exam_student_failed))
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
    private val contentRepository: ContentRepository,
    private val authViewModel: AuthViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminGestionExamenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AdminGestionExamenViewModel(examRepository, userRepository, authRepository, contentRepository, authViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
