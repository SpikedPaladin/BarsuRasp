package me.paladin.barsurasp.ui.viewmodels

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.paladin.barsurasp.App
import me.paladin.barsurasp.data.BusRepository
import me.paladin.barsurasp.models.BusDirection
import me.paladin.barsurasp.models.BusPath

sealed interface BusesUiState {
    data object Loading : BusesUiState

    data class Success(
        val data: List<BusDirection>
    ) : BusesUiState

    data class Error(
        val networkError: Boolean = false
    ) : BusesUiState
}

class BusesViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<BusesUiState>(BusesUiState.Loading)
    val uiState = _uiState.asStateFlow()
    val paths = App.prefs.buses.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        listOf()
    )
    val selectedPath = App.prefs.selectedPath.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        0
    )

    val currentInfo = paths.combineTransform(selectedPath) { list, index ->
        if (list.isEmpty() || index == -1 || list.lastIndex < index) {
            emit(null)
            return@combineTransform
        }

        emit(list[index])
    }.stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        null
    )

    init {
        loadBuses()
    }

    fun refresh() {
        loadBuses(false)
    }

    fun loadBuses(useCache: Boolean = true) {
        viewModelScope.launch {
            _uiState.update { BusesUiState.Loading }
            try {
                val data = BusRepository.getBusDirections(useCache)
                _uiState.update { BusesUiState.Success(data) }
            } catch (_: Exception) {
                _uiState.update { BusesUiState.Error(networkError = true) }
            }
        }
    }

    fun selectPath(index: Int) {
        viewModelScope.launch {
            App.prefs.selectPath(index)
        }
    }

    fun addPath(path: BusPath) {
        viewModelScope.launch {
            if (BusRepository.isLoaded())
                App.prefs.addPath(path)
            else {
                try {
                    BusRepository.getBusDirections()
                    App.prefs.addPath(path)
                } catch (_: Exception) {
                    Toast.makeText(
                        App.application,
                        "Ошибка подключения к сети!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    fun deletePath(index: Int) {
        viewModelScope.launch {
            App.prefs.deletePath(index)
        }
    }
}