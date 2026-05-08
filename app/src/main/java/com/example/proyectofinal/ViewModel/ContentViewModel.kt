package com.example.proyectofinal.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.Model.Content
import com.example.proyectofinal.Repository.ContentRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

class ContentViewModel(
    private val contentRepository: ContentRepository
) : ViewModel() {

    // ID del cinturón seleccionado
    private val _selectedBeltId = MutableStateFlow<String?>(null)
    val selectedBeltId: StateFlow<String?> = _selectedBeltId.asStateFlow()

    // Cambiar el id del cinturñon seleccionado
    fun setSelectedBeltId(beltId: String) {
        _selectedBeltId.value = beltId
    }

    // ID del contenido seleccionado
    private val _selectedContentId = MutableStateFlow<String?>(null)

    // Cambiar el id del contenido seleccionado
    fun setSelectedContentId(contentId: String) {
        _selectedContentId.value = contentId
    }

    // Traer la lista de contenido de la BD de forma reactiva
    @OptIn(ExperimentalCoroutinesApi::class)
    val contentList: StateFlow<List<Content>> = _selectedBeltId.flatMapLatest { beltId ->
        if (beltId != null) {
            contentRepository.getContentStream(beltId)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
}

class ContentViewModelFactory(
    private val contentRepository: ContentRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ContentViewModel(contentRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
