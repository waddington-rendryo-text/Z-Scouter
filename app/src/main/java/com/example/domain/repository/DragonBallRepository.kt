package com.example.domain.repository

import com.example.domain.model.ActionFigure
import com.example.domain.model.Character
import com.example.domain.model.CharacterDetail
import com.example.domain.model.Planet
import com.example.domain.model.WorkoutLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface DragonBallRepository {
    val charactersFlow: Flow<List<Character>>
    val planetsFlow: Flow<List<Planet>>
    val figuresFlow: Flow<List<ActionFigure>>
    val workoutsFlow: Flow<List<WorkoutLog>>
    val totalKiGainedFlow: Flow<Long?>
    val isOfflineMode: StateFlow<Boolean>

    suspend fun getCharacters(): Result<List<Character>>
    suspend fun getCharacterDetail(id: Int): Result<CharacterDetail>
    suspend fun getPlanets(): Result<List<Planet>>

    suspend fun isFigureInCollection(characterId: Int): Boolean
    suspend fun addFigure(figure: ActionFigure)
    suspend fun removeFigure(figure: ActionFigure)
    suspend fun removeFigureByCharacterId(characterId: Int)

    suspend fun logWorkout(workout: WorkoutLog)
    suspend fun deleteWorkout(workout: WorkoutLog)
}
