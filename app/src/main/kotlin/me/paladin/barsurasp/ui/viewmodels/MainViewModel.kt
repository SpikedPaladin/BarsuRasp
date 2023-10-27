package me.paladin.barsurasp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.paladin.barsurasp.data.loaders.StudentLoader
import me.paladin.barsurasp.models.Timetable
import me.paladin.barsurasp.usecase.ChangeMainGroup
import me.paladin.barsurasp.usecase.ChangeWeek
import me.paladin.barsurasp.usecase.GetMainGroup
import me.paladin.barsurasp.usecase.GetWeek
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

class MainViewModel(
    getMainGroup: GetMainGroup,
    private val _changeGroup: ChangeMainGroup,
    getWeek: GetWeek,
    private val _changeWeek: ChangeWeek
) : ViewModel() {
    private var currentJob: Job? = null
    private val mainGroup: StateFlow<String?> = getMainGroup().stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        null
    )
    val week: StateFlow<String> = getWeek().stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        "current"
    )

    private val _timetableFlow = MutableStateFlow<UiState>(UiState.Loading)
    val timetableFlow: StateFlow<UiState> = _timetableFlow.asStateFlow()

    init {
        viewModelScope.launch {
            mainGroup.collect { value ->
                if (value == null)
                    return@collect

                if (value != "") {
                    updateTimetable()
                } else {
                    _timetableFlow.update { UiState.Error(noGroup = true) }
                }
            }
        }
        viewModelScope.launch {
            week.collect {
                updateTimetable()
            }
        }
    }

    fun updateTimetable() {
        _timetableFlow.update { UiState.Loading }

        val apiWeek = if (week.value == "current") getCurrentWeek() else getNextWeek()

        currentJob?.cancel()

        if (mainGroup.value == null)
            return

        currentJob = viewModelScope.launch {
            try {
                val timetable = StudentLoader.getTimetable(mainGroup.value!!, apiWeek)

                _timetableFlow.update {
                    if (timetable != null)
                        UiState.Success(timetable)
                    else
                        UiState.Error(noTimetable = true)
                }
            } catch (e: Exception) {
                Log.i("NetworkError", "updateTimetable: ${e.message}")
                _timetableFlow.update {
                    UiState.Error(noInternet = true)
                }
            }
        }
    }

    fun changeWeek(week: String) {
        viewModelScope.launch { _changeWeek.invoke(week) }
    }

    fun setMainGroup(group: String) {
        viewModelScope.launch { _changeGroup.invoke(group) }
    }

    class Factory(
        private val getMainGroup: GetMainGroup,
        private val changeMainGroup: ChangeMainGroup,
        private val getWeek: GetWeek,
        private val changeWeek: ChangeWeek
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(
                    getMainGroup = getMainGroup,
                    _changeGroup = changeMainGroup,
                    getWeek = getWeek,
                    _changeWeek = changeWeek
                ) as T
            }
            error("Unknown ViewModel class: $modelClass")
        }
    }
}