package me.paladin.barsurasp.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BusPathViewModel : ViewModel() {
    private val _buses = MutableStateFlow(listOf<Triple<Int, String, Boolean>>())
    val buses = _buses.asStateFlow()
    private val _title = MutableStateFlow("")
    val title = _title.asStateFlow()

    fun addPath(bus: Triple<Int, String, Boolean>) {
        _buses.update {
            _buses.value + bus
        }
    }

    fun setTitle(title: String) {
        _title.update { title }
    }
}