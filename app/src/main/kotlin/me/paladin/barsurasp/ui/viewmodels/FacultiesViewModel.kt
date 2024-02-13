package me.paladin.barsurasp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.paladin.barsurasp.data.ItemsRepository
import me.paladin.barsurasp.models.Faculty

sealed interface FacultiesUiState {
    data object Loading : FacultiesUiState

    data class Success(
        val data: List<Faculty>
    ) : FacultiesUiState

    data class Error(
        val networkError: Boolean = false
    ) : FacultiesUiState
}

class FacultiesViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<FacultiesUiState>(FacultiesUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        load()
    }

    fun refresh() {
        load(false)
    }

    private fun load(useCache: Boolean = true) {
        if (!useCache)
            _uiState.update { FacultiesUiState.Loading }

        viewModelScope.launch {
            _uiState.update {
                try {
                    FacultiesUiState.Success(ItemsRepository.getFaculties(useCache))
                } catch (_: Exception) {
                    FacultiesUiState.Error(networkError = true)
                }
            }
        }
    }
}