package me.paladin.barsurasp.usecase

import me.paladin.barsurasp.data.UserPreferencesRepository
import me.paladin.barsurasp.models.AppTheme

class ChangeTheme(private val userPreferencesRepository: UserPreferencesRepository) {
    suspend operator fun invoke(theme: AppTheme) =
        userPreferencesRepository.changeTheme(theme)
}