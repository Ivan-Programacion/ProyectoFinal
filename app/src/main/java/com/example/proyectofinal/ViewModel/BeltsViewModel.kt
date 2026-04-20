package com.example.proyectofinal.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.Model.Belt
import com.example.proyectofinal.Model.User
import com.example.proyectofinal.Repository.AuthRepository
import com.example.proyectofinal.Repository.ContentRepository
import com.example.proyectofinal.Repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

data class BeltItemUiState(
    val belt: Belt,
    val isEnabled: Boolean
)

class BeltsViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val contentRepository: ContentRepository
) : ViewModel() {

    // 1. Obtener al usuario actual de forma reactiva
    @OptIn(ExperimentalCoroutinesApi::class)
    private val currentUserStream: Flow<User?> =
        authRepository.getAuthStateStream().flatMapLatest { uid ->
            if (uid != null) {
                userRepository.getUserStream(uid)
            } else {
                flowOf(null)
            }
        }

    // 2. Obtener lista de cinturones de forma reactiva
    private val beltsStream: Flow<List<Belt>> = contentRepository.getBeltsStream()

    // 3. Combinar usuario + cinturones para aplicar lógica de muestra de cinturones
    val beltsUiState: StateFlow<List<BeltItemUiState>> =
        combine(currentUserStream, beltsStream) { user, belts ->
            if (user == null || belts.isEmpty()) return@combine emptyList()

            // Buscar en la lista el cinturón que tiene asignado el usuario
            val userBelt = belts.find { it.id == user.beltId }
            // Si el usuario no tiene cinturón, asumimos orden 0 (Blanco)
            val userBeltOrder = userBelt?.order ?: 0
            
            // Acceso hasta nivel actual + 1
            val maxAllowedOrder = userBeltOrder + 1

            belts.map { belt ->
                BeltItemUiState(
                    belt = belt,
                    isEnabled = belt.order <= maxAllowedOrder
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}

class BeltsViewModelFactory(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val contentRepository: ContentRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BeltsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BeltsViewModel(authRepository, userRepository, contentRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
