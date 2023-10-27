package me.paladin.barsurasp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.paladin.barsurasp.data.FacultyRepository
import me.paladin.barsurasp.models.Faculty

sealed interface FacultiesUiState {
    data object Loading : FacultiesUiState

    data class Success(
        val data: List<Faculty>
    ) : FacultiesUiState

    data class Error(
        val noTimetable: Boolean = false
    ) : FacultiesUiState
}

class FacultiesViewModel : ViewModel() {
    private val _groupFlow = MutableStateFlow<FacultiesUiState>(FacultiesUiState.Loading)
    val groupFlow: StateFlow<FacultiesUiState> = _groupFlow.asStateFlow()

    init {
        viewModelScope.launch {
            val groups = FacultyRepository.getFaculties()

            if (groups.isNotEmpty()) {
                _groupFlow.update { FacultiesUiState.Success(groups) }
            } else {
                _groupFlow.update { FacultiesUiState.Error() }
            }
        }
    }
}