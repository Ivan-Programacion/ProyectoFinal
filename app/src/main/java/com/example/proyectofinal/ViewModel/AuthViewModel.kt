package com.example.proyectofinal.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.Model.User
import com.example.proyectofinal.Repository.AuthRepository
import com.example.proyectofinal.Repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    object Success : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun registerUser(user: User, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            
            val uid = authRepository.register(user.email, password)
            if (uid != null) {
                // Al ser data class, clonamos el objeto con su nuevo UID
                val userWithId = user.copy(id = uid)
                
                val createdInFirestore = userRepository.createUser(userWithId)
                if (createdInFirestore) {
                    _uiState.value = AuthUiState.Success
                } else {
                    _uiState.value = AuthUiState.Error("Fallo al guardar el usuario en la base de datos.")
                }
            } else {
                _uiState.value = AuthUiState.Error("Fallo al registrar usuario en Firebase Auth.")
            }
        }
    }

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            
            val loginSuccess = authRepository.login(email, password)
            if (loginSuccess) {
                _uiState.value = AuthUiState.Success
            } else {
                _uiState.value = AuthUiState.Error("Credenciales incorrectas o fallo al iniciar sesión.")
            }
        }
    }
}
