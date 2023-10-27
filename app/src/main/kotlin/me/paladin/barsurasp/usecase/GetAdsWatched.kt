package me.paladin.barsurasp.usecase

import kotlinx.coroutines.flow.Flow
import me.paladin.barsurasp.data.UserPreferencesRepository

class GetAdsWatched(private val userPreferencesRepository: UserPreferencesRepository) {
    operator fun invoke(): Flow<Int> = userPreferencesRepository.adsWatched
}