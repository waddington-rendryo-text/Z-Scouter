package com.example.domain.usecase

import com.example.domain.model.Planet
import com.example.domain.repository.DragonBallRepository
import kotlinx.coroutines.flow.Flow

class GetPlanetsUseCase(
    private val repository: DragonBallRepository
) {
    fun observe(): Flow<List<Planet>> = repository.planetsFlow

    suspend operator fun invoke(): Result<List<Planet>> = repository.getPlanets()
}
