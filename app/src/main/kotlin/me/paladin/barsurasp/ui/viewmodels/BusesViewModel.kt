package me.paladin.barsurasp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.paladin.barsurasp.App
import me.paladin.barsurasp.data.BusRepository
import me.paladin.barsurasp.models.BusPath

class BusesViewModel : ViewModel() {
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

    val buses = flow {
        emit(BusRepository.getBusDirections())
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        listOf()
    )

    fun selectPath(index: Int) {
        viewModelScope.launch {
            App.prefs.selectPath(index)
        }
    }

    fun addPath(path: BusPath) {
        viewModelScope.launch {
            App.prefs.addPath(path)
        }
    }

    fun deletePath(index: Int) {
        viewModelScope.launch {
            App.prefs.deletePath(index)
        }
    }
}