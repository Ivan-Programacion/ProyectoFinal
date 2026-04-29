package com.example.proyectofinal.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.Logic.meses
import com.example.proyectofinal.Model.Belt
import com.example.proyectofinal.Model.Center
import com.example.proyectofinal.Model.User
import com.example.proyectofinal.Repository.ContentRepository
import com.example.proyectofinal.Repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminPerfilClienteViewModel(
    private val userRepository: UserRepository,
    private val contentRepository: ContentRepository
) : ViewModel() {

    private val _studentId = MutableStateFlow<String?>(null)
    private var currentUser: User? = null

    // Estados de los campos superiores (Adulto o Tutor)
    val nombre = MutableStateFlow("")
    val apellidos = MutableStateFlow("")
    val dia = MutableStateFlow("")
    val mes = MutableStateFlow("")
    val anio = MutableStateFlow("")

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
    val beltId = MutableStateFlow("white")
    val isActive = MutableStateFlow(true)

    // Listas para desplegables
    private val _listaCentros = MutableStateFlow<List<Center>>(emptyList())
    val listaCentros: StateFlow<List<Center>> = _listaCentros.asStateFlow()

    private val _profesoresDisponibles = MutableStateFlow<List<User>>(emptyList())
    val profesoresDisponibles: StateFlow<List<User>> = _profesoresDisponibles.asStateFlow()

    private val _listaCinturones = MutableStateFlow<List<Belt>>(emptyList())
    val listaCinturones: StateFlow<List<Belt>> = _listaCinturones.asStateFlow()

    init {
        loadInitialData()
    }

    fun resetViewModelsStatesAdmin() {
        nombre.value = ""
        apellidos.value = ""
        dia.value = ""
        mes.value = ""
        anio.value = ""
        telefono.value = ""
        esMenor.value = false
        nombreMenor.value = ""
        apellidosMenor.value = ""
        diaMenor.value = ""
        mesMenor.value = ""
        anioMenor.value = ""
        centroSeleccionado.value = ""
        profesoresSeleccionados.value = emptySet()
        beltId.value = "white"
        isActive.value = true
        _listaCentros.value = emptyList()
        _profesoresDisponibles.value = emptyList()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _listaCentros.value = userRepository.getCenters()
            // Cargamos y ordenamos los cinturones por el campo order
            val belts = contentRepository.getBelts()
            _listaCinturones.value = belts.sortedBy { it.order }
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
        _studentId.value = id
        loadStudentData(id)
    }

    // Helper para convertir YYYY-ClaveMes-DD a [DD, NombreMes, YYYY]
    private fun parseDate(date: String?): List<String> {
        if (date.isNullOrBlank()) return listOf("", "", "")
        val parts = date.split("-")
        return if (parts.size == 3) {
            val year = parts[0]
            val monthInDb = parts[1] // Nombre del mes (ej: "Mayo")
            val dayNum = parts[2].toIntOrNull()?.toString() ?: ""

            // Buscamos la clave en el map que coincida (ignorando mayúsculas por seguridad)
            val monthName = meses.keys.find {
                it.equals(
                    monthInDb,
                    ignoreCase = true
                )
            } ?: monthInDb

            listOf(dayNum, meses.getValue(monthName), year)
        } else listOf("", "", "")
    }

    // Helper para convertir DD, NombreMes, YYYY a YYYY-ClaveMes-DD
    private fun formatDate(d: String, mName: String, a: String): String {
        if (d.isEmpty() || mName.isEmpty() || a.isEmpty()) return ""
        val dLabel = if (d.length == 1) "0$d" else d
        // mName ya es la clave ("Mayo", "Enero", etc.)
        val monthKey = meses.entries.find { it.value == mName }?.key ?: mName
        return "$a-$monthKey-$dLabel"
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
                beltId.value = it.beltId
                isActive.value = it.isActive
                if (it.isMinor) {
                    nombre.value = it.tutorName ?: ""
                    apellidos.value = it.tutorLastName ?: ""
                    val tDate = parseDate(it.tutorBirthDate)
                    dia.value = tDate[0]; mes.value = tDate[1]; anio.value = tDate[2]

                    nombreMenor.value = it.name
                    apellidosMenor.value = it.lastName
                    val aDate = parseDate(it.birthDate)
                    diaMenor.value = aDate[0]; mesMenor.value = aDate[1]; anioMenor.value = aDate[2]
                } else {
                    nombre.value = it.name
                    apellidos.value = it.lastName
                    val aDate = parseDate(it.birthDate)
                    dia.value = aDate[0]; mes.value = aDate[1]; anio.value = aDate[2]
                    
                    nombreMenor.value = ""
                    apellidosMenor.value = ""
                    diaMenor.value = ""
                    mesMenor.value = ""
                    anioMenor.value = ""
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
            val centerChanged = centroSeleccionado.value != userBase.centerId
            val newExamStatus = if (centerChanged) "NONE" else userBase.examStatus

            val updatedUser = if (esMenor.value) {
                userBase.copy(
                    name = nombreMenor.value,
                    lastName = apellidosMenor.value,
                    birthDate = formatDate(diaMenor.value, mesMenor.value, anioMenor.value),
                    tutorName = nombre.value,
                    tutorLastName = apellidos.value,
                    tutorBirthDate = formatDate(dia.value, mes.value, anio.value),
                    isMinor = true,
                    phone = telefono.value,
                    centerId = centroSeleccionado.value,
                    teacherIds = profesoresSeleccionados.value.toList(),
                    beltId = beltId.value,
                    isActive = isActive.value,
                    examStatus = newExamStatus
                )
            } else {
                userBase.copy(
                    name = nombre.value,
                    lastName = apellidos.value,
                    birthDate = formatDate(dia.value, mes.value, anio.value),
                    tutorName = null,
                    tutorLastName = null,
                    tutorBirthDate = null,
                    isMinor = false,
                    phone = telefono.value,
                    centerId = centroSeleccionado.value,
                    teacherIds = profesoresSeleccionados.value.toList(),
                    beltId = beltId.value,
                    isActive = isActive.value,
                    examStatus = newExamStatus
                )
            }

            if (userRepository.updateUser(updatedUser)) {
                onSuccess()
            }
        }
    }

    fun toggleUserActivation(onSuccess: () -> Unit) {
        val userBase = currentUser ?: return
        viewModelScope.launch {
            val newActiveState = !isActive.value
            val newExamStatus = if (!newActiveState) "NONE" else userBase.examStatus
            val updatedUser = userBase.copy(
                isActive = newActiveState,
                examStatus = newExamStatus
            )
            if (userRepository.updateUser(updatedUser)) {
                isActive.value = newActiveState
                currentUser = updatedUser
                onSuccess()
            }
        }
    }
}

class AdminPerfilClienteViewModelFactory(
    private val userRepository: UserRepository,
    private val contentRepository: ContentRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminPerfilClienteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AdminPerfilClienteViewModel(userRepository, contentRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
