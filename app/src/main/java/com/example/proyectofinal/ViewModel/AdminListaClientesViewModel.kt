package com.example.proyectofinal.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.Model.User
import com.example.proyectofinal.Repository.AuthRepository
import com.example.proyectofinal.Repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.text.Normalizer

class AdminListaClientesViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val currentTeacherId = authRepository.getCurrentUserUid() ?: ""

    // Estado del buscador
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    // Función de extensión (utilidad privada) para normalizar strings de búsqueda
    private fun String.normalize(): String {
        // Separa los caracteres de las tildes/diacríticos y los elimina
        val normalized = Normalizer.normalize(this, Normalizer.Form.NFD)
        return "\\p{InCombiningDiacriticalMarks}+".toRegex().replace(normalized, "").lowercase()
    }

    // Usamos el Flow nativo del repositorio de alumnos del profesor y lo combinamos con la query
    @OptIn(ExperimentalCoroutinesApi::class)
    val filteredStudents: StateFlow<List<User>> = userRepository.getStudentsByTeacherStream(currentTeacherId)
        .combine(_searchQuery) { students, query ->
            if (query.isBlank()) {
                students // Sin query, devolvemos todos
            } else {
                val queryNormalized = query.normalize()
                students.filter { student ->
                    val fullName = "${student.name} ${student.lastName}".normalize()
                    fullName.contains(queryNormalized)
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList() // Estado inicial cargando la lista (vacía primero)
        )
}

// Factoría para pasar las instancias requeridas en Compose Mvvm
class AdminListaClientesViewModelFactory(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminListaClientesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AdminListaClientesViewModel(authRepository, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

