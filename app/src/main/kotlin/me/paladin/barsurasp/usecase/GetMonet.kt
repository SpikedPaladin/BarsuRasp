package me.paladin.barsurasp.usecase

import kotlinx.coroutines.flow.Flow
import me.paladin.barsurasp.data.UserPreferencesRepository

class GetMonet(private val userPreferencesRepository: UserPreferencesRepository) {
    operator fun invoke(): Flow<Boolean> = userPreferencesRepository.monet
}