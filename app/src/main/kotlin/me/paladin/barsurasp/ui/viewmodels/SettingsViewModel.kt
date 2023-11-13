package me.paladin.barsurasp.ui.viewmodels

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.paladin.barsurasp.App
import me.paladin.barsurasp.glance.TimetableWidget
import me.paladin.barsurasp.models.AppTheme

class SettingsViewModel : ViewModel() {
    val theme = App.prefs.theme.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        AppTheme.AUTO,
    )
    val monet = App.prefs.monet.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        true
    )
    val adCounter = App.prefs.adsWatched.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        0
    )
    val showBuses = App.prefs.showBuses.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        true
    )

    fun changeTheme(theme: AppTheme) {
        viewModelScope.launch { App.prefs.changeTheme(theme) }
    }

    fun changeMonet(monet: Boolean) {
        viewModelScope.launch { App.prefs.changeMonet(monet) }
    }

    fun adWatched() {
        viewModelScope.launch { App.prefs.incrementAdCounter() }
    }

    fun setShowBuses(enabled: Boolean) {
        viewModelScope.launch { App.prefs.setShowBuses(enabled) }
    }

    fun updateWidgets(context: Context) {
        viewModelScope.launch { TimetableWidget().updateAll(context) }
    }
}