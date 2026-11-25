package com.example.domain.repository

import com.example.data.local.FigureEntity
import com.example.data.local.WorkoutLogEntity
import com.example.data.model.CharacterDetailDto
import com.example.data.model.CharacterDto
import com.example.data.model.PlanetDto
import kotlinx.coroutines.flow.Flow

interface DragonBallRepository {
    val figuresFlow: Flow<List<FigureEntity>>
    val workoutsFlow: Flow<List<WorkoutLogEntity>>
    val totalKiGainedFlow: Flow<Long?>

    suspend fun getCharacters(): Result<List<CharacterDto>>
    suspend fun getCharacterDetail(id: Int): Result<CharacterDetailDto>
    suspend fun getPlanets(): Result<List<PlanetDto>>

    suspend fun isFigureInCollection(characterId: Int): Boolean
    suspend fun addFigure(figure: FigureEntity)
    suspend fun removeFigure(figure: FigureEntity)
    suspend fun removeFigureByCharacterId(characterId: Int)

    suspend fun logWorkout(workout: WorkoutLogEntity)
    suspend fun deleteWorkout(workout: WorkoutLogEntity)
}
