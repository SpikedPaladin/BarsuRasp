package me.paladin.barsurasp.usecase

import me.paladin.barsurasp.data.UserPreferencesRepository

class IncrementAdCounter(private val userPreferencesRepository: UserPreferencesRepository) {
    suspend operator fun invoke() = userPreferencesRepository.incrementAdCounter()
}