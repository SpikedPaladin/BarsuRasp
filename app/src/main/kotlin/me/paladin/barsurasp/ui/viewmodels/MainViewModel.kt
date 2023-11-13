package me.paladin.barsurasp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.paladin.barsurasp.App
import me.paladin.barsurasp.data.TimetableRepository
import me.paladin.barsurasp.models.Timetable
import me.paladin.barsurasp.utils.getCurrentWeek
import me.paladin.barsurasp.utils.getNextWeek

sealed interface UiState {
    data object Loading : UiState

    data class Success(
        val data: Timetable
    ) : UiState

    data class Error(
        val noGroup: Boolean = false,
        val noTimetable: Boolean = false,
        val noInternet: Boolean = false
    ) : UiState
}

class MainViewModel : ViewModel() {
    private var currentJob: Job? = null
    private val mainGroup = App.prefs.mainGroup.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        null
    )
    val week = App.prefs.week.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        "current"
    )
    val showBuses = App.prefs.showBuses.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        true
    )

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            mainGroup.collect { value ->
                if (value == null)
                    return@collect

                if (value != "") {
                    updateTimetable()
                } else {
                    _uiState.update { UiState.Error(noGroup = true) }
                }
            }
        }
        viewModelScope.launch {
            week.collect {
                updateTimetable()
            }
        }
    }

    fun refreshTimetable() {
        mainGroup.value?.let(TimetableRepository::refreshTimetable)
        updateTimetable()
    }

    fun updateTimetable() {
        _uiState.update { UiState.Loading }

        val apiWeek = if (week.value == "current") getCurrentWeek() else getNextWeek()

        currentJob?.cancel()

        if (mainGroup.value == null)
            return

        currentJob = viewModelScope.launch {
            try {
                val timetable = TimetableRepository.getTimetable(mainGroup.value!!, apiWeek)

                _uiState.update {
                    if (timetable != null)
                        UiState.Success(timetable)
                    else
                        UiState.Error(noTimetable = true)
                }
            } catch (e: Exception) {
                Log.i("NetworkError", "updateTimetable: ${e.message}")
                _uiState.update {
                    UiState.Error(noInternet = true)
                }
            }
        }
    }

    fun changeWeek(week: String) {
        viewModelScope.launch { App.prefs.changeWeek(week) }
    }

    fun setMainGroup(group: String) {
        viewModelScope.launch { App.prefs.changeMainGroup(group) }
    }
}