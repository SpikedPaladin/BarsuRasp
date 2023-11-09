package me.paladin.barsurasp.ui.viewmodels

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.paladin.barsurasp.App
import me.paladin.barsurasp.glance.TimetableWidget
import me.paladin.barsurasp.models.AppTheme

class SettingsViewModel : ViewModel() {
    val theme: StateFlow<AppTheme> = App.preferences.theme.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        AppTheme.AUTO,
    )
    val monet: StateFlow<Boolean> = App.preferences.monet.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        true
    )
    val adCounter: StateFlow<Int> = App.preferences.adsWatched.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        0
    )

    fun changeTheme(theme: AppTheme) {
        viewModelScope.launch { App.preferences.changeTheme(theme) }
    }

    fun changeMonet(monet: Boolean) {
        viewModelScope.launch { App.preferences.changeMonet(monet) }
    }

    fun adWatched() {
        viewModelScope.launch { App.preferences.incrementAdCounter() }
    }

    fun updateWidgets(context: Context) {
        viewModelScope.launch { TimetableWidget().updateAll(context) }
    }
}