package me.paladin.barsurasp.usecase

import kotlinx.coroutines.flow.Flow
import me.paladin.barsurasp.data.UserPreferencesRepository
import me.paladin.barsurasp.models.AppTheme

class GetTheme(private val userPreferencesRepository: UserPreferencesRepository) {
    operator fun invoke(): Flow<AppTheme> = userPreferencesRepository.theme
}