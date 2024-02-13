package me.paladin.barsurasp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.paladin.barsurasp.data.ItemsRepository
import me.paladin.barsurasp.models.Department

sealed interface TeachersUiState {
    data object Loading : TeachersUiState

    data class Success(
        val data: List<Department>
    ) : TeachersUiState

    data class Error(
        val networkError: Boolean = false
    ) : TeachersUiState
}

class TeachersViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<TeachersUiState>(TeachersUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        load()
    }

    fun refresh() {
        load(false)
    }

    private fun load(useCache: Boolean = true) {
        _uiState.update { TeachersUiState.Loading }

        viewModelScope.launch {
            _uiState.update {
                try {
                    TeachersUiState.Success(ItemsRepository.getDepartments(useCache))
                } catch (_: Exception) {
                    TeachersUiState.Error(networkError = true)
                }
            }
        }
    }
}