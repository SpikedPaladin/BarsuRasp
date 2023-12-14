package me.paladin.barsurasp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.paladin.barsurasp.data.BusRepository
import me.paladin.barsurasp.data.BusRepository.isLoaded
import me.paladin.barsurasp.models.BusDirection
import me.paladin.barsurasp.models.BusInfo
import me.paladin.barsurasp.utils.getDurationToNextMinute

class BusViewModel(private val number: Int) : ViewModel() {
    private val _progress = MutableStateFlow(0F)
    val progress = _progress.asStateFlow()
    private val _busState = MutableStateFlow<BusState>(BusState.None)
    val busState = _busState.asStateFlow()
    lateinit var direction: BusDirection

    init {
        viewModelScope.launch {
            direction = BusRepository.getBusDirections().find { it.busNumber == number }!!
            if (direction.isLoaded())
                _busState.update { BusState.Loaded(BusRepository.getBusInfo(number)) }
        }
    }

    fun getStopSchedule(name: String, backward: Boolean, limit: Int) = flow {
        while (true) {
            val info = (_busState.value as BusState.Loaded).info
            val stop = info.getStop(name, backward)

            emit(stop.getNearestTime(limit))
            delay(getDurationToNextMinute())
        }
    }

    fun load(errorCallback: () -> Unit) {
        _busState.update { BusState.Loading }
        viewModelScope.launch {
            try {
                _busState.update {
                    BusState.Loaded(
                        BusRepository.getBusInfo(number) { progress -> _progress.update { progress } }
                    )
                }
            } catch (_: Exception) {
                errorCallback()
                _busState.update { BusState.None }
            }
        }
    }

    fun delete() {
        viewModelScope.launch {
            BusRepository.deleteBusInfo(number)
            _busState.update { BusState.None }
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val number: Int) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return BusViewModel(number) as T
        }
    }
}

sealed interface BusState {
    data object None : BusState
    data object Loading : BusState
    data class Loaded(val info: BusInfo) : BusState
}