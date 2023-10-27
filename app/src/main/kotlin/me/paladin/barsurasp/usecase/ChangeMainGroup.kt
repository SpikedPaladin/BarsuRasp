package me.paladin.barsurasp.usecase

import me.paladin.barsurasp.data.UserPreferencesRepository

class ChangeMainGroup(private val userPreferencesRepository: UserPreferencesRepository) {
    suspend operator fun invoke(group: String) =
        userPreferencesRepository.changeMainGroup(group)
}