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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import java.text.Normalizer

class AdminListaClientesViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    // Estado del buscador
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    // Función de extensión para normalizar strings de búsqueda
    private fun String.normalize(): String {
        val normalized = Normalizer.normalize(this, Normalizer.Form.NFD)
        return "\\p{InCombiningDiacriticalMarks}+".toRegex().replace(normalized, "").lowercase()
    }

    // Ahora filteredStudents reacciona tanto al cambio de usuario como a la búsqueda
    @OptIn(ExperimentalCoroutinesApi::class)
    val filteredStudents: StateFlow<List<User>> = authRepository.getAuthStateStream()
        .flatMapLatest { uid ->
            if (uid == null) {
                // Si no hay usuario, devolvemos una lista vacía
                flowOf(emptyList())
            } else {
                // Si hay usuario, obtenemos sus alumnos en tiempo real
                userRepository.getStudentsByTeacherStream(uid)
            }
        }
        .combine(_searchQuery) { students, query ->
            if (query.isBlank()) {
                students
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
            initialValue = emptyList()
        )
}

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
