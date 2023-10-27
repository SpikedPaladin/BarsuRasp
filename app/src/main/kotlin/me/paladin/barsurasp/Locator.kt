package me.paladin.barsurasp

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import io.ktor.client.HttpClient
import me.paladin.barsurasp.data.UserPreferencesRepository
import me.paladin.barsurasp.ui.viewmodels.MainViewModel
import me.paladin.barsurasp.ui.viewmodels.SettingsViewModel
import me.paladin.barsurasp.usecase.ChangeMainGroup
import me.paladin.barsurasp.usecase.ChangeMonet
import me.paladin.barsurasp.usecase.ChangeTheme
import me.paladin.barsurasp.usecase.ChangeWeek
import me.paladin.barsurasp.usecase.GetAdsWatched
import me.paladin.barsurasp.usecase.GetMainGroup
import me.paladin.barsurasp.usecase.GetMonet
import me.paladin.barsurasp.usecase.GetTheme
import me.paladin.barsurasp.usecase.GetWeek
import me.paladin.barsurasp.usecase.IncrementAdCounter

object Locator {
    private var application: Application? = null
    val client = HttpClient()

    private inline val requireApplication
        get() = application ?: error("Missing call: initWith(application)")

    fun initWith(application: Application) {
        this.application = application
    }

    val mainViewModelFactory
        get() = MainViewModel.Factory(
            getMainGroup = getMainGroup,
            changeMainGroup = changeMainGroup,
            getWeek = getWeek,
            changeWeek = changeWeek
        )

    val settingsViewModelFactory
        get() = SettingsViewModel.Factory(
            getTheme = getTheme,
            changeTheme = changeTheme,
            getMonet = getMonet,
            changeMonet = changeMonet,
            getAdsWatched = getAdsWatched,
            incrementAdCounter = incrementAdCounter
        )

    fun getCacheDir() = requireApplication.cacheDir

    private val getWeek get() = GetWeek(userPreferencesRepository)
    private val changeWeek get() = ChangeWeek(userPreferencesRepository)

    private val getMainGroup get() = GetMainGroup(userPreferencesRepository)
    private val changeMainGroup get() = ChangeMainGroup(userPreferencesRepository)

    private val getAdsWatched get() = GetAdsWatched(userPreferencesRepository)
    private val incrementAdCounter get() = IncrementAdCounter(userPreferencesRepository)

    private val changeTheme get() = ChangeTheme(userPreferencesRepository)
    private val getTheme get() = GetTheme(userPreferencesRepository)

    private val changeMonet get() = ChangeMonet(userPreferencesRepository)
    private val getMonet get() = GetMonet(userPreferencesRepository)

    private val Context.dataStore by preferencesDataStore(name = "user_preferences")

    private val userPreferencesRepository by lazy {
        UserPreferencesRepository(requireApplication.dataStore)
    }
}