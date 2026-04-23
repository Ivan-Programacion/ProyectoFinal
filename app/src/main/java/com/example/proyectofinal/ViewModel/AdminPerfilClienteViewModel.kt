package com.example.proyectofinal.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.Model.Center
import com.example.proyectofinal.Model.User
import com.example.proyectofinal.Repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminPerfilClienteViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _studentId = MutableStateFlow<String?>(null)
    private var currentUser: User? = null

    // Estados de los campos superiores (Adulto o Tutor)
    val nombre = MutableStateFlow("")
    val apellidos = MutableStateFlow("")
    val dia = MutableStateFlow("")
    val mes = MutableStateFlow("")
    val anio = MutableStateFlow("")
    
    // Estos campos siempre pertenecen al documento principal (el alumno/usuario)
    val email = MutableStateFlow("")
    val telefono = MutableStateFlow("")

    // Estados del menor (Los datos del alumno si isMinor es true)
    val esMenor = MutableStateFlow(false)
    val nombreMenor = MutableStateFlow("")
    val apellidosMenor = MutableStateFlow("")
    val diaMenor = MutableStateFlow("")
    val mesMenor = MutableStateFlow("")
    val anioMenor = MutableStateFlow("")

    // Otros campos
    val centroSeleccionado = MutableStateFlow("")
    val profesoresSeleccionados = MutableStateFlow<Set<String>>(emptySet())
    val cinturon = MutableStateFlow("")

    // Listas para desplegables
    private val _listaCentros = MutableStateFlow<List<Center>>(emptyList())
    val listaCentros: StateFlow<List<Center>> = _listaCentros.asStateFlow()

    private val _profesoresDisponibles = MutableStateFlow<List<User>>(emptyList())
    val profesoresDisponibles: StateFlow<List<User>> = _profesoresDisponibles.asStateFlow()

    init {
        loadCenters()
    }

    private fun loadCenters() {
        viewModelScope.launch {
            _listaCentros.value = userRepository.getCenters()
        }
    }

    fun onCenterSelected(centerId: String) {
        centroSeleccionado.value = centerId
        profesoresSeleccionados.value = emptySet()
        loadTeachers(centerId)
    }

    private fun loadTeachers(centerId: String) {
        viewModelScope.launch {
            _profesoresDisponibles.value = userRepository.getTeachersByCenter(centerId)
        }
    }

    fun setStudentId(id: String) {
        if (_studentId.value == id) return
        _studentId.value = id
        loadStudentData(id)
    }

    private fun loadStudentData(id: String) {
        viewModelScope.launch {
            val user = userRepository.getUser(id)
            currentUser = user
            user?.let {
                email.value = it.email
                telefono.value = it.phone
                esMenor.value = it.isMinor
                centroSeleccionado.value = it.centerId
                profesoresSeleccionados.value = it.teacherIds.toSet()
                cinturon.value = it.beltId
                
                if (it.isMinor) {
                    // SI ES MENOR:
                    // Arriba (Tutor): tutorName, tutorLastName, tutorBirthDate
                    nombre.value = it.tutorName ?: ""
                    apellidos.value = it.tutorLastName ?: ""
                    it.tutorBirthDate?.split("/")?.let { p ->
                        if (p.size == 3) { dia.value = p[0]; mes.value = p[1]; anio.value = p[2] }
                    }
                    // Abajo (Alumno): name, lastName, birthDate
                    nombreMenor.value = it.name
                    apellidosMenor.value = it.lastName
                    it.birthDate.split("/").let { p ->
                        if (p.size == 3) { diaMenor.value = p[0]; mesMenor.value = p[1]; anioMenor.value = p[2] }
                    }
                } else {
                    // SI ES ADULTO:
                    // Arriba (Alumno): name, lastName, birthDate
                    nombre.value = it.name
                    apellidos.value = it.lastName
                    it.birthDate.split("/").let { p ->
                        if (p.size == 3) { dia.value = p[0]; mes.value = p[1]; anio.value = p[2] }
                    }
                    // Limpiamos campos del menor
                    nombreMenor.value = ""
                    apellidosMenor.value = ""
                    diaMenor.value = ""; mesMenor.value = ""; anioMenor.value = ""
                }

                if (it.centerId.isNotEmpty()) {
                    loadTeachers(it.centerId)
                }
            }
        }
    }

    fun updateStudent(onSuccess: () -> Unit) {
        val currentId = _studentId.value ?: return
        val userBase = currentUser ?: return
        
        viewModelScope.launch {
            val updatedUser = if (esMenor.value) {
                // Caso menor: name/lastName es el niño (abajo), tutorName es el adulto (arriba)
                userBase.copy(
                    name = nombreMenor.value,
                    lastName = apellidosMenor.value,
                    birthDate = "${diaMenor.value}/${mesMenor.value}/${anioMenor.value}",
                    tutorName = nombre.value,
                    tutorLastName = apellidos.value,
                    tutorBirthDate = "${dia.value}/${mes.value}/${anio.value}",
                    isMinor = true,
                    phone = telefono.value,
                    centerId = centroSeleccionado.value,
                    teacherIds = profesoresSeleccionados.value.toList(),
                    beltId = cinturon.value
                )
            } else {
                // Caso adulto: name/lastName es el adulto (arriba), tutorName es null
                userBase.copy(
                    name = nombre.value,
                    lastName = apellidos.value,
                    birthDate = "${dia.value}/${mes.value}/${anio.value}",
                    tutorName = null,
                    tutorLastName = null,
                    tutorBirthDate = null,
                    isMinor = false,
                    phone = telefono.value,
                    centerId = centroSeleccionado.value,
                    teacherIds = profesoresSeleccionados.value.toList(),
                    beltId = cinturon.value
                )
            }
            
            if (userRepository.updateUser(updatedUser)) {
                onSuccess()
            }
        }
    }

    fun deleteStudent(onSuccess: () -> Unit) {
        val currentId = _studentId.value ?: return
        viewModelScope.launch {
            if (userRepository.deleteUser(currentId)) {
                onSuccess()
            }
        }
    }
}

class AdminPerfilClienteViewModelFactory(
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminPerfilClienteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AdminPerfilClienteViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
