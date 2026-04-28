package com.example.proyectofinal.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.Model.Center
import com.example.proyectofinal.Model.Exam
import com.example.proyectofinal.Model.User
import com.example.proyectofinal.Repository.AuthRepository
import com.example.proyectofinal.Repository.ExamRepository
import com.example.proyectofinal.Repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withTimeoutOrNull

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val message: String = "") : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val examRepository: ExamRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // Estado local para conocer el UUID autenticado (si lo hay)
    private val currentUserUid = MutableStateFlow(authRepository.getCurrentUserUid())

    // Listas dinámicas para Centros y Profesores
    private val _listaCentros = MutableStateFlow<List<Center>>(emptyList())
    val listaCentros: StateFlow<List<Center>> = _listaCentros.asStateFlow()

    private val _profesoresDisponibles = MutableStateFlow<List<User>>(emptyList())
    val profesoresDisponibles: StateFlow<List<User>> = _profesoresDisponibles.asStateFlow()

    init {
        // Cargar los centros al inicializar el ViewModel
        viewModelScope.launch {
            _listaCentros.value = userRepository.getCenters()
        }
    }

    // Stream en vivo del usuario logueado en base a su UID. Si no hay, null.
    // Usamos flatMapLatest para volver a escuchar en Firestore si el UID cambia.
    @OptIn(ExperimentalCoroutinesApi::class)
    val currentUserState: StateFlow<User?> = currentUserUid.flatMapLatest { uid ->
        if (uid != null) {
            userRepository.getUserStream(uid)
        } else {
            flowOf(null)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // Stream en vivo del examen en base al centerId del usuario. Si no hay, null.
    // Se vuelve a escuchar si el usuario cambia o si el examen es actualizado en Firestore.
    @OptIn(ExperimentalCoroutinesApi::class)
    val currentExamState: StateFlow<Exam?> = currentUserState.flatMapLatest { user ->
        if (user != null && user.centerId.isNotEmpty()) {
            examRepository.observeExam(user.centerId)
        } else {
            flowOf(null)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // Resetea el estado a Idle (para no repetir errores al volver a pantallas)
    fun resetUiState() {
        _uiState.value = AuthUiState.Idle
    }

    // Función para probar la conexión antes de navegar a Registro
    fun checkConnectionAndNavigate(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                // Intenta realizar una lectura con un timeout para comprobar conexión a internet/Firebase
                val result = withTimeoutOrNull(3000) {
                    userRepository.getCenters()
                }
                // Si la respuesta no es null y la lista NO está vacía (tenemos centros), hay conexión
                if (result != null && result.isNotEmpty()) { 
                    _uiState.value = AuthUiState.Idle
                    onSuccess()
                } else {
                    _uiState.value = AuthUiState.Error("Error de conexión. Inténtalo más tarde")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = AuthUiState.Error("Error de conexión. Inténtalo más tarde")
            }
        }
    }

    // Comprueba en Firebase si el email es válido y no está en uso antes de pasar a la contraseña
    fun checkEmailAndNavigate(emailToCheck: String, onSuccess: () -> Unit) {
        if (isValidEmail(emailToCheck)) {
            _uiState.value = AuthUiState.Idle
            onSuccess()
        } else {
            _uiState.value = AuthUiState.Error("Correo electrónico no válido.")
        }
    }

    // Login States
    val loginEmail = MutableStateFlow("")
    val loginPassword = MutableStateFlow("")
    val loginPasswordVisible = MutableStateFlow(false)

    // Registro Info State
    val nombre = MutableStateFlow("")
    val apellidos = MutableStateFlow("")
    val email = MutableStateFlow("")
    val telefono = MutableStateFlow("")

    val dia = MutableStateFlow("")
    val mes = MutableStateFlow("")
    val anio = MutableStateFlow("")

    val esMenor = MutableStateFlow(false)
    val nombreMenor = MutableStateFlow("")
    val apellidosMenor = MutableStateFlow("")
    val diaMenor = MutableStateFlow("")
    val mesMenor = MutableStateFlow("")
    val anioMenor = MutableStateFlow("")

    val centroSeleccionado = MutableStateFlow("") // Guarda el ID del Centro
    val profesoresSeleccionados = MutableStateFlow<Set<String>>(emptySet()) // Guarda IDs de profesores

    fun onCenterSelected(centerId: String) {
        centroSeleccionado.value = centerId
        profesoresSeleccionados.value = emptySet() // Limpiamos selección previa
        viewModelScope.launch {
            if (centerId.isNotBlank()) {
                _profesoresDisponibles.value = userRepository.getTeachersByCenter(centerId)
            } else {
                _profesoresDisponibles.value = emptyList()
            }
        }
    }

    // Registro Pass States
    val registroPassword = MutableStateFlow("")
    val registroRepeatPassword = MutableStateFlow("")
    val registroAceptoTerminos = MutableStateFlow(false)
    val registroPasswordVisible = MutableStateFlow(false)
    val registroRepeatPasswordVisible = MutableStateFlow(false)
    val showDialogTerminos = MutableStateFlow(false)

    // Verificación de email mínima desde Android
    private fun isValidEmail(emailStr: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()
    }

    // Comprobación de botón "Siguiente" enabled en RegistroInfo
    val isNextButtonEnabled: StateFlow<Boolean> = combine(
        nombre,
        apellidos,
        email,
        telefono,
        dia,
        mes,
        anio,
        centroSeleccionado,
        profesoresSeleccionados,
        esMenor,
        nombreMenor,
        apellidosMenor,
        diaMenor,
        mesMenor,
        anioMenor
    ) { args ->
        val nombreStr = args[0] as String
        val apellidosStr = args[1] as String
        val emailStr = args[2] as String
        val telefonoStr = args[3] as String
        val diaStr = args[4] as String
        val mesStr = args[5] as String
        val anioStr = args[6] as String
        val centroStr = args[7] as String
        val profeSet = args[8] as Set<*>
        val isMenor = args[9] as Boolean
        val nombreMenorStr = args[10] as String
        val apellidosMenorStr = args[11] as String
        val diaMenorStr = args[12] as String
        val mesMenorStr = args[13] as String
        val anioMenorStr = args[14] as String

        val mainFieldsValid = nombreStr.isNotBlank() && apellidosStr.isNotBlank() && emailStr.isNotBlank() &&
                telefonoStr.isNotBlank() && diaStr.isNotBlank() && mesStr.isNotBlank() && anioStr.isNotBlank() &&
                centroStr.isNotBlank() && profeSet.isNotEmpty()

        // Comprobación con la lógica del checkbox de menor
        if (!isMenor) {
            mainFieldsValid
        } else {
            mainFieldsValid && nombreMenorStr.isNotBlank() && apellidosMenorStr.isNotBlank() &&
                    diaMenorStr.isNotBlank() && mesMenorStr.isNotBlank() && anioMenorStr.isNotBlank()
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    fun registerUser(user: User, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            var errorMessage = ""
            val uid = authRepository.register(
                user.email,
                password,
                registroRepeatPassword.value
            ) { errorMessage = it }
            if (uid != null) {
                // Al ser data class, clonamos el objeto con su nuevo UID
                val userWithId = user.copy(id = uid)

                val createdInFirestore = userRepository.createUser(userWithId)
                if (createdInFirestore) {
                    currentUserUid.value = uid // Actualizamos el estado del uid actual
                    _uiState.value = AuthUiState.Success()
                    resetViewModelsStates()

                } else {
                    _uiState.value =
                        AuthUiState.Error("Error al guardar los datos del usuario. Intentalo más tarde")
                }
            } else {
                _uiState.value = AuthUiState.Error(errorMessage)
            }
        }
    }
// Resetear viewModels de RegistroInfo y RegistroPass
    fun resetViewModelsStates() {
        loginEmail.value = ""
        loginPassword.value = ""
        loginPasswordVisible.value = false
        nombre.value = ""
        apellidos.value = ""
        email.value = ""
        telefono.value = ""
        dia.value = ""
        mes.value = ""
        anio.value = ""
        esMenor.value = false
        nombreMenor.value = ""
        apellidosMenor.value = ""
        diaMenor.value = ""
        mesMenor.value = ""
        anioMenor.value = ""
        centroSeleccionado.value = ""
        profesoresSeleccionados.value = emptySet()

        registroPassword.value = ""
        registroRepeatPassword.value = ""
        registroAceptoTerminos.value = false
        registroPasswordVisible.value = false
        registroRepeatPasswordVisible.value = false
        showDialogTerminos.value = false

    }
    // Resetear viewModels de RegistroPass solo
    fun resetRegisterPassViewModelsStates() {
        registroPassword.value = ""
        registroRepeatPassword.value = ""
        registroAceptoTerminos.value = false
        registroPasswordVisible.value = false
        registroRepeatPasswordVisible.value = false
        showDialogTerminos.value = false
    }

    // Construye el modelo User con los estados actuales y llama al registro
    fun registerWithCurrentForm() {
        // Para guardar la fecha en formato yyyy-mm-dd
        val dateStr = "${anio.value}-${mes.value.padStart(2, '0')}-${dia.value.padStart(2, '0')}"
        val minorDateStr = if (esMenor.value) {
            "${anioMenor.value}-${mesMenor.value.padStart(2, '0')}-${
                diaMenor.value.padStart(
                    2,
                    '0'
                )
            }"
        } else null

        val newUser = User(
            email = email.value,
            centerId = centroSeleccionado.value,
            teacherIds = profesoresSeleccionados.value.toList(),
            name = if (esMenor.value) nombreMenor.value else nombre.value,
            lastName = if (esMenor.value) apellidosMenor.value else apellidos.value,
            birthDate = if (esMenor.value) minorDateStr!! else dateStr,
            phone = telefono.value,
            isMinor = esMenor.value,
            tutorName = if (esMenor.value) nombre.value else null,
            tutorLastName = if (esMenor.value) apellidos.value else null,
            tutorBirthDate = if (esMenor.value) dateStr else null
        )

        registerUser(newUser, registroPassword.value)
    }

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            var errorMessage = ""
            val loginSuccess = authRepository.login(email, password) { errorMessage = it }
            if (loginSuccess) {
                currentUserUid.value = authRepository.getCurrentUserUid() // Actualizamos al nuevo logueado
                _uiState.value = AuthUiState.Success()
                resetViewModelsStates()
            } else {
                _uiState.value = AuthUiState.Error(errorMessage)
            }
        }
    }

    fun updateUserProfile(name: String, lastName: String, phone: String) {
        viewModelScope.launch {
            val currentUser = currentUserState.value
            if (currentUser != null) {
                val updatedUser = currentUser.copy(
                    name = name,
                    lastName = lastName,
                    phone = phone
                )
                val success = userRepository.updateUser(updatedUser)
                if (success) {
                    _uiState.value = AuthUiState.Success("Datos actualizados correctamente")
                } else {
                    _uiState.value = AuthUiState.Error("Error de conexión. No se pudieron actualizar los datos")
                }
            }
        }
    }

    // Funciones de Logout
    fun logoutUser() {
        authRepository.logout()
        currentUserUid.value = null
    }

    fun requestExam() {
        viewModelScope.launch {
            val currentUser = currentUserState.value
            if (currentUser != null) {
                val updatedUser = currentUser.copy(examStatus = "APPLICANT")
                val success = userRepository.updateUser(updatedUser)
                if (success) {
                    _uiState.value = AuthUiState.Success("Solicitud de examen enviada correctamente")
                } else {
                    _uiState.value = AuthUiState.Error("Error al enviar la solicitud de examen")
                }
            }
        }
    }

    // Lógica para el mensaje informativo en la pantalla Perfil
    fun getMensajeInformativoExamen(
        isActive: Boolean,
        examStatus: String,
        examText: String,
        estadoExamenGlobal: String,
        infoMessage: String?
    ): String {
        return when {
            !isActive -> "Tu cuenta está dada de baja. Habla con tu sensei para reactivarla."
            examStatus == "APPROVED" -> examText.takeIf { it.isNotBlank() } ?: "¡Enhorabuena! Has aprobado tu último examen."
            examStatus == "FAILED" -> examText.takeIf { it.isNotBlank() } ?: "Lo siento, no has superado el examen."
            examStatus == "APPLICANT" -> examText.takeIf { it.isNotBlank() } ?: "Esperando aprobación de solicitud..."
            examStatus == "REFUSED" -> examText.takeIf { it.isNotBlank() } ?: "Tu solicitud de examen ha sido denegada."
            examStatus == "CANDIDATE" && estadoExamenGlobal == "IN_PROGRESS" -> examText.takeIf { it.isNotBlank() } ?: "¡El proceso de examen ha comenzado!"
            examStatus == "CANDIDATE" && estadoExamenGlobal == "OPEN_REQUESTS" -> examText.takeIf { it.isNotBlank() } ?: "¡Tu solicitud ha sido aprobada! El examen está a punto de empezar."
            estadoExamenGlobal == "OPEN_REQUESTS" && examStatus == "NONE" -> {
                infoMessage?.takeIf { it.isNotBlank() } ?: "¡Ya puedes solicitar tu examen!"
            }
            estadoExamenGlobal == "IN_PROGRESS" && examStatus == "NONE" -> "Hay un examen en proceso al cual no llegaste a solicitar acceso a tiempo."
            else -> "No hay proceso de examen en estos momentos."
        }
    }

}

/*
Al requerir dependencias por constructor en nuestro AuthViewModel (AuthRepository y UserRepository),
es necesario crear una ViewModelProvider.Factory para instanciar el ViewModel en Compose,
ya que de lo contrario no sabe de dónde obtener esas instancias.
 */
class AuthViewModelFactory(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val examRepository: ExamRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(authRepository, userRepository, examRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
