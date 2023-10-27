package me.paladin.barsurasp.usecase

import me.paladin.barsurasp.data.UserPreferencesRepository

class ChangeWeek(private val userPreferencesRepository: UserPreferencesRepository) {
    suspend operator fun invoke(week: String) =
        userPreferencesRepository.changeWeek(week)
}