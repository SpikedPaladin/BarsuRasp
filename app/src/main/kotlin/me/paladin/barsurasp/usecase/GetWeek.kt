package me.paladin.barsurasp.usecase

import kotlinx.coroutines.flow.Flow
import me.paladin.barsurasp.data.UserPreferencesRepository

class GetWeek(private val userPreferencesRepository: UserPreferencesRepository) {
    operator fun invoke(): Flow<String> = userPreferencesRepository.week
}