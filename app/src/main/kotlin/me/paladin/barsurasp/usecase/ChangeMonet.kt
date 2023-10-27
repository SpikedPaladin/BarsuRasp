package me.paladin.barsurasp.usecase

import me.paladin.barsurasp.data.UserPreferencesRepository

class ChangeMonet(private val userPreferencesRepository: UserPreferencesRepository) {
    suspend operator fun invoke(monet: Boolean) =
        userPreferencesRepository.changeMonet(monet)
}