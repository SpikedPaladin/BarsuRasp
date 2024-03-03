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
    val mainGroup = App.prefs.mainGroup.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        null
    )
    val showBuses = App.prefs.showBuses.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        true
    )
    val savedItems = App.prefs.savedItems.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        setOf()
    )

    private val _week = MutableStateFlow(getCurrentWeek())
    val week = _week.asStateFlow()
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            launch {
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
            launch {
                week.collect {
                    updateTimetable()
                }
            }
        }
    }

    fun setWeek(value: String) = _week.update { value }

    fun refreshTimetable() {
        mainGroup.value?.let(TimetableRepository::refreshTimetable)
        updateTimetable()
    }

    fun updateTimetable() {
        if (mainGroup.value == null || mainGroup.value == "")
            return

        _uiState.update { UiState.Loading }

        currentJob?.cancel()

        currentJob = viewModelScope.launch {
            _uiState.update {
                try {
                    val timetable = TimetableRepository.getTimetable(mainGroup.value!!, _week.value)

                    if (timetable != null)
                        UiState.Success(timetable)
                    else
                        UiState.Error(noTimetable = true)
                } catch (e: Exception) {
                    Log.i("NetworkError", "updateTimetable: ${e.message}")
                    UiState.Error(noInternet = true)
                }
            }
        }
    }

    fun saveItem(item: String) {
        viewModelScope.launch { App.prefs.saveItem(item) }
    }

    fun setMainGroup(group: String) {
        viewModelScope.launch { App.prefs.changeMainGroup(group) }
    }
}