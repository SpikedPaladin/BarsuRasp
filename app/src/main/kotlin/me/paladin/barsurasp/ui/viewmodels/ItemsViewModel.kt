package me.paladin.barsurasp.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.paladin.barsurasp.data.ItemsRepository
import me.paladin.barsurasp.models.Department
import me.paladin.barsurasp.models.Faculty
import me.paladin.barsurasp.models.Item

class ItemsViewModel : ViewModel() {
    private val _groupsUiState = MutableStateFlow<GroupsUiState>(GroupsUiState.Loading)
    private val _teachersUiState = MutableStateFlow<TeachersUiState>(TeachersUiState.Loading)
    val groupsUiState = _groupsUiState.asStateFlow()
    val teachersUiState = _teachersUiState.asStateFlow()

    var searchQuery by mutableStateOf("")
        private set

    val searchResults: StateFlow<List<Item>> =
        snapshotFlow { searchQuery }
            .combine(ItemsRepository.itemsFlow()) { query, items ->
                when {
                    query.isNotEmpty() -> {
                        val searchResult = mutableListOf<Item>()
                        for (item in items)
                            if (item.title.lowercase().contains(query.lowercase()))
                                searchResult += item
                        searchResult
                    }
                    else -> listOf()
                }
            }.stateIn(
                scope = viewModelScope,
                initialValue = listOf(),
                started = SharingStarted.WhileSubscribed(5_000)
            )

    init {
        viewModelScope.launch {
            fetchGroups()
            fetchTeachers()
        }
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery = query
    }

    fun refreshGroups() = viewModelScope.launch {
        fetchGroups(false)
    }

    fun refreshTeachers() = viewModelScope.launch {
        fetchTeachers(false)
    }

    private suspend fun fetchGroups(fromCache: Boolean = true) {
        if (!fromCache)
            _groupsUiState.update { GroupsUiState.Loading }

        _groupsUiState.update {
            try {
                GroupsUiState.Success(ItemsRepository.fetchFaculties(fromCache).first())
            } catch (_: Exception) {
                GroupsUiState.Error(networkError = true)
            }
        }
    }

    private suspend fun fetchTeachers(fromCache: Boolean = true) {
        _teachersUiState.update { TeachersUiState.Loading }

        _teachersUiState.update {
            try {
                TeachersUiState.Success(ItemsRepository.fetchDepartments(fromCache).first())
            } catch (_: Exception) {
                TeachersUiState.Error(networkError = true)
            }
        }
    }
}

sealed interface GroupsUiState {
    data object Loading : GroupsUiState

    data class Success(
        val data: List<Faculty>
    ) : GroupsUiState

    data class Error(
        val networkError: Boolean = false
    ) : GroupsUiState
}

sealed interface TeachersUiState {
    data object Loading : TeachersUiState

    data class Success(
        val data: List<Department>
    ) : TeachersUiState

    data class Error(
        val networkError: Boolean = false
    ) : TeachersUiState
}