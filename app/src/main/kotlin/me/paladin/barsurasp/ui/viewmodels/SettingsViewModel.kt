package me.paladin.barsurasp.ui.viewmodels

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.paladin.barsurasp.glance.TimetableWidget
import me.paladin.barsurasp.models.AppTheme
import me.paladin.barsurasp.usecase.ChangeMonet
import me.paladin.barsurasp.usecase.ChangeTheme
import me.paladin.barsurasp.usecase.GetAdsWatched
import me.paladin.barsurasp.usecase.GetMonet
import me.paladin.barsurasp.usecase.GetTheme
import me.paladin.barsurasp.usecase.IncrementAdCounter

class SettingsViewModel(
    getTheme: GetTheme,
    private val _changeTheme: ChangeTheme,
    getMonet: GetMonet,
    private val _changeMonet: ChangeMonet,
    getAdsWatched: GetAdsWatched,
    private val _incrementAdCounter: IncrementAdCounter
) : ViewModel() {
    val theme: StateFlow<AppTheme> = getTheme().stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        AppTheme.AUTO,
    )
    val monet: StateFlow<Boolean> = getMonet().stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        true
    )
    val adCounter: StateFlow<Int> = getAdsWatched().stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        0
    )

    fun changeTheme(theme: AppTheme) {
        viewModelScope.launch { _changeTheme(theme) }
    }

    fun changeMonet(monet: Boolean) {
        viewModelScope.launch { _changeMonet(monet) }
    }

    fun adWatched() {
        viewModelScope.launch { _incrementAdCounter() }
    }

    fun updateWidgets(context: Context) {
        viewModelScope.launch { TimetableWidget().updateAll(context) }
    }

    class Factory(
        private val getTheme: GetTheme,
        private val changeTheme: ChangeTheme,
        private val getMonet: GetMonet,
        private val changeMonet: ChangeMonet,
        private val getAdsWatched: GetAdsWatched,
        private val incrementAdCounter: IncrementAdCounter
        ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SettingsViewModel(
                    getTheme = getTheme,
                    _changeTheme = changeTheme,
                    getMonet = getMonet,
                    _changeMonet = changeMonet,
                    getAdsWatched = getAdsWatched,
                    _incrementAdCounter = incrementAdCounter
                ) as T
            }
            error("Unknown ViewModel class: $modelClass")
        }
    }
}